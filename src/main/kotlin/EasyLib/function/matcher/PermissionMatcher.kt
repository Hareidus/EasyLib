package EasyLib.function.matcher

import EasyLib.function.Actions
import EasyLib.function.MatcherStrategy
import EasyLib.function.actions.VoidAction
import org.bukkit.entity.Player

class PermissionMatcher : MatcherStrategy {

    override fun matches(text: String, thisPlayer: Player): Actions<*>? {
        val split = text.split("|")
        if (split.size != 2) {
            return null
        }
        val permission = split[1]
        return if (thisPlayer.hasPermission(permission)) {
            VoidAction()
        } else {
            null
        }
    }
}