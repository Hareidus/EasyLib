package EasyLib.function.actions

import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.platform.compat.depositBalance
import taboolib.platform.compat.getBalance
import EasyLib.function.Actions
import EasyLib.function.actions.ActionExecutionException

/**
 * 待定操作，扣除玩家金币
 */
class VaultAction : Actions<Double> {
    override var data = 0.0


    /**
     * 执行待定操作，扣除玩家金币
     */

    override fun doAction(thisPlayer: Player) {
        try {
            thisPlayer.depositBalance(-data)
        } catch (e: Exception) {
            throw ActionExecutionException("无法执行金币操作: ${e.message}")
        }
    }

    override fun doAction(thisPlayer: Player, text: String) {
        try {
            thisPlayer.depositBalance(data)
        } catch (e: Exception) {
            throw ActionExecutionException("无法执行金币操作: ${e.message}")
        }
    }

    override fun toString(): String {
        return "VaultAction - 玩家金币 已注册"
    }
}