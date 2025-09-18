package EasyLib.EasyGui.EasyGuiConfig.GuiConfig

import taboolib.common.PrimitiveIO.warning
import taboolib.module.configuration.Configuration

class PageableGuiConfig(private val config: Configuration) : GuiConfig(config) {

    override fun checkForCompleteness() {
        val keySet = getGuiKey()

        validate(this, setOf("baned"))

        for(key in keySet){
            val section = getKeySection()?.getConfigurationSection(key.toString())
            val function = section?.getString("IconFunction")
            if(function == "baned"){
                val isUsed = getUsedChar().contains(key.toCharArray()[0])
                if (!isUsed) {
                    warning(" baned 展示节点 $key 未声明在GuiPlain中，请检查配置文件，如果没有正确声明此界面将无法打开!!!")
                }
            }
        }
    }
}