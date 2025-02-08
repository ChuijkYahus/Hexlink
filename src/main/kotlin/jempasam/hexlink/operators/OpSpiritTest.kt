package jempasam.hexlink.operators

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.BooleanIota
import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.spell.iota.Vec3Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import jempasam.hexlink.iota.SpiritIota
import net.minecraft.text.Text

class OpSpiritTest : ConstMediaAction {
    override val argc: Int
        get() = 2

    override fun execute(args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
        val spirit=args[0]
        val target=args[1]
        return if(spirit is SpiritIota){
            when (target) {
                is Vec3Iota ->
                    listOf(BooleanIota(spirit.getSpirit().lookAt(ctx.castingEntity,ctx.world,target.vec3)))
                is EntityIota ->
                    listOf(BooleanIota(spirit.getSpirit().lookIn(ctx.castingEntity,ctx.world,target.entity)))
                else ->
                    throw MishapInvalidIota(target, 0, Text.translatable("hexcasting.iota.hexcasting:entity").append(Text.translatable("hexlink.or")).append(Text.translatable("hexcasting.iota.hexcasting:vec3")))
            }
        }
        else throw MishapInvalidIota(spirit,1, Text.translatable("hexlink.spirit_iota"))
    }
}