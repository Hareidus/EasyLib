package EasyLib.function

import org.bukkit.entity.Player

//设计思路
//有许多的条件，每个条件都有自己的处理代码
//一行文字传入后，通过matcherManager来判断是否，符合条件
//最终返回结果
//最终选定 策略模式
//业务流程 玩家传入文本 -> 判断条件是否都满足 -> 返回结果 -> 扣除条件所需 ->执行动作
class MatcherManager {


    private val strategyMap = mutableMapOf<String, MatcherStrategy>().apply {
//        put("@live", LiveTimeMatcher())
//        put("@activator", ActivatorMatcher())
        // 添加其他策略前缀
    }
    fun checkMatcher(input : List<String>,  thisPlayer : Player) : Boolean{
        val actions = mutableListOf<Actions>()
        for (text in input){
            val prefix = text.substringBefore(':')
            strategyMap[prefix]?.let {
                it.matches(text,thisPlayer)?.run(actions::add)
            } ?: return false
            //特性笔记
            // 对于 strategyMap[prefix]?,如果没有这个前缀则直接跳过let块
            // 如果有这个前缀则执行let块,其中如果matches返回的是null,则let块中的返回值就为null，触发Elvis  操作符return false
        }
        actions.forEach {
            it.doAction(thisPlayer)
        }
        return true
    }

    fun checkMatcherWithoutAction(input : List<String>,  thisPlayer : Player) : Boolean{
        for (text in input){
            val prefix = text.substringBefore(':')
           if (strategyMap[prefix]?.matches(text,thisPlayer) == null) return  false
        }
        return true
    }

    fun checkMatcherSingleTextWithoutAction(input : String,  thisPlayer : Player) : Boolean{
        val prefix = input.substringBefore(':')
        return strategyMap[prefix]?.matches(input,thisPlayer) != null
    }
}