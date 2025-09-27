package EasyLib.function.actions

import org.bukkit.entity.Player
import EasyLib.function.Actions

class MessageAction: Actions<String> {
    override var data =  ""

    override fun doAction(thisPlayer: Player) {
        try {
            thisPlayer.sendMessage(data)
        }catch (e: Exception){
            throw ActionExecutionException("消息发送动作执行失败", e)
        }
    }

    override fun doAction(thisPlayer: Player, text: String) {
        val message = text.substringAfter("|")
        try {
            thisPlayer.sendMessage(message)
        }catch (e: Exception){
            throw ActionExecutionException("消息发送动作执行失败", e)
        }
    }

    override fun toString(): String {
        return "MessageAction - 发送一个信息 已注册"
    }



}