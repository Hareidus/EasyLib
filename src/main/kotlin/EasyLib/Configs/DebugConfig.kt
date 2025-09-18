// 在Configs包中创建DebugConfig.kt
package EasyLib.Configs

import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile

object DebugConfig {
    @Config("debug.yml")
    lateinit var config: ConfigFile

    val enabled: Boolean by lazy {
        config.getBoolean("debug.enabled", false)
    }

    val logLevel: String by lazy {
        config.getString("debug.log-level", "INFO") ?: "INFO"
    }

    fun isDebugStepEnabled(step: String): Boolean {
        return enabled && config.getBoolean("debug.steps.$step", false)
    }
}
