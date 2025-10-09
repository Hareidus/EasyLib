package EasyLib.EasyGui.EasyGuiBuilder.Example

import EasyLib.EasyGui.EasyGuiBuilder.IPageableGuiBuilder
import EasyLib.EasyGui.EasyGuiConfig.GuiConfig.GuiConfig
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import taboolib.common.platform.function.info
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.platform.util.buildItem

class Builder(override val config: GuiConfig, thisPlayer: Player) : IPageableGuiBuilder<Player>(config,thisPlayer) {

    override fun setupElement() {
        trackStep("setupElement"){
            getCustomChestImpl().elements {
                Bukkit.getOnlinePlayers().map { it }.toList()
            }
        }
    }

    override fun elementGenerateItem() {
        trackStep("elementGenerateItem"){
            getCustomChestImpl().onGenerate { player, element, index, slot ->
                buildItem(XMaterial.PLAYER_HEAD){
                    name = "&a${element.name}"
                }
            }
        }
    }

    override fun open() {
        buildAsyncAndOpen {  }
    }

    override fun mapIconsToFunctions() {
        trackStep("mapIconsToFunctions"){
            mapIconsToFunctionWay{ key, function ->
                when(function){
                    "playerInfo" -> setPlayerInfoIcon(key)
                    "player" -> elementSlotByKey(key)
                    "next" -> setNextIcon(key)
                    "last" -> setLastIcon(key)
                    else -> setDefaultIcon(key)
                }
            }
        }
    }

    private fun setPlayerInfoIcon(charKey: Char) {
        setIcon(charKey){ key, itemStack ->
            getCustomChestImpl().set(key,itemStack){
                isCancelled = true
                elementSlotByKey(key)
                info("hello world!")
            }
        }
    }
}