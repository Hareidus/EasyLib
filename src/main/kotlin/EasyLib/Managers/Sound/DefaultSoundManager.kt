package EasyLib.Managers.Sound

import org.bukkit.Sound
import org.bukkit.entity.Player
import taboolib.common.platform.function.warning

class DefaultSoundManager : SoundManager {
    override fun playSound(player: Player, soundName: Sound?) {
        soundName?.let {
            try {
                player.playSound(player.location, it, 1f, 1f)
            } catch (e: Exception) {
                // 处理无效声音名称
                warning("无效的声音名称: $soundName")
            }
        }
    }
}