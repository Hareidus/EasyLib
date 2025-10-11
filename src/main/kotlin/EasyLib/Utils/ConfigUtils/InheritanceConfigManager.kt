package EasyLib.Utils.ConfigUtils
/**
 * # 通用继承配置管理器 (Generic Inheritance Configuration Manager)
 *
 * ## 模块目的 (Purpose)
 * 此模块旨在提供一个通用的配置继承机制，允许配置项之间建立继承关系。
 * 通过继承，子配置可以继承父配置的所有属性，并可以选择性地覆盖或添加新的属性。
 * 这在需要建立层级配置结构的场景中非常有用，比如权限系统、物品组、角色系统等。
 *
 * This module aims to provide a generic configuration inheritance mechanism that allows inheritance relationships between configuration items.
 * Through inheritance, child configurations can inherit all properties from parent configurations and optionally override or add new properties.
 * This is very useful in scenarios requiring hierarchical configuration structures, such as permission systems, item groups, role systems, etc.
 *
 * ## 如何添加新的管理配置项 (How to Add New Managed Configuration Items)
 * 1. **定义配置项类**: 创建一个数据类实现 `InheritanceConfigManager.ConfigItem` 接口
 *    - Define a data class that implements the `InheritanceConfigManager.ConfigItem` interface
 *
 * 2. **指定继承关系**: 在配置项中使用 `extend` 属性指定要继承的父配置项ID列表
 *    - Specify the list of parent configuration item IDs to inherit from using the `extend` property in the configuration item
 *
 * 3. **实例化管理器**: 创建 `InheritanceConfigManager<YourConfigType>` 实例
 *    - Instantiate `InheritanceConfigManager<YourConfigType>`
 *
 * 4. **注册配置项**: 使用 `addConfig(id, config)` 方法添加配置项
 *    - Add configuration items using the `addConfig(id, config)` method
 *
 * 5. **处理继承**: 调用 `handleInheritance(getIdFunc, getExtendListFunc, mergeFunc)` 方法处理继承关系
 *    - Process inheritance relationships by calling the `handleInheritance(getIdFunc, getExtendListFunc, mergeFunc)` method
 *
 * ## 适用场景 (Applicable Scenarios)
 * - **权限系统**: 用户角色可以继承基础权限，并添加特定权限
 *   - Permission systems: User roles can inherit base permissions and add specific permissions
 * - **物品配置**: 特殊物品组可以继承普通物品组的物品，并添加独有的物品
 *   - Item configurations: Special item groups can inherit items from common item groups and add exclusive items
 * - **角色系统**: 高级角色可以继承低级角色的能力，并扩展新的能力
 *   - Role systems: Advanced roles can inherit abilities from lower-level roles and expand new abilities
 * - **配置模板**: 基础配置模板可以被特定配置继承并定制
 *   - Configuration templates: Basic configuration templates can be inherited and customized by specific configurations
 *
 * ## 一些吐槽 (Some Complaints)
 * - 继承层级过深可能导致性能问题，需要谨慎设计继承结构
 *   - Deep inheritance hierarchies can cause performance issues, so inheritance structures need to be designed carefully
 * - 循环继承是一个常见陷阱，本模块已内置检测机制
 *   - Circular inheritance is a common pitfall, and this module has built-in detection mechanisms
 * - 合并逻辑需要开发者自行实现，确保合并行为符合预期
 *   - Merging logic needs to be implemented by the developer to ensure the merging behavior meets expectations
 * - 错误的继承配置可能导致意外的结果，建议在生产环境使用前充分测试
 *   - Incorrect inheritance configurations can lead to unexpected results, so thorough testing is recommended before using in production
 */
class InheritanceConfigManager<T : Any> {
    /**
     * 配置项接口，需要实现继承功能的配置类需要实现此接口
     * Interface for configuration items that require inheritance functionality
     */
    interface ConfigItem {
        /**
         * 配置项唯一标识符
         * Unique identifier for the configuration item
         */
        val id: String

        /**
         * 继承的配置项ID列表
         * List of configuration item IDs to inherit from
         */
        val extend: List<String>
    }

    private val configMap = mutableMapOf<String, T>()

    /**
     * 添加配置项
     * Add a configuration item
     *
     * @param id 配置项ID / Configuration item ID
     * @param config 配置项对象 / Configuration item object
     */
    fun addConfig(id: String, config: T) {
        configMap[id] = config
    }

    /**
     * 获取所有配置项
     * Get all configuration items
     *
     * @return 包含所有配置项的Map / Map containing all configuration items
     */
    fun getAllConfigs(): Map<String, T> = configMap.toMap()

