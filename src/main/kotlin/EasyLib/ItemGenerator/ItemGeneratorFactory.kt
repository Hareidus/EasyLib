package EasyLib.ItemGenerator


import EasyLib.ItemGenerator.Implement.any
import EasyLib.ItemGenerator.ItemGenerator

object ItemGeneratorFactory {
    fun getItemGenerator() : ItemGenerator {
        return any()
    }

}