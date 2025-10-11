package EasyLib.function.actions

import EasyCrate.Factory.ItemGenerator.ItemGenerator
import EasyLib.function.Actions
import org.bukkit.entity.Player
class GiveItemAction : Actions<String> {
    override var data: String = ""

    // itemid-amount
    override fun doAction(thisPlayer: Player) {
        try {
            val split = data.split("-")
            if (split.size < 2) {
                error("Invalid data format, expected 'itemid-amount': $data")
            }

            val itemId = split[0]
            val amount = split[1].toIntOrNull() ?: run {
                error("Invalid amount: ${split[1]}")
            }

            val itemStack = ItemGenerator.generateItem(itemId, thisPlayer)
            repeat(amount) {
                thisPlayer.inventory.addItem(itemStack)
            }
        } catch (e: Exception) {
            println("GiveItemAction execution failed: ${e.message}")
        }
    }

    // giveitem|itemid-amount
    override fun doAction(thisPlayer: Player, text: String) {
        try {
            val split = text.split("|")
            if (split.size < 2) {
                error("Invalid text format, expected 'giveitem|itemid-amount': $text")
            }

            val itemSplit = split[1].split("-")
            if (itemSplit.size < 2) {
                error("Invalid item format, expected 'itemid-amount': ${split[1]}")
            }

            val itemId = itemSplit[0]
            val amount = itemSplit[1].toIntOrNull() ?: run {
                error("Invalid amount: ${itemSplit[1]}")
            }

            val itemStack = ItemGenerator.generateItem(itemId, thisPlayer)
            repeat(amount) {
                thisPlayer.inventory.addItem(itemStack)
            }
        } catch (e: Exception) {
            println("GiveItemAction execution failed: ${e.message}")
        }
    }

    override fun toString(): String {
        return "GiveItemAction - 给予玩家一个物品"
    }

}