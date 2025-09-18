package EasyCrate.Factory.ItemGenerator.Implement

import EasyCrate.Factory.ItemGenerator.ItemGenerator
import net.Indyuce.mmoitems.MMOItems
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem
import net.Indyuce.mmoitems.api.player.PlayerData
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.buildItem

class MMOItem  : ItemGenerator {
    override fun generate(id: String): ItemStack {
        return buildItem(Material.STICK){
            name = "注意！如果使用mmoitem，你的LotteryItem只能使用CustomItem！！"
            throw Exception("注意！如果使用mmoitem，你的LotteryItem只能使用CustomItem！！")
        }
    }
    override fun generate(id: String,player : Player): ItemStack? {
        if (!id.contains("-")) throw Exception("使用MMOItem加载器格式为 ItemId-Type")
        val type = id.split("-")[1]
        val itemId = id.split("-")[0]
        val MMOType = MMOItems.plugin.types.getOrThrow(type.uppercase())
        val MMOItem = MMOItems.plugin.templates.getTemplateOrThrow(MMOType,itemId.uppercase())
        val MMOitem = MMOItem.newBuilder(PlayerData.get(player).rpg).build()
        return MMOitem.newBuilder().build()
    }
}