package EasyLib.Utils

import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.command.simpleCommand
import taboolib.common.platform.function.adaptCommandSender
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions
import java.util.concurrent.CompletableFuture

object HookKether {

    fun runKether(script: List<String>, player: Player) {
        KetherShell.eval(
            script, options = ScriptOptions(
                sender = adaptCommandSender(player)
            )
        )
    }

    fun runKether(script: String, player: Player) {
        KetherShell.eval(
            script, options = ScriptOptions(
                sender = adaptCommandSender(player)
            )
        )
    }

    fun runKetherWithReturn(script: List<String>, player: Player): CompletableFuture<Any> {
        return KetherShell.eval(
            script, options = ScriptOptions(
                sender = adaptCommandSender(player)
            )
        ).thenApply { it }
    }

    fun runKetherWithReturn(script: String, player: Player): CompletableFuture<Any> {
        return KetherShell.eval(
            script, options = ScriptOptions(
                sender = adaptCommandSender(player)
            )
        ).thenApply { it }
    }

    @Awake(LifeCycle.LOAD)
    fun testCommand(){
        simpleCommand("testKether"){ sender, args ->
            sender.castSafely< Player>()?.let {
                runKether(args.joinToString (" ") ,it)
            }
        }
    }
}