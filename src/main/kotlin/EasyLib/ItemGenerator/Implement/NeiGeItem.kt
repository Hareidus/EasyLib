package EasyCrate.Factory.ItemGenerator.Implement


import EasyCrate.Factory.ItemGenerator.ItemGenerator
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import pers.neige.neigeitems.manager.ItemManager

class NeiGeItem : ItemGenerator {

    override fun generate(id : String): ItemStack?{
        return ItemManager.getItemStack(id)
    }

    override fun generate(id: String, player: Player): ItemStack? {
        return ItemManager.getItemStack(id)
    }

}