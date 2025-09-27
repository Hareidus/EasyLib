package EasyLib.ItemGenerator.Implement

import EasyLib.ItemGenerator.ItemGenerator
import github.saukiya.sxitem.SXItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

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