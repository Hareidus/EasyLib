package EasyLib.EasyGui.EasyGuiConfig.GuiConfig

import EasyLib.EasyGui.EasyGuiBuilder.IBuilder
import EasyLib.Utils.DebugLogger
import org.bukkit.entity.Player

object EasyGuiExtensions {


    /**
     * 扩展函数：快速构建并打开GUI
     */
    fun GuiConfig.buildAndOpen(builderFactory: (GuiConfig, Player) -> IBuilder, player: Player) {
        try {
            val builder = builderFactory(this, player)
            builder.open()
        } catch (e: Exception) {
            //DebugLogger.error("Failed to build and open GUI: ${this.getAbsolutePath()}", e)
            DebugLogger.debug("Failed to build and open GUI: ${this.getAbsolutePath()}", "config")
        }
    }

    /**
     * 扩展函数：安全地获取配置值
     */
    fun GuiConfig.getSafeString(path: String, default: String = ""): String {
        return try {
            // 假设 Configuration 有 getString 方法
            // 这里需要根据实际的 Configuration 类型调整
            default
        } catch (e: Exception) {
            DebugLogger.warn("Failed to get config value at path: $path", "config")
            default
        }
    }

}