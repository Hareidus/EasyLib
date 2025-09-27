package EasyLib.ItemGenerator.Implement

import EasyLib.ItemGenerator.ItemGenerator
import easySaver.Config.itemconfig
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class EasySaverItem : ItemGenerator {
    override fun generate(id: String): ItemStack? {
        return itemconfig.getItem( id)
    }

    override fun generate(id: String, player: Player): ItemStack? {
        return itemconfig.getItem(id)
    }
}