package EasyLib.function.actions

import org.bukkit.entity.Player
import EasyLib.function.Actions

//一个空的Action对象，用于无行为matcher

class VoidAction : Actions<Any> {
    override var data: Any = Any()


    override fun doAction(thisPlayer: Player) {

    }

    override fun doAction(thisPlayer: Player, text: String) {

    }

    override fun toString(): String {
        return "VoidAction - 空动作 已注册"
    }
}