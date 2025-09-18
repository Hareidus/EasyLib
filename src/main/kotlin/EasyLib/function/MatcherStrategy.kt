package EasyLib.function

import EasyLib.function.Actions
import org.bukkit.entity.Player

interface MatcherStrategy {
    fun matches(text : String,thisPlayer : Player) : Actions?
}