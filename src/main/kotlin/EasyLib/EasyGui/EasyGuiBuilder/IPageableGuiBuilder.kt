package EasyLib.EasyGui.EasyGuiBuilder

import EasyLib.EasyGui.EasyGuiConfig.GuiConfig.GuiConfig
import EasyLib.Utils.DebugLogger
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.type.impl.PageableChestImpl
import taboolib.platform.util.buildItem
/**
 * @author Hareidus
 * @date 2025/9/18
 * 翻页界面构建器
 */
abstract class IPageableGuiBuilder<T>(override val config : GuiConfig, val thisPlayer: Player) : IBuilder(config,thisPlayer) {

    abstract val chestImpl : PageableChestImpl<T>

    abstract fun setupElement ()

    abstract fun elementGenerateItem()

    /**
     *  构建翻页界面，需要注意如下步骤：
     * 1. 创建翻页界面实例
     * 2. 设置布局 setupChest(chestImpl)
     * 3. 为界面加入元素 setUpElement()
     * 4. 设置元素对应物品 elementGenerateItem()
     * 5. 映射图标 mapIconsToFunctions()
     *
     */
    abstract override fun build() : Inventory

    fun elementSlotByKey (key : Char){
        chestImpl.slotsBy(key)
    }

        /**
     * 获取遮挡用途的物品图标
     *
     * 该函数遍历配置中的所有声明字符，查找功能为"baned"的图标配置，
     * 并返回对应的ItemStack对象。如果未找到匹配的图标配置，则抛出异常。
     *
     * @return ItemStack 被禁用的物品图标堆栈
     * @throws IllegalArgumentException 当配置中未定义baned图标时抛出异常
     */
    fun getBanedItem() : ItemStack {
        // 遍历配置中的所有声明字符，查找baned功能的图标
        for (key in config.getDeclareChar()) {
            try {
                val function = config.getIconFunction(key.toString())
                DebugLogger.debug("Processing icon key: $key, function: $function", "icon_mapping")
                if (function == "baned"){
                    config.getKeySection()?.let {
                        itemProvider(it, key, thisPlayer)?.let { itemStack ->
                            return@getBanedItem itemStack
                        }
                    }
                }
            } catch (e: Exception) {
                //DebugLogger.error("Error processing icon key: $key", e)
                DebugLogger.warn("Error processing icon key: $key", "icon_mapping")
            }
        }
        throw IllegalArgumentException("No baned icon found | 未找到baned图标，请在配置中定义，file文件 ${config.getAbsolutePath()}")
    }


    fun setNextIcon(key: Char , pageableChestImpl: PageableChestImpl<T> , banedItem: ItemStack) {
        pageableChestImpl.setNextPage(pageableChestImpl.getFirstSlot(key)) { _, hasNextPage ->
            if (hasNextPage) {
                config.getKeySection()?.let { section ->
                    itemProvider(section, key, thisPlayer)?.let { itemStack ->
                        return@setNextPage itemStack
                    }
                }
                // 只有在无法从配置获取物品时才创建默认物品
                buildItem(Material.STONE) {
                    lore.add("&cError Next Page Icon show item")
                    colored()
                }
            } else {
                banedItem
            }
        }
    }
    fun setLastIcon(key: Char, pageableChestImpl: PageableChestImpl<T>, banedItem: ItemStack) {
        pageableChestImpl.setPreviousPage(pageableChestImpl.getFirstSlot(key)) { _, hasPreviousPage ->
            if (hasPreviousPage) {
                config.getKeySection()?.let { section ->
                    itemProvider(section, key, thisPlayer)?.let { itemStack ->
                        return@setPreviousPage itemStack
                    }
                }
                buildItem(Material.STONE) {
                    lore.add("&cError Last Page Icon show item")
                    colored()
                }
            } else {
                banedItem
            }
        }
    }
}