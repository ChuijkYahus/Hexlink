package jempasam.hexlink.recipe.vortex

import com.google.gson.JsonObject
import jempasam.hexlink.spirit.ItemSpirit
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritHelper
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.Item
import net.minecraft.recipe.RecipeManager
import net.minecraft.recipe.RecipeType
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.JsonHelper
import net.minecraft.world.World
import kotlin.math.max
import kotlin.math.min

class SmeltingVortexHandler : AbstractVortexHandler {

    val multiplier: Float

    constructor(catalyzer: List<Spirit>, output: List<Spirit>, multiplier: Float)
            : super(catalyzer, output)
    {
        this.multiplier=multiplier
    }

    constructor(obj: JsonObject)
            : super(obj)
    {
        this.multiplier=JsonHelper.getFloat(obj, "multiplier", 1.0f)
    }

    override fun serialize(obj: JsonObject){
        super.serialize(obj)
        obj.addProperty("multiplier", multiplier)
    }


    private val recipeManager=RecipeManager.createCachedMatchGetter(RecipeType.SMELTING)

    override fun findRealRecipe(ingredients: Collection<Spirit>, world: ServerWorld): AbstractVortexHandler.Recipe? {
        if(ingredients.isNotEmpty()){
            val ingredient=ingredients.first()
            val item= SpiritHelper.asItem(ingredient)
            if(item!=null){
                val inventory=SimpleInventory(1)
                inventory.setStack(0, item.defaultStack)
                val cookingRecipe=recipeManager.getFirstMatch(inventory,world)
                if(cookingRecipe.isPresent){
                    val result=cookingRecipe.get().craft(inventory, world.registryManager)
                    if(!result.isEmpty){
                        return Recipe(result.item, min(1,(result.count*multiplier).toInt()), this, world)
                    }
                }
            }
        }
        return null
    }

    override fun getRealRecipesExamples(world: World): Sequence<Pair<List<HexVortexHandler.Ingredient>, List<Spirit>>>{
        return sequence {
            for (recipe in world.recipeManager.listAllOfType(RecipeType.SMELTING)){
                val output = recipe.getOutput(world.registryManager)
                yield(Pair(
                    listOf(HexVortexHandler.Ingredient(recipe.ingredients[0])),
                    List(max(1,(multiplier*output.count).toInt())){ SpiritHelper.asSpirit(output.item) },
                ))
            }
        }
    }

    class Recipe(val item: Item, val count: Int, handler: SmeltingVortexHandler, val world: ServerWorld): AbstractVortexHandler.Recipe(handler){
        override fun realIngredientCount(): Int = 1

        override fun realMix(ingredients: Collection<Spirit>): List<Spirit> {
            val ret= mutableListOf<Spirit>()
            for(i in 0..<count)ret.add(ItemSpirit(item))
            return ret
        }

    }

    override val parser get() = PARSER

    object PARSER: HexVortexHandler.Parser<SmeltingVortexHandler> {
        override fun parse(json: JsonObject): SmeltingVortexHandler = SmeltingVortexHandler(json)
    }
}