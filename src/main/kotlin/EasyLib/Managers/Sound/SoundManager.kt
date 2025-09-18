package EasyLib.Managers.Sound

import org.bukkit.Sound
import org.bukkit.entity.Player

interface SoundManager {
    fun playSound(player: Player, soundName: Sound?)
}