package EasyLib.Utils

import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File

object FileUtils {
    /**
     * 遍历目录下的所有文件
     * @param suffix 文件后缀
     * @param dir 目录
     * @param func 处理函数
     */
    fun File.processDirectory(suffix : String, func : (File, Configuration) -> Unit){
        this.listFiles()?.forEach { childFile ->
            if (childFile.isDirectory){
                childFile.processDirectory(suffix,func)
            }else if (childFile.isFile && childFile.name.endsWith(suffix)){
                val config = Configuration.loadFromFile(childFile, Type.YAML)
                func(childFile,config)
            }
        }
    }
}