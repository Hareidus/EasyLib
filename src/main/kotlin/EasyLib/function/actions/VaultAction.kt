package Matching.function.actions

import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.platform.compat.depositBalance
import taboolib.platform.compat.getBalance
import EasyLib.function.Actions
/**
 * 待定操作，扣除玩家金币
 */
class VaultAction(val Number: Int) : Actions {

    /**
     * 执行待定操作，扣除玩家金币
     */
    override fun doAction(thisPlayer: Player) {
        info("$thisPlayer 的VaultAction执行了，扣除金币为$Number")
        thisPlayer.depositBalance(-Number.toDouble())
        info("$thisPlayer 的VaultAction执行完毕，当前金币为${thisPlayer.getBalance()}")
    }

}