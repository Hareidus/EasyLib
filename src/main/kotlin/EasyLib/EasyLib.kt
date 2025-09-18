package EasyLib

import taboolib.common.platform.Platform
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.module.kether.KetherShell
import taboolib.module.kether.runKether
import taboolib.module.metrics.Metrics
import top.maplex.arim.tools.conditionevaluator.ConditionEvaluator
import top.maplex.arim.tools.fixedcalculator.FixedCalculator
import top.maplex.arim.tools.glow.api.IGlow
import top.maplex.arim.tools.itemmatch.ItemMatch
import top.maplex.arim.tools.variablecalculator.VariableCalculator

object EasyLib : Plugin() {


    /**
     *  条件判断
     */
    val evaluator by lazy { ConditionEvaluator() }

    /**
     *  计算器
     */
    val fixedCalculator by lazy { FixedCalculator() }

    /**
     *  物品匹配
     */
    val itemMatch by lazy { ItemMatch() }

    /**
     *  物品高光
     */
    val glow by lazy { IGlow() }
    // 项目使用TabooLib Start Jar 创建!
    override fun onEnable() {
        info("Successfully running EasyLib!")
        Metrics(27293,"1.0.0",Platform.BUKKIT)

    }
}