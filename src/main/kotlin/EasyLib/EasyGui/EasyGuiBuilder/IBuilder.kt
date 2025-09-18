package EasyLib.EasyGui.EasyGuiBuilder

import EasyLib.Configs.DebugConfig
import EasyLib.EasyGui.EasyGuiConfig.GuiConfig.GuiConfig
import EasyLib.EasyGui.EasyGuiConfig.GuiConfig.GuiInterface.Companion.getItemFromConfig
import EasyLib.EasyGui.Exceptions.GuiBuildException
import EasyLib.Managers.Sound.DefaultSoundManager
import EasyLib.Utils.DebugLogger
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.chat.colored
import taboolib.module.ui.ClickEvent
import taboolib.module.ui.type.Chest
import taboolib.module.ui.type.impl.ChestImpl

abstract class IBuilder(open val config: GuiConfig, private val thisPlayer: Player) {
    protected data class BuildStep(val name: String, val duration: Long, val success: Boolean)
    protected val buildSteps = mutableListOf<BuildStep>()
    val itemProvider: (ConfigurationSection, Char, Player) -> ItemStack? = { section, key, player ->
        getItemFromConfig(section, key, player)
    }

        /**
     * 跟踪GUI构建步骤的执行时间和结果
     *
     * @param stepName 步骤名称，用于标识当前执行的构建步骤
     * @param step 要执行的构建步骤逻辑，这是一个无参数无返回值的lambda表达式
     */
    protected fun trackStep(stepName: String, step: () -> Unit) {
        // 记录步骤开始执行的时间
        val startTime = System.currentTimeMillis()
        var success = true

        try {
            // 执行构建步骤
            step()
        } catch (e: Exception) {
            // 如果步骤执行过程中发生异常，标记为失败并重新抛出异常
            success = false
            throw e
        } finally {
            // 计算步骤执行耗时并记录构建步骤信息
            val duration = System.currentTimeMillis() - startTime
            buildSteps.add(BuildStep(stepName, duration, success))
            DebugLogger.debug("GUI构建步骤 '$stepName' 耗时: ${duration}ms, 结果: ${if (success) "成功" else "失败"}", "gui_build")
        }
    }


    /**
     * 打印构建性能报告
     */
    protected fun printBuildReport() {
        if (!DebugConfig.enabled) return
        val totalDuration = buildSteps.sumOf { it.duration }
        DebugLogger.debug("=== GUI构建性能报告 ===", "gui_build")
        buildSteps.forEach { step ->
            val percentage = if (totalDuration > 0) (step.duration.toDouble() / totalDuration * 100).toInt() else 0
            DebugLogger.debug("  ${step.name}: ${step.duration}ms (${percentage}%)", "gui_build")
        }
        DebugLogger.debug("总耗时: ${totalDuration}ms", "gui_build")
    }

    private val soundManager = DefaultSoundManager()

    abstract fun build() : Inventory
    abstract fun open()
    abstract fun mapIconsToFunctions()


    /**
     * 构建并打开GUI的便捷方法
     */
    open fun buildAndOpen(): IBuilder {
        try {
            build()
            open()
        } catch (e: Exception) {
            DebugLogger.debug("Failed to build and open GUI: ${e.message}", "gui_build", "EasyLib")
            throw GuiBuildException("GUI构建和打开失败", config.getAbsolutePath(), e)
        }
        return this
    }

    /**
     * 异步构建GUI
     */
    open fun buildAsync(callback: (Inventory) -> Unit) {
        // 使用TabooLib的异步任务
        submit(async = true) {
            try {
                val inventory = build()
                submit {
                    callback(inventory)
                }
            } catch (e: Exception) {
                DebugLogger.debug("Failed to build GUI asynchronously: ${e.message}", "gui_build", "EasyLib")
            }
        }
    }




