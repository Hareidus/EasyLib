package EasyLib.EasyGui.EasyGuiConfig.GuiConfig

import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.info
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.type.Chest
import taboolib.platform.compat.replacePlaceholder
import taboolib.platform.util.buildItem
import java.util.stream.Collectors


/**
 * 定义一个开放的接口，用于GUI相关的操作和数据获取
 */
interface GuiInterface {

    fun getName() : String


    /**
     * 获取一个字符串列表，通常用于在GUI中展示信息或数据
     *
     * @return 包含字符串信息的列表
     */
    fun getMap() : List<String>

    /**
     * 获取一个配置部分，可能包含GUI的配置信息
     *
     * @return 可能包含GUI配置信息的ConfigurationSection，如果未定义则返回null
     */
    fun getKeySection ()  : ConfigurationSection?

    /**
     * 获取已使用的字符列表，这些字符可能在GUI中有特殊意义或用途
     *
     * @return 包含已使用字符的列表
     */
    fun getUsedChar ()  : List<Char>

    /**
     * 获取声明的字符列表，这些字符可能用于标识或定义GUI中的元素
     *
     * @return 包含声明字符的列表
     */
    fun getDeclareChar ()  : List<Char>

    /**
     * 获取GUI的标题，用于标识或展示GUI的信息
     *
     * @return GUI的标题字符串
     */
    fun getTitle ()  : String

    fun getAbsolutePath():String

    fun getIconFunction(key : String):String

    fun reloadConfig(func : () -> Unit)

    fun getSound(icons: String): Sound?

    fun getOpenSound(): Sound?

    fun getCloseSound(): Sound?

    companion object{
        fun getEffectChar (section : ConfigurationSection?)  : List<Char> {
            return section?.getKeys(false)?.stream()
                ?.map { it.toCharArray() }
                ?.map { it[0]}
                ?.collect(Collectors.toList()) ?: listOf()
        }
        fun getUsedChar (map : List<String>) : List<Char> {
            return map.flatMap { it.toList() }.distinct()
        }
        fun getItemFromConfig(section: ConfigurationSection?, key: Char,player : Player): ItemStack? {
            // 检查是否启用了调试模式
            val itemSection = section?.getConfigurationSection(key.toString()) ?: run {
                info("配置项没有找到Key: $key")
                return null
            }
            val materialString = itemSection.getString("Material") ?.uppercase()?.replace(" ","_")?: run {
                info("这个材质标签不可用，请检查你的配置文件。Key: $key")
                return null
            }
            val material = XMaterial.matchXMaterial(materialString).orElse(XMaterial.STONE) ?: run {
                info("这个材质无效，请检查你的配置文件。Key: $key, value: $materialString")
                return null
            }
            val itemName = itemSection.getString("Name")
            val itemLore = itemSection.getStringList("Lore")
            return try {
                buildItem(material) {
                    this.name = itemName
                    for (text in itemLore) {
                        val newtext = text.replacePlaceholder(player)
                        lore.add(newtext)
                    }
                    colored()
                }
            } catch (e: Exception) {
                error("无法构建物品: $key 错误项: ${e.message}")
                e.printStackTrace()
                null
            }
        }

    }
}


interface IGui {
    fun build() : Inventory
    var InvImpl : Chest
}



// 抽象GUI构建器接口
interface GuiBuilderInterface {
    fun open(player: Player)
}

interface SoundManager {
    fun playSound(player: Player, soundName: Sound?)
}