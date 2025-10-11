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
import taboolib.expansion.submitChain
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.chat.colored
import taboolib.module.ui.ClickEvent
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Chest
import taboolib.module.ui.type.impl.ChestImpl

/**
 * # GUI构建器系统 (GUI Builder System)
 *
 * ## 模块目的 (Purpose)
 * 此模块提供了一个灵活的GUI构建框架，支持普通界面和翻页界面的创建。
 * 它通过配置驱动的方式，允许开发者通过配置文件定义GUI布局、图标和功能，
 * 并提供了统一的构建和事件处理机制。该系统支持异步构建、性能跟踪和错误处理。
 *
 * This module provides a flexible GUI building framework that supports the creation of both normal and paginated interfaces.
 * It allows developers to define GUI layouts, icons, and functions through configuration files via a configuration-driven approach,
 * and provides unified building and event handling mechanisms. The system supports asynchronous building, performance tracking, and error handling.
 *
 * ## 如何添加新的GUI构建器 (How to Add New GUI Builders)
 * 1. **继承基础构建器**: 创建一个类继承自 `IBuilder` 或其子类 `INormalGuiBuilder` / `IPageableGuiBuilder`
 *    - Inherit from the base builder: Create a class that inherits from `IBuilder` or its subclasses `INormalGuiBuilder` / `IPageableGuiBuilder`
 *
 * 2. **实现抽象方法**: 实现 `build()`, `open()`, `mapIconsToFunctions()` 等抽象方法
 *    - Implement abstract methods: Implement abstract methods like `build()`, `open()`, `mapIconsToFunctions()`
 *
 * 3. **定义界面类型**: 根据需要选择普通界面或翻页界面
 *    - Define interface type: Choose between normal interface or paginated interface as needed
 *
 * 4. **配置映射**: 在 `mapIconsToFunctions()` 中实现图标到功能的映射逻辑
 *    - Configuration mapping: Implement icon-to-function mapping logic in `mapIconsToFunctions()`
 *
 * 5. **事件处理**: 为GUI元素添加点击事件处理逻辑
 *    - Event handling: Add click event handling logic for GUI elements
 *
 * ## 适用场景 (Applicable Scenarios)
 * - **插件界面**: 为Bukkit插件创建复杂的交互式界面
 *   - Plugin interfaces: Creating complex interactive interfaces for Bukkit plugins
 * - **配置驱动界面**: 通过配置文件快速创建GUI，无需硬编码
 *   - Configuration-driven interfaces: Quickly creating GUIs through configuration files without hardcoding
 * - **翻页菜单**: 创建包含大量项目的可翻页菜单
 *   - Paginated menus: Creating paginated menus with large numbers of items
 * - **多玩家界面**: 为不同玩家提供个性化的界面体验
 *   - Multi-player interfaces: Providing personalized interface experiences for different players
 *
 * ## 一些吐槽 (Some Complaints)
 * - 代码结构较为复杂，继承层次较深，学习成本较高
 *   - Code structure is relatively complex with deep inheritance hierarchies, resulting in a high learning cost
 * - 异步构建的错误处理机制不够完善
 *   - Asynchronous building error handling mechanism is not comprehensive enough
 * - 配置文件格式和结构需要开发者自行维护文档
 *   - Configuration file format and structure require developers to maintain documentation themselves
 * - Sound效果管理较为简单，缺少音效池和音效优先级管理
 *   - Sound effect management is relatively simple, lacking sound pools and priority management
 * - 性能跟踪功能虽然存在，但输出信息可能过于冗长
 *   - Although performance tracking functionality exists, output information may be overly verbose
 */


abstract class IBuilder(open val config: GuiConfig, private val thisPlayer: Player) {
    /**
     * 构建步骤数据类
     * 用于记录GUI构建过程中的各个步骤及其执行信息
     *
     * Data class for build steps
     * Used to record information about various steps in the GUI building process
     */
    protected data class BuildStep(val name: String, val duration: Long, val success: Boolean)

    /**
     * 构建步骤列表
     * 记录GUI构建过程中的所有步骤
     *
     * List of build steps
     * Records all steps in the GUI building process
     */
    protected val buildSteps = mutableListOf<BuildStep>()

    /**
     * 物品提供者函数
     * 从配置中获取物品的通用函数
     *
     * Item provider function
     * Generic function to retrieve items from configuration
     */
    val itemProvider: (ConfigurationSection, String, Player) -> ItemStack? = { section, key, player ->
        getItemFromConfig(section, key, player)
    }

    /**
     * 箱子界面实现对象
     * 具体的GUI实现对象，由子类提供
     *
     * Chest interface implementation object
     * Specific GUI implementation object, provided by subclasses
     */
    abstract val chestImpl : ChestImpl

    private val soundManager = DefaultSoundManager()

    /**
     * 构建GUI的方法
     * 子类必须实现此方法来定义具体的GUI构建逻辑
     *
     * Method to build the GUI
     * Subclasses must implement this method to define specific GUI building logic
     */
    abstract fun build(otherFunc : () -> Unit) : Inventory

