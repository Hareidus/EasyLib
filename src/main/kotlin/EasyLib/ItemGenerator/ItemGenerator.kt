
package EasyLib.ItemGenerator

import EasyLib.ItemGenerator.Implement.MMOItem
import EasyLib.ItemGenerator.Implement.NeiGeItem
import EasyLib.ItemGenerator.Implement.SxItem
import EasyLib.ItemGenerator.Implement.EasySaverItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * # 物品生成器接口 (Item Generator Interface)
 *
 * ## 模块目的 (Purpose)
 * 此模块提供了一个统一的物品生成接口，用于从不同的物品插件系统中生成物品。
 * 它通过自动检测已安装的插件并按优先级顺序尝试生成物品，实现了对多种物品插件的兼容性。
 * 开发者可以通过实现此接口来扩展对新物品插件的支持。
 *
 * This module provides a unified item generation interface for generating items from different item plugin systems.
 * It achieves compatibility with multiple item plugins by automatically detecting installed plugins and attempting to generate items in priority order.
 * Developers can extend support for new item plugins by implementing this interface.
 *
 * ## 如何添加新的物品生成器 (How to Add New Item Generators)
 * 1. **创建实现类**: 创建一个实现 `ItemGenerator` 接口的新类
 *    - Create a new class that implements the `ItemGenerator` interface
 *
 * 2. **实现生成逻辑**: 在 `generate` 方法中实现从特定插件生成物品的逻辑
 *    - Implement the logic to generate items from the specific plugin in the `generate` methods
 *
 * 3. **检查依赖**: 在 `ItemGenerator` 的 `companion object` 初始化块中添加依赖检查
 *    - Add dependency checks in the `init` block of the `ItemGenerator`'s `companion object`
 *
 * 4. **按优先级添加**: 将新生成器按适当的优先级添加到 `generators` 列表中
 *    - Add the new generator to the `generators` list at an appropriate priority
 *
 * 5. **导入实现**: 在文件顶部添加相应的导入语句
 *    - Add the appropriate import statements at the top of the file
 *
 * ## 适用场景 (Applicable Scenarios)
 * - **多插件环境**: 在同时使用多种物品插件的服务器中统一物品生成
 *   - Multi-plugin environments: Unifying item generation in servers using multiple item plugins simultaneously
 * - **插件兼容性**: 为不同物品插件提供一致的API接口
 *   - Plugin compatibility: Providing a consistent API interface for different item plugins
 * - **物品系统集成**: 在自定义插件中集成第三方物品系统
 *   - Item system integration: Integrating third-party item systems in custom plugins
 * - **动态加载**: 根据服务器安装的插件动态加载对应的生成器
 *   - Dynamic loading: Dynamically loading corresponding generators based on plugins installed on the server
 *
 * ## 一些吐槽 (Some Complaints)
 * - 插件依赖检测仅通过类名存在判断，可能不够准确
 *   - Plugin dependency detection only checks for class name existence, which may not be accurate enough
 * - 优先级固定写在代码中，灵活性有限
 *   - Priority is hardcoded in the code, limiting flexibility
 * - 错误处理相对简单，缺少详细的错误信息
 *   - Error handling is relatively simple, lacking detailed error information
 * - 如果多个插件都支持相同物品ID，可能产生意外的行为
 *   - Unexpected behavior may occur if multiple plugins support the same item ID
 * - 某些插件可能需要特定的上下文或权限才能正确生成物品
 *   - Some plugins may require specific context or permissions to correctly generate items
 */
interface ItemGenerator {
    /**
     * 根据ID生成物品
     * Generate an item based on ID
     *
     * @param id 物品ID / Item ID
     * @return 生成的物品堆栈，如果无法生成则返回null / Generated item stack, or null if generation fails
     */
    fun generate(id: String): ItemStack?

    /**
     * 根据ID和玩家生成物品（可能包含玩家特定属性）
     * Generate an item based on ID and player (may include player-specific attributes)
     *
     * @param id 物品ID / Item ID
     * @param player 相关玩家 / Associated player
     * @return 生成的物品堆栈，如果无法生成则返回null / Generated item stack, or null if generation fails
     */
    fun generate(id: String, player: Player): ItemStack?
    companion object {
        private val generators = mutableListOf<ItemGenerator>()
        init {
            // 按优先级添加生成器
            // Add generators in priority order
            try {
                Class.forName("github.saukiya.sxitem.SXItem")
                generators.add(SxItem())
            } catch (_: ClassNotFoundException) {}

            try {
                Class.forName("pers.neige.neigeitems.manager.ItemManager")
                generators.add(NeiGeItem())
            } catch (_: ClassNotFoundException) {}

            try {
                Class.forName("net.Indyuce.mmoitems.MMOItems")
                generators.add(MMOItem())
            } catch (_: ClassNotFoundException) {}

            try {
                Class.forName("easySaver.Config.itemconfig")
                generators.add(EasySaverItem())
            } catch (_: ClassNotFoundException) {}
        }
        /**
         * 根据ID和玩家生成物品，尝试所有可用的生成器
         * Generate an item based on ID and player, trying all available generators
         *
         * @param id 物品ID / Item ID
         * @param player 相关玩家 / Associated player
         * @return 生成的物品堆栈 / Generated item stack
         * @throws IllegalArgumentException 如果没有生成器可以生成指定物品 / If no generator can generate the specified item
         */
        fun generateItem(id: String, player: Player): ItemStack {
            for (generator in generators) {
                generator.generate(id, player)?.let { return it }
            }
            throw IllegalArgumentException("No ItemGenerator found for item: $id")
        }

        /**
         * 根据ID生成物品，尝试所有可用的生成器
         * Generate an item based on ID, trying all available generators
         *
         * @param id 物品ID / Item ID
         * @return 生成的物品堆栈 / Generated item stack
         * @throws IllegalArgumentException 如果没有生成器可以生成指定物品 / If no generator can generate the specified item
         */
        fun generateItem(id: String): ItemStack {
            for (generator in generators) {
                generator.generate(id)?.let { return it }
            }
            throw IllegalArgumentException("No ItemGenerator found for item: $id")
        }
    }
}



