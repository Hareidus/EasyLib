package EasyLib.function.actions

import EasyLib.function.Actions
import easySaver.arim.Arim
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.takeItem

class RemoveItemAction  : Actions<String> {
    override var data: String = ""

    override fun doAction(thisPlayer: Player) {
        try {
            val parts = data.split("-")
            if (parts.size != 2) {
                error("Invalid data format: $data")
            }

            val expression = parts[0]
            val amount = parts[1].toIntOrNull() ?: run {
                error("Invalid amount: ${parts[1]}")
            }

            thisPlayer.inventory.takeItem(amount) { itemStack ->
                Arim.itemMatch.match(itemStack, expression)
            }
        } catch (e: Exception) {
            // 记录错误日志
            println("RemoveItemAction execution failed: ${e.message}")
        }
    }

    override fun doAction(thisPlayer: Player, text: String) {
        try {
            val text2 = text.substringAfter("|", "")
            if (text2.isEmpty()) {
                error("Invalid text format: $text")
            }

            val parts = text2.split("-")
            if (parts.size != 2) {
                error("Invalid text format: $text")
            }

            val expression = parts[0]
            val amount = parts[1].toIntOrNull() ?: run {
                error("Invalid amount: ${parts[1]}")
            }

            thisPlayer.inventory.takeItem(amount) { itemStack ->
                Arim.itemMatch.match(itemStack, expression)
            }
        } catch (e: Exception) {
            // 记录错误日志
            println("RemoveItemAction execution failed: ${e.message}")
        }
    }

}