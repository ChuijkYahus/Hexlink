package jempasam.hexlink.spirit

import jempasam.hexlink.HexlinkRegistry
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import kotlin.jvm.optionals.getOrNull

class SpecialSpirit(val specialType: SpecialType) : Spirit {
    override fun getColor(): Int = specialType.color

    override fun getName(): Text = specialType.text

    override fun equals(other: Any?): Boolean = other is SpecialSpirit && specialType===other.specialType

    override fun hashCode(): Int = specialType.hashCode()

    override fun lookAt(caster: LivingEntity?, world: ServerWorld, position: Vec3d): Boolean
        = specialType.lookAt.lookAt(caster, world, position)

    override fun lookIn(caster: LivingEntity?, world: ServerWorld, entity: Entity): Boolean
        = specialType.lookIn.lookIn(caster, world, entity)

    override fun manifestAt(caster: LivingEntity?, world: ServerWorld, position: Vec3d, count: Int): Spirit.Manifestation
        = specialType.manifestAt.manifestAt(caster, world, position, count)

    override fun manifestIn(caster: LivingEntity?, world: ServerWorld, entity: Entity, count: Int): Spirit.Manifestation
        = specialType.manifestIn.manifestIn(caster, world, entity, count)

    override fun serialize(): NbtElement {
        return NbtString.of(HexlinkRegistry.SPECIAL_SPIRIT.getKey(specialType).getOrNull()?.value?.toString() ?: "")
    }

    override fun getType(): Spirit.SpiritType<*> = Type

    object Type: Spirit.SpiritType<SpecialSpirit>{
        override fun getName(): Text
            = Text.translatable("hexlink.spirit.special")

        override fun deserialize(nbt: NbtElement): SpecialSpirit?
            = HexlinkRegistry.SPECIAL_SPIRIT.get(Identifier.tryParse(nbt.asString()))?.let { SpecialSpirit(it) }
    }

    class SpecialType(val manifestAt: Spirit, val manifestIn: Spirit, val lookAt: Spirit, val lookIn: Spirit, val text: Text, val color: Int)
}