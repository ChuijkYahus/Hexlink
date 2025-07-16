package jempasam.hexlink.recipe.vortex

import com.google.gson.JsonObject
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritHelper
import net.minecraft.recipe.RecipeManager
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.World

interface HexVortexHandler {

    fun findRecipe(ingredients: Collection<Spirit>, world: ServerWorld): Recipe?

    fun getRecipesExamples(world: World): Sequence<Pair<List<Ingredient>,List<Spirit>>> = sequenceOf()

    fun serialize(json: JsonObject)

    val parser: Parser<*>

    interface Recipe{
        fun mix(ingredients: Collection<Spirit>): List<Spirit>
        fun ingredientCount(): Int
    }

    class Ingredient(val content: Sequence<Spirit>, val hashcode: Int): Sequence<Spirit>{

        constructor(ingredient: net.minecraft.recipe.Ingredient?): this(
                if(ingredient==null) sequenceOf<Spirit>()
                else sequence {
                    for(s in ingredient.matchingStacks){
                        yield(SpiritHelper.asSpirit(s.item))
                    }
                },
                ingredient.hashCode()
        )

        constructor(spirit: Spirit): this(sequenceOf(spirit), spirit.hashCode())
        override fun iterator(): Iterator<Spirit> = content.iterator()

        override fun hashCode(): Int = hashcode

        override fun equals(other: Any?): Boolean = other is Ingredient && other.hashcode==hashcode
    }

    interface Parser<T: HexVortexHandler>{
        fun parse(json: JsonObject): T
    }
}

