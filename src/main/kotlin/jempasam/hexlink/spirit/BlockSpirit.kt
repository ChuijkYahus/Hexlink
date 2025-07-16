package jempasam.hexlink.spirit

import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.entity.FallingBlockEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.registry.Registries
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import kotlin.jvm.optionals.getOrNull

//TODO Long time block interaction with manifestation
class BlockSpirit(val block: Block): Spirit{

    override fun manifestAt(caster: LivingEntity?, world: ServerWorld, position: Vec3d, power: Int): Spirit.Manifestation {
        val startpos=BlockPos.ofFloored(position)
        var testpos=startpos
        var finalPower=0
        while(finalPower<power){
            if(!world.getBlockState(testpos).isAir) break
            testpos=testpos.up()
            finalPower++
        }
        if(finalPower==0)return Spirit.NONE_MANIFESTATION

        return Spirit.Manifestation(3, finalPower){
            var blockpos=startpos
            for(i in 0..<it){
                world.setBlockState(blockpos, block.defaultState)
                blockpos=blockpos.up()
            }
        }
    }

    override fun manifestIn(caster: LivingEntity?, world: ServerWorld, entity: Entity, count: Int): Spirit.Manifestation {
        return Spirit.Manifestation(1,1){
            val pos=BlockPos.ofFloored(entity.pos)
            val state=block.defaultState
            block.onEntityCollision(state, world, pos, entity)
            block.onSteppedOn(world, pos, state, entity)
            if(entity is PlayerEntity){
                block.onUse(state, world, pos, entity, Hand.MAIN_HAND, BlockHitResult(entity.pos, Direction.UP, BlockPos.ofFloored(entity.pos), true))
            }
        }
    }

    override fun manifestBetween(caster: LivingEntity?, world: ServerWorld, from: Vec3d, to: Vec3d, count: Int): Spirit.Manifestation {
        val blockpos=BlockPos.ofFloored(from)
        if(!world.getBlockState(blockpos).isAir)return Spirit.NONE_MANIFESTATION
        return Spirit.Manifestation(1,count){
            var direction=to.subtract(from)
            if(direction.length()>5)direction=direction.normalize().multiply(5.0)
            for(i in 0..<it){
                val falling=FallingBlockEntity.spawnFromBlock(world, blockpos, block.defaultState)
                falling.setVelocity(direction.x, direction.y+i*0.5, direction.z)
            }
        }
    }

    override fun manifestBetween(caster: LivingEntity?, world: ServerWorld, from: Entity, to: Vec3d, count: Int): Spirit.Manifestation {
        return super.manifestBetween(caster, world, from.pos, to, count)
    }

    override fun manifestBetween(caster: LivingEntity?, world: ServerWorld, from: Entity, to: Entity, count: Int): Spirit.Manifestation {
        return super.manifestBetween(caster, world, from.pos, to.pos, count)
    }


    override fun lookAt(caster: LivingEntity?, world: ServerWorld, position: Vec3d): Boolean {
        return world.getBlockState(BlockPos.ofFloored(position)).block==block
    }

    override fun lookIn(caster: LivingEntity?, world: ServerWorld, entity: Entity): Boolean {
        val stack= StackHelper.stack(entity, {true}, StackHelper.inDutyOf(caster)) ?: return false
        val item=stack.stack.item
        if(item !is BlockItem)return false
        return item.block==block
    }



    override fun equals(other: Any?): Boolean = other is BlockSpirit && block===other.block

    override fun hashCode(): Int = block.hashCode()*36



    override fun getColor(): Int = block.defaultMapColor.color

    override fun getName(): Text = block.name

    override fun serialize(): NbtElement {
        return NbtString.of(Registries.BLOCK.getId(block).toString())
    }



    override fun getType(): Spirit.SpiritType<*> = Type

    object Type: Spirit.SpiritType<BlockSpirit>{
        override fun getName(): Text {
            return Text.translatable("hexlink.spirit.block")
        }

        override fun deserialize(nbt: NbtElement): BlockSpirit? {
            if(nbt is NbtString){
                val type=Registries.BLOCK.getOrEmpty(Identifier(nbt.asString())).getOrNull() ?: return null
                return BlockSpirit(type)
            }
            else throw IllegalArgumentException()
        }
    }

}