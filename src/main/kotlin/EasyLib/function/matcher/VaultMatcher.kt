package EasyLib.function.matcher

import EasyLib.Utils.infoType
import EasyLib.Utils.infoType.Companion.send
import org.bukkit.entity.Player
import taboolib.common.platform.function.warning
import taboolib.platform.compat.getBalance
import EasyLib.function.Actions
import EasyLib.function.MatcherStrategy
import EasyLib.function.actions.VaultAction
import easySaver.arim.Arim
import taboolib.platform.util.asLangText

class VaultMatcher : MatcherStrategy {
    override fun matches(text: String, thisPlayer: Player, info: infoType): Actions<*>? {
        return matches(text, "{amount}", thisPlayer, info)
    }

    override fun matches(text: String, op: String, thisPlayer: Player, info: infoType): Actions<*>? {
        if (!text.startsWith("@vault")) {
            return null
        }
        val split = text.split("|").map { it.trim() }
        if (split.size != 2) {
            warning("vault matcher error, text: $text")
            return null
        }
        val vaultStr = split[1]
        val parse = op.replace("{amount}", vaultStr)
        val requiredVault = try {
            Arim.fixedCalculator.evaluate(parse)
        } catch (e: Exception) {
            warning("vault matcher error, text: $text")
            return null
        }
        val playersVault = thisPlayer.getBalance()
        return if (playersVault >= requiredVault) {
            VaultAction().apply { this.data = requiredVault }
        } else {
            thisPlayer.asLangText("vault-matcher-fail").send(info, thisPlayer)
            null
        }
    }
}
