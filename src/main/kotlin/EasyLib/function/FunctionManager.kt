package EasyLib.function

object FunctionManager {
    val matcherManagers = MatcherManager()
    val actionManagers = ActionManager()

    fun getMatcherManager(): MatcherManager {
        return matcherManagers
    }
    fun getActionManager(): ActionManager {
        return actionManagers
    }
}