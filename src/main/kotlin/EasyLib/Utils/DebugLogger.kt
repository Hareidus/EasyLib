// 在Utils包中创建DebugLogger.kt
package EasyLib.Utils


import EasyLib.Configs.DebugConfig
import taboolib.common.platform.function.info
import taboolib.common.platform.function.warning
import taboolib.common.platform.function.severe


// 改进后
object DebugLogger {
    enum class LogLevel {
        OFF, ERROR, WARN, INFO, DEBUG, TRACE
    }

    private val currentLogLevel: LogLevel by lazy {
        when (DebugConfig.logLevel.uppercase()) {
            "ERROR" -> LogLevel.ERROR
            "WARNING", "WARN" -> LogLevel.WARN
            "INFO" -> LogLevel.INFO
            "DEBUG" -> LogLevel.DEBUG
            "TRACE" -> LogLevel.TRACE
            else -> LogLevel.OFF
        }
    }

    fun debug(message: String, step: String? = null, prefix: String? = null) {
        if (!shouldLog(LogLevel.DEBUG, step)) return
        val formattedPrefix = prefix?.let { "[$it]" } ?: ""
        info("[DEBUG] $formattedPrefix$message")
    }

    fun warn(message: String, step: String? = null, prefix: String? = null) {
        if (!shouldLog(LogLevel.WARN, step)) return
        val formattedPrefix = prefix?.let { "[$it]" } ?: ""
        warning("[WARN] $formattedPrefix$message")
    }

    private fun shouldLog(level: LogLevel, step: String?): Boolean {
        if (currentLogLevel == LogLevel.OFF || level > currentLogLevel) return false
        return step == null || DebugConfig.isDebugStepEnabled(step)
    }
}
