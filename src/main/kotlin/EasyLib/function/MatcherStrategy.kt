package EasyLib.function

import EasyLib.Utils.infoType
import EasyLib.function.Actions
import org.bukkit.entity.Player

interface MatcherStrategy {
    fun matches(text : String,thisPlayer : Player,info : infoType) : Actions<*>?
    fun matches(text : String,op : String,thisPlayer : Player,info : infoType) : Actions<*>?

}