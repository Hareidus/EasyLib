package EasyLib.EasyGui.EasyGuiConfig.GuiConfig


import EasyLib.EasyGui.EasyGuiConfig.GuiConfig.GuiInterface.Companion.getEffectChar
import EasyLib.EasyGui.Exceptions.GuiBuildException
import org.bukkit.Sound
import taboolib.common.platform.function.info
import taboolib.common.platform.function.warning
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import java.io.File


open class  GuiConfig(private val config: Configuration) : GuiInterface {

    fun reload(){
        checkForCompleteness()
    }

    open fun checkForCompleteness() {
        val keySet = getGuiKey()

        for(key in keySet){
            val section = getKeySection()?.getConfigurationSection(key.toString())
            val function = section?.getString("IconFunction")
//            if(function == "Start"){
//                val isUsed = getUsedChar().contains(key.toCharArray()[0])
//                if (!isUsed) {
//                    warning(" Start 展示节点 $key 未声明在GuiPlain中，请检查配置文件，如果没有正确声明此界面将无法打开!!!")
//                }
//            }
        }
    }


    override fun getOpenSound(): Sound? {
        try {
            val soundString = Sound.valueOf(config.getString("OpenSound")?.uppercase()?.replace(" ","_") ?: "")
            return soundString
        }catch (e: Exception){
            if(config.getString("OpenSound") != null){
                warning("${config.getString("OpenSound")}不是合法的Sound")
            }
        }
        return null
    }
    override fun getCloseSound(): Sound? {
        //return Sound.valueOf(config.getString("CloseSound")?.uppercase()?.replace(" ","_") ?: "")
        try {
            val soundString = Sound.valueOf(config.getString("CloseSound")?.uppercase()?.replace(" ","_") ?: "")
            return soundString
        }catch (e: Exception){
            if(config.getString("CloseSound") != null){
                warning("${config.getString("CloseSound")}不是合法的Sound")
            }
        }
        return null
    }



    override fun getSound(icons: String): Sound? {
        //return Sound.valueOf(config.getConfigurationSection("GuiKey")?.getConfigurationSection(icons)?.getString("Sound")?.uppercase()?.replace("","_") ?: "")
        try {
            val soundString = Sound.valueOf(config.getConfigurationSection("GuiKey")?.getConfigurationSection(icons)?.getString("Sound")?.uppercase()?.replace(" ","_") ?: "")
            return soundString
        }catch (e: Exception){
            if(config.getConfigurationSection("GuiKey")?.getConfigurationSection(icons)?.getString("Sound") != null){
                warning("${config.getConfigurationSection("GuiKey")?.getConfigurationSection(icons)?.getString("Sound")}不是合法的Sound")
            }
        }
        return null
    }

    override fun getName(): String {
        return config.getString("name") ?: "未命名"
    }

    /**
     * 获取GUI布局的字符串列表。
     * @return 包含GUI布局的字符串列表。
     */
    override fun getMap(): List<String> {
        return config.getStringList("GuiPlain")
    }

    /**
     * 获取GUI键(GuiKey)的配置节。
     * @return 包含GUI键配置的ConfigurationSection对象，如果不存在则返回null。
     */
    override fun getKeySection(): ConfigurationSection? {
        return config.getConfigurationSection("GuiKey")
    }

    /**
     * 获取GUI中使用的字符列表。
     * @return 包含GUI中使用的字符列表。
     */
    override fun getUsedChar(): List<Char> {
        return GuiInterface.getUsedChar(getMap())
    }

    /**
     * 获取GUI中声明的字符列表。
     * @return 包含GUI中声明的字符列表。
     */
    override fun getDeclareChar(): List<Char> {
        return getKeySection().let { getEffectChar(it) }
    }

    /**
     * 获取GUI的标题。
     * @return GUI的标题字符串，如果配置中不存在则返回默认标题。
     */
    override fun getTitle(): String {
        return config.getString("Title")?.replace("&", "§") ?: "§cGuiTitle - 未命名"
    }

    /**
     * 获取配置文件的绝对路径。
     * @return 配置文件的绝对路径字符串。
     */
    override fun getAbsolutePath(): String {
        info(config.file?.absolutePath)
        return config.file?.absolutePath ?: ""
    }

    /**
     * 监听配置文件的变化，当配置文件发生变化时重新加载配置并通知所有打开的GUI视图。
     */

    /**
     * 获取指定键的图标功能。
     * @param icons 获取的图标功能键
     * @return 指定键的图标功能字符串，如果不存在则返回"unKnow"。
     */
    override fun getIconFunction(icons: String): String {
        return config.getConfigurationSection("GuiKey")?.getConfigurationSection(icons)?.getString("IconFunction") ?: "unKnow"
    }

    /**
     * 重新加载配置文件。
     */
    override fun reloadConfig(func : () -> Unit) {
        info(config.file?.absolutePath)
        config.reload()
        func()
    }

    /**
     * 获取GuiKey的键集合
     */
    fun getGuiKey() : Set<String> {
        return config.getConfigurationSection("GuiKey")?.getKeys(false) ?: setOf()
    }

    fun getFile() : File {
        if (config.file == null) throw GuiBuildException("Gui构建失败 : 配置文件不存在")
        return config.file!!
    }

    companion object{
        /**
         * 验证配置完整性
         * @param guiConfig 配置
         * @param functionSet 功能图标集合
         * @throws GuiBuildException 如果配置不完整或有错误
         *
         */
        fun validate(guiConfig : GuiConfig,functionSet : Set<String>) {
            val errors = mutableListOf<String>()
            // 验证标题
            if (guiConfig.getTitle().isEmpty() || guiConfig.getTitle() == "§cGuiTitle - 未命名") {
                errors.add("GUI标题未设置或无效")
            }

            // 验证布局
            if (guiConfig.getMap().isEmpty()) {
                errors.add("GUI布局(GuiPlain)未设置")
            }

            // 验证布局字符一致性
            val usedChars = guiConfig.getUsedChar().toSet()
            val declaredChars = guiConfig.getDeclareChar().toSet()
            val undeclaredChars = usedChars - declaredChars
            if (undeclaredChars.isNotEmpty()) {
                errors.add("以下字符在布局中使用但未在GuiKey中声明: $undeclaredChars")
            }

            // 验证关键功能图标
            val requiredFunctions = functionSet
            val availableFunctions = guiConfig.getDeclareChar().map { guiConfig.getIconFunction(it.toString()) }
            val missingFunctions = requiredFunctions - availableFunctions.toSet()
            if (missingFunctions.isNotEmpty()) {
                errors.add("缺少必需的功能图标: $missingFunctions")
            }

            if (errors.isNotEmpty()) {
                throw GuiBuildException("GUI配置验证失败:\n${errors.joinToString("\n")}")
            }
        }
    }
}
