package EasyLib.function.matcher

import EasyLib.Utils.infoType
import EasyLib.Utils.infoType.Companion.send
import EasyLib.function.Actions
import EasyLib.function.MatcherStrategy
import EasyLib.function.actions.VoidAction
import org.bukkit.entity.Player
import taboolib.platform.util.asLangText

class PermissionMatcher : MatcherStrategy {

    override fun matches(text: String, thisPlayer: Player, info: infoType): Actions<*>? {
        val split = text.split("|")
        if (split.size != 2) {
            return null
        }
        val permission = split[1]
        return if (thisPlayer.hasPermission(permission)) {
            VoidAction()
        } else {
            thisPlayer.asLangText("permission-matcher-fail").send(info, thisPlayer)
            null
        }
    }

    override fun matches(text: String, op: String, thisPlayer: Player, info: infoType): Actions<*>? {
        TODO("Not yet implemented")
    }
}