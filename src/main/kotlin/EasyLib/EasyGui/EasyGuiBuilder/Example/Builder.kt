package EasyLib.EasyGui.EasyGuiBuilder.Example

import EasyLib.EasyGui.EasyGuiBuilder.IBuilder
import EasyLib.EasyGui.EasyGuiBuilder.IPageableGuiBuilder
import EasyLib.EasyGui.EasyGuiConfig.GuiConfig.GuiConfig
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.info
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Chest
import taboolib.module.ui.type.impl.ChestImpl
import taboolib.module.ui.type.impl.PageableChestImpl
import taboolib.platform.util.buildItem

class Builder(override val config: GuiConfig, thisPlayer: Player) : IPageableGuiBuilder<Player>(config,thisPlayer) {
    override val chestImpl: PageableChestImpl<Player> by lazy {
        PageableChestImpl(config.getTitle())
    }

    override fun setupElement() {
        trackStep("setupElement"){
            chestImpl.elements {
                Bukkit.getOnlinePlayers().map { it }.toList()
            }
        }
    }

    override fun elementGenerateItem() {
        trackStep("elementGenerateItem"){
            chestImpl.onGenerate { player, element, index, slot ->
                buildItem(XMaterial.PLAYER_HEAD){
                    name = "&a${element.name}"
                }
            }
        }
    }


    override fun build(): Inventory {
        trackStep("build"){
            setupChest(chestImpl)
            setupElement()
            elementGenerateItem()
            mapIconsToFunctions()
        }
        return chestImpl.build()
    }

    override fun open() {
        trackStep("open"){
            overrideTitle(config.getTitle(),chestImpl)
            thisPlayer.openMenu( build())
            printBuildReport()
        }
    }

    override fun mapIconsToFunctions() {
        trackStep("mapIconsToFunctions"){
            mapIconsToFunctionWay{ key, function ->
                when(function){
                    "playerInfo" -> setPlayerInfoIcon(key)
                    "player" -> elementSlotByKey(key)
                    "next" -> setNextIcon(key,chestImpl,getBanedItem())
                    "last" -> setLastIcon(key,chestImpl,getBanedItem())
                    else -> setDefaultIcon(key,chestImpl)
                }
            }
        }
    }

    private fun setPlayerInfoIcon(charKey: Char) {
        setIcon(charKey,chestImpl){ key, itemStack ->
            chestImpl.set(key,itemStack){
                isCancelled = true
                elementSlotByKey(key)
                info("hello world!")
            }
        }
    }
}