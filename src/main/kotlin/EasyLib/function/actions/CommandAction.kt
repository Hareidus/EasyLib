package EasyLib.function.actions

import EasyLib.function.Actions
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.platform.compat.replacePlaceholder

class CommandAction : Actions<String> {
    override var data: String = ""

    // @command|say hello world
    override fun doAction(thisPlayer: Player) {
        try {
            if (data.isBlank()) {
                info("CommandAction: data is empty")
                return
            }
            val command = data.replacePlaceholder(thisPlayer)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
        } catch (e: Exception) {
            info("CommandAction execution failed for data '$data': ${e.message}")
        }
    }

    //@command|say hello world
    override fun doAction(thisPlayer: Player, text: String) {
        try {
            val commandPart = text.substringAfter("|", "")
            if (commandPart.isBlank()) {
                info("CommandAction: invalid command format in text '$text'")
                return
            }
            val command = commandPart.replacePlaceholder(thisPlayer)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
        } catch (e: Exception) {
            info("CommandAction execution failed for text '$text': ${e.message}")
        }
    }

    override fun toString(): String {
        return "CommandAction - 控制台执行指令"
    }

}
