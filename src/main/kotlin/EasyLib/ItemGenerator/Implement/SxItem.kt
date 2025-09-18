package EasyCrate.Factory.ItemGenerator.Implement

import EasyCrate.Factory.ItemGenerator.ItemGenerator
import github.saukiya.sxitem.SXItem
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.bukkitPlugin

class SxItem : ItemGenerator {

    override fun generate(id: String): ItemStack? {
        var item : ItemStack? = null
        item = SXItem.getItemManager().getItem(id)
        return if (item.type == Material.AIR){
            null
        }else{
            item
        }
    }

    override fun generate(id: String, player: Player): ItemStack? {
        var item: ItemStack? = null
        item = SXItem.getItemManager().getItem(id)
        return if (item.type == Material.AIR) {
            null
        } else {
            item
        }
    }
}