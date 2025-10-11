package EasyLib.Utils.ConfigUtils
/**
 * 通用继承配置管理器
 * 支持配置项之间的继承关系处理
 */
class InheritanceConfigManager<T : Any> {

    /**
     * 配置项接口，需要实现继承功能的配置类需要实现此接口
     */
    interface ConfigItem {
        val id: String
        val extend: List<String>
    }

    private val configMap = mutableMapOf<String, T>()

    /**
     * 添加配置项
     */
    fun addConfig(id: String, config: T) {
        configMap[id] = config
    }

    /**
     * 获取所有配置项
     */
    fun getAllConfigs(): Map<String, T> = configMap.toMap()

    /**
     * 获取特定配置项
     */
    fun getConfig(id: String): T? = configMap[id]

    /**
     * 处理继承关系
     * @param getIdFunc 从配置项中提取ID的函数
     * @param getExtendListFunc 从配置项中提取继承列表的函数
     * @param mergeFunc 合并父配置和子配置的函数
     */
    fun handleInheritance(
        getIdFunc: (T) -> String,
        getExtendListFunc: (T) -> List<String>,
        mergeFunc: (parent: T, child: T) -> T
    ) {
        if (configMap.isEmpty()) return

        // 创建临时映射用于更新
        val updatedConfigs = mutableMapOf<String, T>()

        for ((id, config) in configMap) {
            val inheritedConfig = resolveInheritance(id, getExtendListFunc, mergeFunc)
            updatedConfigs[id] = inheritedConfig
        }

        // 更新配置映射
        configMap.clear()
        configMap.putAll(updatedConfigs)
    }

    /**
     * 解析继承关系
     */
    private fun resolveInheritance(
        id: String,
        getExtendListFunc: (T) -> List<String>,
        mergeFunc: (parent: T, child: T) -> T,
        visited: MutableSet<String> = mutableSetOf()
    ): T {
        val config = configMap[id] ?: return configMap[id] ?: throw IllegalArgumentException("配置项 $id 不存在")

        if (id in visited) {
            println("检测到循环继承: $id 已经在继承链中，跳过避免无限递归")
            return config
        }

        visited.add(id)

        val extendList = getExtendListFunc(config)

        if (extendList.isEmpty()) {
            // 没有继承关系，返回自身
            return config
        }

        // 递归处理继承的配置项
        var resultConfig = config
        for (parentId in extendList) {
            val parentConfig = resolveInheritance(parentId, getExtendListFunc, mergeFunc, visited.toMutableSet())
            resultConfig = mergeFunc(parentConfig, resultConfig)
        }

        return resultConfig
    }

    /**
     * 清空所有配置
     */
    fun clear() {
        configMap.clear()
    }

    /**
     * 检查是否存在循环继承
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

    private fun hasCircularInheritanceFrom(
        startId: String,
        getExtendListFunc: (T) -> List<String>,
        path: MutableSet<String> = mutableSetOf()
    ): Boolean {
        if (startId !in configMap) return false

        if (startId in path) {
            return true  // 发现循环
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

//// 使用示例
//fun main() {
//    println("=== 通用继承配置管理器示例 ===\n")
//
//    // 创建物品组
//    val normalGroup = ItemGroup(
//        id = "normal-global-shop-default-group",
//        itemGroup = mutableSetOf(ItemConfig("iron")),
//        extend = emptyList()
//    )
//
//    val vipGroup = ItemGroup(
//        id = "vip-global-shop-default-group",
//        itemGroup = mutableSetOf(ItemConfig("gold")),
//        extend = listOf("normal-global-shop-default-group")
//    )
//
//    val opGroup = ItemGroup(
//        id = "op-global-shop-default-group",
//        itemGroup = mutableSetOf(ItemConfig("diamond")),
//        extend = listOf("vip-global-shop-default-group")
//    )
//
//    // 添加到管理器
//    ItemGroupManager.addItemGroup(normalGroup)
//    ItemGroupManager.addItemGroup(vipGroup)
//    ItemGroupManager.addItemGroup(opGroup)
//
//    println("添加的原始物品组:")
//    ItemGroupManager.getAllItemGroups().forEach { (id, group) ->
//        println("  $id -> $group")
//    }
//    println()
//
//    // 检查是否有循环继承
//    if (ItemGroupManager.hasCircularInheritance()) {
//        println("检测到循环继承！")
//        return
//    } else {
//        println("没有检测到循环继承")
//    }
//
//    // 处理继承
//    ItemGroupManager.reloadItemGroup()
//
//    println("\n处理继承后的物品组:")
//    ItemGroupManager.getAllItemGroups().forEach { (id, group) ->
//        println("  $id -> $group")
//    }
//
//    println("\n=== 通用性演示：其他类型的配置 ===")
//
//    // 演示如何用于其他类型的配置
//    data class PermissionGroup(
//        override val id: String,
//        var permissions: MutableSet<String>,
//        val extend: List<String> = emptyList()
//    ) : InheritanceConfigManager.ConfigItem
//
//    val inheritanceManager = InheritanceConfigManager<PermissionGroup>()
//
//    inheritanceManager.addConfig("user", PermissionGroup(
//        id = "user",
//        permissions = mutableSetOf("read"),
//        extend = emptyList()
//    ))
//
//    inheritanceManager.addConfig("moderator", PermissionGroup(
//        id = "moderator",
//        permissions = mutableSetOf("moderate"),
//        extend = listOf("user")
//    ))
//
//    inheritanceManager.addConfig("admin", PermissionGroup(
//        id = "admin",
//        permissions = mutableSetOf("delete"),
//        extend = listOf("moderator")
//    ))
//
//    // 处理权限继承
//    inheritanceManager.handleInheritance(
//        getIdFunc = { it.id },
//        getExtendListFunc = { it.extend },
//        mergeFunc = { parent, child ->
//            val mergedPermissions = (parent.permissions + child.permissions).toMutableSet()
//            child.copy(permissions = mergedPermissions)
//        }
//    )
//
//    println("权限继承结果:")
//    inheritanceManager.getAllConfigs().forEach { (id, group) ->
//        println("  $id -> 权限: ${group.permissions}")
//    }
//}
//


