package EasyLib.Utils

import org.bukkit.entity.Player
import pers.neige.neigeitems.utils.PlayerUtils.sendActionBar

enum class infoType {
    Message,
    ActionBar,
    Title,
    None;

    companion object{
        fun String.send(infoType: infoType, player: Player){
            when(infoType){
                Message -> player.sendMessage(this)
                ActionBar -> player.sendActionBar(this)
                Title -> player.sendTitle( this, "", 10, 20, 10)
                None -> {}
            }
        }
    }
}

