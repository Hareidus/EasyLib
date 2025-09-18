package EasyLib.function.matcher

import org.bukkit.entity.Player
import taboolib.common.platform.function.warning
import taboolib.platform.compat.getBalance
import EasyLib.function.Actions
import EasyLib.function.MatcherStrategy
import Matching.function.actions.VaultAction

class VaultMatcher : MatcherStrategy {
    override fun matches(text: String, thisPlayer: Player): Actions? {
        if (!text.startsWith("@vault")) {
            return null
        }

        val split = text.split(":").map { it.trim() }
        if (split.size != 2) {
            warning("vault matcher error, text: $text")
            return null
        }

        val vaultStr = split[1]
        val requiredVault = vaultStr.toIntOrNull()
        if (requiredVault == null) {
            warning("vault matcher error, text: $text")
            return null
        }
        val playersVault = thisPlayer.getBalance().toInt()
        return if (playersVault >= requiredVault) {
            VaultAction(requiredVault)
        } else {
            thisPlayer.sendMessage("§c你没有足够的金豆")
            null
        }
    }

}