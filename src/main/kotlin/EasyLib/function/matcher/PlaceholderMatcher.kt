package EasyLib.function.matcher

import org.bukkit.entity.Player
import taboolib.common.platform.function.warning
import taboolib.platform.compat.replacePlaceholder
import EasyLib.function.Actions
import EasyLib.function.MatcherStrategy
import EasyLib.function.actions.VoidAction
import taboolib.platform.util.sendLang

class PlaceholderMatcher : MatcherStrategy {
    /**
     * 判断给定的文本是否匹配玩家的某些条件
     *
     * 该函数用于解析特定格式的文本，以判断玩家是否满足文本中描述的条件
     * 条件格式为 "placeholder: value1 op value2"，其中 placeholder 是玩家的占位符，
     * value1 和 value2 是要比较的数值，op 是比较运算符
     *
     * @param text 待解析的文本
     * @param thisPlayer 当前玩家对象，用于替换占位符
     * @return 如果玩家满足条件，则返回一个空操作对象；否则返回 null
     */
    override fun matches(text: String, thisPlayer: Player): Actions? {
        // 分割文本为两部分，应确保文本包含且仅包含一个冒号
        val split = text.split(":")
        if (split.size != 2) {
            warning("PlaceholderMatcher: $text is not a placeholder (missing colon), please check your file")
            return null
        }
        
        // 分割条件部分为三个部分，确保格式正确
        val parts = split[1].split(" ").map { it.trim() }
        if (parts.size != 3) {
            warning("PlaceholderMatcher: $text is not a placeholder (invalid format), please check your file")
            return null
        }

        // 解构条件的三个部分，分别为左侧值、运算符和右侧值
        val (lhsStr, op, rhsStr) = parts
        // 替换玩家占位符
        val lhs = lhsStr.replacePlaceholder(thisPlayer)
        val rhs = rhsStr.replacePlaceholder(thisPlayer)

        val lhsHolderStr = lhsStr.replace("%","")
        val rhsHolderStr  = lhsStr.replace("%","")

        // 创建一个空操作对象，用于后续可能的返回
        val voidAction = VoidAction(1)

        // 将左侧值转换为数字，如果转换失败，则返回 null
        val lhsValue = runCatching { lhs.toDouble() }.getOrElse {
            warning("PlaceholderMatcher: invalid number '$lhs' in text $text")
            return null
        }

        // 将右侧值转换为数字，如果转换失败，则返回 null
        val rhsValue = runCatching { rhs.toDouble() }.getOrElse {
            warning("PlaceholderMatcher: invalid number '$rhs' in text $text")
            return null
        }

        // 根据运算符判断条件是否满足，并返回相应的结果
        return when (op) {
            ">=" -> if (lhsValue >= rhsValue) voidAction else {
                thisPlayer.sendLang("placeholder-matcher-fail", lhsHolderStr, rhsHolderStr , op)
                null
            }
            "<=" -> if (lhsValue <= rhsValue) voidAction else {
                thisPlayer.sendLang("placeholder-matcher-fail", lhsHolderStr, rhsHolderStr , op)
                null
            }
            ">" -> if (lhsValue > rhsValue) voidAction else {
                thisPlayer.sendLang("placeholder-matcher-fail", lhsHolderStr, rhsHolderStr , op)
                null
            }
            "<" -> if (lhsValue < rhsValue) voidAction else {
                thisPlayer.sendLang("placeholder-matcher-fail", lhsHolderStr, rhsHolderStr , op)
                null
            }
            "==" -> if (lhsValue == rhsValue) voidAction else {
                thisPlayer.sendLang("placeholder-matcher-fail", lhsHolderStr, rhsHolderStr , op)
                null
            }
            "!=" -> if (lhsValue != rhsValue) voidAction else {
                thisPlayer.sendLang("placeholder-matcher-fail", lhsHolderStr, rhsHolderStr , op)
                null
            }
            else -> {
                warning("PlaceholderMatcher: error operator $op in text $text")
                null
            }
        }
    }

}