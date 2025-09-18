package EasyCrate.Factory.ItemGenerator


import EasyCrate.Factory.ItemGenerator.Implement.any

object ItemGeneratorFactory {
    fun getItemGenerator() : ItemGenerator {
        return any()
    }

}