    /**
     * 打开GUI的方法
     * 子类必须实现此方法来定义GUI的打开逻辑
     *
     * Method to open the GUI
     * Subclasses must implement this method to define GUI opening logic
     */
    abstract fun open()

    /**
     * 将图标映射到功能的方法
     * 子类必须实现此方法来定义图标到功能的映射逻辑
     *
     * Method to map icons to functions
     * Subclasses must implement this method to define icon-to-function mapping logic
     */
    abstract fun mapIconsToFunctions()


    /**
     * 构建并打开GUI的便捷方法
     * 封装了构建和打开GUI的完整流程
     *
     * Convenient method to build and open GUI
     * Encapsulates the complete process of building and opening GUI
     */
    open fun buildAndOpen(buildFunc : () ->  Unit): IBuilder {
        try {
            thisPlayer.openMenu(build(buildFunc))
        } catch (e: Exception) {
            DebugLogger.debug("Failed to build and open GUI: ${e.message}", "gui_build", "EasyLib")
            throw GuiBuildException("GUI构建和打开失败", config.getAbsolutePath(), e)
        }
        return this
    }

    /**
     * 异步构建GUI
     * 在后台线程构建GUI，避免阻塞主线程
     *
     * Asynchronously build GUI
     * Build GUI in background thread to avoid blocking main thread
     */
    open fun buildAsyncAndOpen(buildOtherFunc : () ->  Unit) {
        submitChain {
            val inv = async {
                try {
                    build(buildOtherFunc)
                } catch (e: Exception) {
                    DebugLogger.debug("Failed to build GUI asynchronously: ${e.message}", "gui_build", "EasyLib")
                    throw GuiBuildException("GUI构建失败", config.getAbsolutePath(), e)
                }
            }
            sync {
                thisPlayer.openMenu(inv)
            }
        }
        //DebugLogger.debug("Failed to build GUI asynchronously: ${e.message}", "gui_build", "EasyLib")
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
        val declareChars = config.getDeclareChar() // 缓存结果，避免重复调用

        // 使用 forEachIndexed 避免额外的索引计算
        declareChars.forEach { key ->
            try {
                val function = config.getIconFunction(key.toString())
                DebugLogger.debug("Processing icon key: $key, function: $function", "icon_mapping")
                func(key, function)
            } catch (e: Exception) {
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

    /**
     * 设置GUI布局
     * 根据配置文件中的布局信息设置GUI的布局
     *
     * Set GUI layout
     * Set the GUI layout based on layout information in the configuration file
     */
    open fun setupChest() {
        trackStep("setup_chest") {
            try {
                DebugLogger.debug("Setting up chest GUI ${config.getFile()?.absolutePath}", "gui_setup")
                chestImpl.map(*config.getMap().toTypedArray())
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
    open fun setDefaultIcon(key : Char){
        try {
            DebugLogger.debug("Setting default icon for key: $key", "default_icon")
            config.getKeySection()?.let { section ->
                itemProvider(section, key.toString(), thisPlayer)?.let { itemStack ->
                    chestImpl.set(key, itemStack) {
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

    private fun setIconWithSound(key: Char, itemStack: ItemStack) {
        chestImpl.set(key, itemStack) {
            isCancelled = true
            try {
                soundManager.playSound(thisPlayer, config.getSound(key.toString()))
            } catch (e: Exception) {
                DebugLogger.debug("Error processing icon key: $key", "mapIconsToFunction", "EasyLib")
            }
        }
    }


    /**
     * 设置自定义图标
     *
     * 该函数根据给定的键值从配置中获取相应的物品图标，并将其设置到指定的箱子界面中。
     * 与默认图标不同，此函数允许开发者传入自定义的图标处理函数。
     *
     * @param key 图标在GUI中的位置键值
     * @param iconFuc 自定义的图标处理函数，接收键值和物品堆栈作为参数
     */
    fun setIcon(key : Char, iconFuc : (key : Char,itemStack : ItemStack) -> Unit){
        try {
            DebugLogger.debug("Setting custom icon for key: $key", "default_icon")
            config.getKeySection()?.let { section ->
                itemProvider(section, key.toString(), thisPlayer)?.let { itemStack ->
//                    chestImpl.set(key, itemStack) {
//
//                    }
                    iconFuc(key,itemStack)
                    setIconWithSound(key, itemStack)
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

    /**
     * 跟踪GUI构建步骤的执行时间和结果
     *
     * @param stepName 步骤名称，用于标识当前执行的构建步骤
     * @param step 要执行的构建步骤逻辑，这是一个无参数无返回值的lambda表达式
     */
    protected fun trackStep(stepName: String, step: () -> Unit) {
        // 如果调试未启用，直接执行步骤而不进行跟踪
        if (!DebugConfig.enabled) {
            step()
            return
        }

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
}