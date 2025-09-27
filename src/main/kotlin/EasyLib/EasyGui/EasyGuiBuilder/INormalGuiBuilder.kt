package EasyLib.EasyGui.EasyGuiBuilder

import EasyLib.EasyGui.EasyGuiConfig.GuiConfig.GuiConfig
import EasyLib.Utils.DebugLogger
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import taboolib.module.configuration.ConfigFile
import taboolib.module.ui.type.impl.ChestImpl
import taboolib.module.ui.type.impl.PageableChestImpl

/**
 * @author Hareidus
 * @date 2025/9/18
 *  普通界面构建器
 */

abstract class INormalGuiBuilder(override val config : GuiConfig, thisPlayer: Player) : IBuilder(config,thisPlayer) {
    override val chestImpl : ChestImpl by lazy {
        ChestImpl(config.getTitle())
    }
    fun getCustomChestImpl() :ChestImpl {
        return chestImpl
    }
    /**
     *  构建普通界面，需要注意如下步骤：
     * 1. 创建翻页界面实例
     * 2. 设置布局 setupChest(chestImpl)
     * 3. 映射图标 mapIconsToFunctions()
     *
     */
     override fun build(otherFunc : () -> Unit): Inventory {
         setupChest()
         mapIconsToFunctions()
        otherFunc()
         return chestImpl.build()
     }
}