    /**
     * 将图标映射到功能函数
     *
     * 该方法遍历配置中声明的所有字符键，获取每个键对应的功能字符串，
     * 并通过传入的函数参数进行处理。在处理过程中会记录调试日志，并处理可能发生的异常。
     *
     * @param func 用于处理键值对的函数，接收两个参数：
     *             key: Char - 图标的键字符
     *             function: String - 图标对应的功能字符串
     */
// 改进后
    open fun mapIconsToFunctionWay(func: (key: Char, function: String) -> Unit) {
        DebugLogger.debug("Mapping icons to functions ${config.getFile()?.absolutePath}", "icon_mapping")

        val errors = mutableListOf<Exception>()
        for (key in config.getDeclareChar()) {
            try {
                val function = config.getIconFunction(key.toString())
                DebugLogger.debug("Processing icon key: $key, function: $function", "icon_mapping")
                func(key, function)
            } catch (e: Exception) {
                //DebugLogger.error("Error processing icon key: $key", e)
                DebugLogger.debug("Error processing icon key: $key", "mapIconsToFunction", "EasyLib")
                errors.add(e)
            }
        }

        DebugLogger.debug("Icon mapping completed with ${errors.size} errors", "icon_mapping")

        if (errors.isNotEmpty()) {
            val combinedMessage = "GUI构建过程中遇到${errors.size}个错误，查看日志获取详细信息"
            throw GuiBuildException(
                combinedMessage,
                config.getFile().absolutePath,
                Exception(errors.first()).apply {
                    errors.drop(1).forEach { addSuppressed(it) }
                }
            )
        }
    }
    private fun setIconWithSound(key: Char, itemStack: ItemStack, chest : ChestImpl) {
        chest.set(key, itemStack) {
            isCancelled = true
            try {
                soundManager.playSound(thisPlayer, config.getSound(key.toString()))
            } catch (e: Exception) {
                DebugLogger.debug("Error processing icon key: $key", "mapIconsToFunction", "EasyLib")
            }
        }
    }
    open fun setupChest(impl: ChestImpl) {
        trackStep("setup_chest") {
            try {
                DebugLogger.debug("Setting up chest GUI ${config.getFile()?.absolutePath}", "gui_setup")
                impl.map(*config.getMap().toTypedArray())
            } catch (e: Exception) {
                DebugLogger.debug("Error setting up chest GUI", "gui_setup", "EasyLib")
                throw GuiBuildException("Gui构建异常, 流程: 布局 , 请检查配置", config.getFile().absolutePath, e)
            }
        }
    }

       /**
     * 设置默认图标
     *
     * 该函数根据给定的键值从配置中获取相应的物品图标，并将其设置到指定的箱子界面中。
     * 同时为图标设置点击事件，当玩家点击时会播放相应的声音效果。
     *
     * @param key 图标在GUI中的位置键值
     * @param chest 目标箱子界面实例
     */
    open fun setDefaultIcon(key : Char , chest : ChestImpl){
        try {
            DebugLogger.debug("Setting default icon for key: $key", "default_icon")
            config.getKeySection()?.let { section ->
                itemProvider(section, key, thisPlayer)?.let { itemStack ->
                    chest.set(key, itemStack) {
                        isCancelled = true
                        try {
                            soundManager.playSound(thisPlayer, config.getSound(key.toString()))
                        } catch (e: Exception) {
                           // DebugLogger.error("Error playing sound for default icon key: $key", e)
                            DebugLogger.debug("Error playing sound for default icon key: $key", "default_icon", "EasyLib")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            //DebugLogger.error("Error setting default icon for key: $key", e)
            DebugLogger.debug("Error setting default icon for key: $key", "default_icon", "EasyLib")
        }
    }

    fun setIcon(key : Char , chest : ChestImpl , iconFuc : (key : Char,itemStack : ItemStack) -> Unit){
        try {
            DebugLogger.debug("Setting default icon for key: $key", "default_icon")
            config.getKeySection()?.let { section ->
                itemProvider(section, key, thisPlayer)?.let { itemStack ->
                    //chest.set(key, itemStack) {
                    iconFuc(key,itemStack)
                    setIconWithSound(key, itemStack, chest)
                }
            }
        } catch (e: Exception) {
            //DebugLogger.error("Error setting default icon for key: $key", e)
            DebugLogger.debug("Error setting default icon for key: $key", "default_icon", "EasyLib")
        }
    }
        /**
         * 重写GUI标题
         *
         * @param newTitle 新的标题字符串
         * @param chest 需要更新标题的Chest对象
         */
        fun overrideTitle(newTitle : String,chest : Chest){
            chest.updateTitle(newTitle.colored())
        }
}