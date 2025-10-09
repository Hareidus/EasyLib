package EasyLib.function.matcher

import EasyLib.Utils.infoType
import EasyLib.Utils.infoType.Companion.send
import EasyLib.function.Actions
import EasyLib.function.MatcherStrategy
import EasyLib.function.actions.RemoveItemAction
import easySaver.arim.Arim
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.platform.compat.replacePlaceholder
import taboolib.platform.util.asLangText


class ItemMatcher : MatcherStrategy {
    /**
     * 匹配玩家物品名称
     */
    override fun matches(text: String, thisPlayer: Player, info: infoType): Actions<*>? {
        return matches(text, "{amount}", thisPlayer, info)
    }

    //op example : {amount} * 2 + 1
    override fun matches(text: String, op: String, thisPlayer: Player, info: infoType): Actions<*>? {
        val split = text.split("|")
        if (split.size != 2) {
            info("item matcher error, text: $text")
            return null
        }

        val split2 = split[1].split("-")
        if (split2.size != 2) {
            info("item matcher error, text: $text")
            return null
        }

        val expression = split2[0]
        val amount = split2[1].toIntOrNull() ?: throw IllegalArgumentException("Invalid amount: ${split2[1]}")
        val parse = op.replace("{amount}",amount.toString()).replacePlaceholder(thisPlayer)
        val number = try {
            Arim.fixedCalculator.evaluate(parse).toInt()
        } catch (e: Exception) {
            info("item matcher error, text: $text")
            return null
        }

        val count = thisPlayer.inventory.contents.count { itemStack ->
            Arim.itemMatch.match(itemStack, expression)
        }

        if (count >= number) {
            return RemoveItemAction().apply {
                this.amount = amount
                this.condition = expression
            }
        } else {
            thisPlayer.asLangText("item-matcher-fail").send(info, thisPlayer)
        }
        return null
    }
}
