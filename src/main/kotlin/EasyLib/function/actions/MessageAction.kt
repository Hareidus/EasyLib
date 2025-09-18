package Matching.function.actions

import org.bukkit.entity.Player
import EasyLib.function.Actions

class MessageAction(val Number: String) : Actions {
    override fun doAction(thisPlayer: Player) {
        thisPlayer.sendMessage(Number)
    }

}