package EasyLib.Utils.ItemStackUtils

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.compat.replacePlaceholder

object ItemStackUtils {
    /**
     * 抽象化物品Lore替换工具
     *
     * 此工具函数提供了一个通用的机制，用于根据提供的键值对替换物品描述（Lore）中的占位符。
     * 它接收一个物品堆栈、一个包含占位符和替换值的映射表，以及一个可选的玩家参数，
     * 然后返回一个Lore已更新的物品堆栈。
     *
     * @param replacements 包含占位符和对应替换值的映射表
     * @param player 可选的玩家参数，可用于获取玩家特定的数据
     * @return 包含更新后Lore的物品堆栈副本
     */
    fun ItemStack.replaceItemLore( replacements: Map<String, String>, player: Player? = null): ItemStack {
        val modifiedItemStack = this.clone()

        val meta = modifiedItemStack.itemMeta
        if (meta != null && meta.hasLore()) {
            val lore = meta.lore?.toMutableList() ?: mutableListOf()
            for (i in lore.indices) {
                var modifiedLine = lore[i]
                player?.let {
                    modifiedLine = modifiedLine.replacePlaceholder(it)
                }
                for ((placeholder, replacement) in replacements) {
                    modifiedLine = modifiedLine.replace(placeholder, replacement)
                }
                lore[i] = modifiedLine
            }
            meta.lore = lore
            modifiedItemStack.itemMeta = meta
        }
        return modifiedItemStack
    }
}