    /**
     * 获取特定配置项
     * Get a specific configuration item
     *
     * @param id 配置项ID / Configuration item ID
     * @return 配置项对象，如果不存在则返回null / Configuration item object, or null if not found
     */
    fun getConfig(id: String): T? = configMap[id]

    /**
     * 处理继承关系
     * Process inheritance relationships
     *
     * @param getIdFunc 从配置项中提取ID的函数 / Function to extract ID from configuration item
     * @param getExtendListFunc 从配置项中提取继承列表的函数 / Function to extract inheritance list from configuration item
     * @param mergeFunc 合并父配置和子配置的函数 / Function to merge parent and child configurations
     */
    fun handleInheritance(
        getIdFunc: (T) -> String,
        getExtendListFunc: (T) -> List<String>,
        mergeFunc: (parent: T, child: T) -> T
    ) {
        if (configMap.isEmpty()) return

        // 创建临时映射用于更新 / Create temporary map for updates
        val updatedConfigs = mutableMapOf<String, T>()

        for ((id, config) in configMap) {
            val inheritedConfig = resolveInheritance(id, getExtendListFunc, mergeFunc)
            updatedConfigs[id] = inheritedConfig
        }

        // 更新配置映射 / Update configuration map
        configMap.clear()
        configMap.putAll(updatedConfigs)
    }

    /**
     * 解析继承关系
     * Resolve inheritance relationships
     *
     * @param id 要解析的配置项ID / ID of configuration item to resolve
     * @param getExtendListFunc 从配置项中提取继承列表的函数 / Function to extract inheritance list from configuration item
     * @param mergeFunc 合并父配置和子配置的函数 / Function to merge parent and child configurations
     * @param visited 已访问的配置项ID集合，用于检测循环继承 / Set of visited configuration item IDs to detect circular inheritance
     * @return 解析后的配置项 / Resolved configuration item
     */
    private fun resolveInheritance(
        id: String,
        getExtendListFunc: (T) -> List<String>,
        mergeFunc: (parent: T, child: T) -> T,
        visited: MutableSet<String> = mutableSetOf()
    ): T {
        val config = configMap[id] ?: return configMap[id] ?: throw IllegalArgumentException("配置项 $id 不存在 / Configuration item $id does not exist")

        if (id in visited) {
            println("检测到循环继承: $id 已经在继承链中，跳过避免无限递归 / Circular inheritance detected: $id is already in inheritance chain, skipping to avoid infinite recursion")
            return config
        }

        visited.add(id)

        val extendList = getExtendListFunc(config)

        if (extendList.isEmpty()) {
            // 没有继承关系，返回自身 / No inheritance relationship, return self
            return config
        }

        // 递归处理继承的配置项 / Recursively process inherited configuration items
        var resultConfig = config
        for (parentId in extendList) {
            val parentConfig = resolveInheritance(parentId, getExtendListFunc, mergeFunc, visited.toMutableSet())
            resultConfig = mergeFunc(parentConfig, resultConfig)
        }

        return resultConfig
    }

    /**
     * 清空所有配置
     * Clear all configurations
     */
    fun clear() {
        configMap.clear()
    }

    /**
     * 检查是否存在循环继承
     * Check if circular inheritance exists
     *
     * @param getExtendListFunc 从配置项中提取继承列表的函数 / Function to extract inheritance list from configuration item
     * @return 如果存在循环继承则返回true，否则返回false / Returns true if circular inheritance exists, otherwise false
     */
    fun hasCircularInheritance(
        getExtendListFunc: (T) -> List<String>
    ): Boolean {
        val allIds = configMap.keys
        for (id in allIds) {
            if (hasCircularInheritanceFrom(id, getExtendListFunc)) {
                return true
            }
        }
        return false
    }

    /**
     * 从指定ID开始检查是否存在循环继承
     * Check if circular inheritance exists starting from the specified ID
     *
     * @param startId 起始配置项ID / Starting configuration item ID
     * @param getExtendListFunc 从配置项中提取继承列表的函数 / Function to extract inheritance list from configuration item
     * @param path 当前遍历路径 / Current traversal path
     * @return 如果存在循环继承则返回true，否则返回false / Returns true if circular inheritance exists, otherwise false
     */
    private fun hasCircularInheritanceFrom(
        startId: String,
        getExtendListFunc: (T) -> List<String>,
        path: MutableSet<String> = mutableSetOf()
    ): Boolean {
        if (startId !in configMap) return false

        if (startId in path) {
            return true  // 发现循环 / Circular dependency found
        }

        path.add(startId)

        val extendList = getExtendListFunc(configMap[startId]!!)
        for (extendId in extendList) {
            if (hasCircularInheritanceFrom(extendId, getExtendListFunc, path.toMutableSet())) {
                return true
            }
        }

        path.remove(startId)
        return false
    }
}



