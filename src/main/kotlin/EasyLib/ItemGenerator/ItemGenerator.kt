package EasyLib.ItemGenerator

import EasyLib.ItemGenerator.Implement.MMOItem
import EasyLib.ItemGenerator.Implement.NeiGeItem
import EasyLib.ItemGenerator.Implement.SxItem
import EasyLib.ItemGenerator.Implement.EasySaverItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
interface ItemGenerator {
    fun generate(id: String): ItemStack?
    fun generate(id: String, player: Player): ItemStack?

    companion object {
        private val generators = mutableListOf<ItemGenerator>()

        init {
            // 按优先级添加生成器
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
            }catch (_: ClassNotFoundException){}
        }

        fun generateItem(id: String, player: Player): ItemStack {
            for (generator in generators) {
                generator.generate(id, player)?.let { return it }
            }
            throw IllegalArgumentException("No ItemGenerator found for item: $id")
        }

        fun generateItem(id: String): ItemStack {
            for (generator in generators) {
                generator.generate(id)?.let { return it }
            }
            throw IllegalArgumentException("No ItemGenerator found for item: $id")
        }
    }
}