package EasyLib.function

import EasyLib.function.actions.CommandAction
import EasyLib.function.actions.GiveItemAction
import EasyLib.function.actions.RemoveItemAction
import EasyLib.function.actions.MessageAction
import EasyLib.function.actions.VaultAction
import taboolib.common.platform.function.info

class ActionManager {

    private val actionMap: MutableMap<String, Actions<*>> by lazy {
        mutableMapOf<String, Actions<*>>().apply {
            put("@message", MessageAction())
            put("@vault", VaultAction())
            put("@giveitem", GiveItemAction())
            put("@removeitem", RemoveItemAction())
            put("@command", CommandAction())
        }
    }

    /*
    添加一个动作,开放给外部使用
     */
    fun addAction(prefix: String, action : Actions<*>) {
        actionMap[prefix] = action
        info("register action with prefix: $prefix , function: $action")
    }

    fun listActions(): List<String> {
        val list = mutableListOf<String>()
        for ((prefix, action) in actionMap) {
            list.add("registered action with prefix: $prefix , function: $action")
        }
        return list
    }
}