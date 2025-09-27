package EasyLib.function

import org.bukkit.entity.Player

interface Actions<T> {
    var data : T
    //这个Action是给决策模式后用的
    fun doAction(thisPlayer : Player)
    //这个Action是直接识别文本用的
    fun doAction(thisPlayer : Player,text : String)
}