package EasyLib.function.matcher

import EasyLib.function.Actions
import EasyLib.function.MatcherStrategy
import EasyLib.function.actions.RemoveItemAction
import easySaver.arim.Arim
import org.bukkit.entity.Player
import taboolib.common.platform.function.info

class ItemMatcher : MatcherStrategy {
    /**
     * 匹配玩家物品名称
     */

    override fun matches(text: String, thisPlayer: Player): Actions<*>? {
        // @item|name:all(startswith(&c机械),c(靴))-1
        // 表达式-数量
        val split = text.split("|")
        if (split.size != 2){
            info("item matcher error, text: $text")
            return null
        }
        val split2 = split[1].split("-")
        if (split2.size != 2){
            info("item matcher error, text: $text")
        }
        val expression = split2[0]
        val number = split2[1].toIntOrNull()
        if (number == null){
            info("item matcher error, text: $text")
            return null
        }
        val count = thisPlayer.inventory.contents.count { itemStack ->
            Arim.itemMatch.match(itemStack, expression)
        }
        if (count >= number){
            return RemoveItemAction().apply {
                // 吧后续的表达式传递
                this.data = split[1]
            }
        }
        return null
    }
}