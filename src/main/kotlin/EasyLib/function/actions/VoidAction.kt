package EasyLib.function.actions

import org.bukkit.entity.Player
import EasyLib.function.Actions

//一个空的Action对象，用于无行为matcher

class VoidAction(val Number: Int) : Actions {
    override fun doAction(thisPlayer: Player) {
    }


}