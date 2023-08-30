package jempasam.hexlink.block

import jempasam.hexlink.block.functionnality.BlockSpiritContainer
import jempasam.hexlink.block.functionnality.BlockSpiritSource
import jempasam.hexlink.block.functionnality.BlockSpiritTarget
import jempasam.hexlink.entity.HexlinkEntities
import jempasam.hexlink.entity.block.HexVortexBlockEntity
import jempasam.hexlink.particle.HexlinkParticles
import jempasam.hexlink.spirit.Spirit
import jempasam.hexlink.spirit.inout.SpiritSource
import jempasam.hexlink.spirit.inout.SpiritTarget
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import kotlin.streams.asSequence

class HexVortexBlock(settings: Settings) : BlockWithEntity(settings), BlockSpiritSource, BlockSpiritTarget, BlockSpiritContainer{

    companion object{
        fun coloredParticle(world: World, pos: BlockPos, color: Int, count: Int){
            val center=Vec3d.of(pos)
            val r = (color shr 16 and 0xFF).toDouble() / 255.0
            val g = (color shr 8 and 0xFF).toDouble() / 255.0
            val b = (color shr 0 and 0xFF).toDouble() / 255.0
            for (j in 0 until count) {
                val pos=center.add(Math.random(), Math.random(),Math.random())
                world.addParticle(
                        HexlinkParticles.SPIRIT,
                        pos.x, pos.y, pos.z,
                        r, g, b
                )
            }
        }
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return HexlinkEntities.HEX_VORTEX.instantiate(pos,state)
    }

    @Deprecated("Call AbstractBlockState.getRenderType")
    override fun getRenderType(state: BlockState): BlockRenderType = BlockRenderType.MODEL


    override fun <T : BlockEntity?> getTicker(world: World?, state: BlockState?, type: BlockEntityType<T>?): BlockEntityTicker<T>? {
        return checkType(type, HexlinkEntities.HEX_VORTEX){w,p,s,be -> be.tick(w,p,s)}
    }

    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
        if (!state.isOf(newState.block) && world is ServerWorld) {
            val center=Vec3d.ofCenter(pos)
            world.spawnParticles(
                    ParticleTypes.CLOUD,
                    center.x, center.y, center.z,
                    10,
                    0.5, 0.5, 0.5,
                    0.1
            )
        }

        super.onStateReplaced(state, world, pos, newState, moved)
    }



    fun addAt(world: ServerWorld, pos: BlockPos, spirit: Spirit?=null): Boolean{
        val bstate=world.getBlockState(pos)
        val block=bstate.block
        if(bstate.isAir){
            world.setBlockState(pos,defaultState)
        }
        else if(block!=this)return false
        val vortexEntity=world.getBlockEntity(pos)
        vortexEntity as HexVortexBlockEntity
        if(spirit!=null)vortexEntity.give(spirit)
        return true
    }

    fun canAddAt(world: ServerWorld, pos: BlockPos): Boolean{
        val bstate=world.getBlockState(pos)
        val block=bstate.block
        return bstate.isAir || block==this
    }

    override fun getSpiritSource(world: ServerWorld, pos: BlockPos): SpiritSource {
        val vortexentity=world.getBlockEntity(pos)
        if(vortexentity is HexVortexBlockEntity){
            return object: SpiritSource{
                override fun extract(count: Int, spirit: Spirit): SpiritSource.SpiritOutputFlux {
                    var currentCount=0
                    val removedOutput= mutableListOf<Int>()
                    val removedInput= mutableListOf<Int>()
                    for(i in (vortexentity.output.size-1) downTo 0){
                        val spi=vortexentity.output[i]
                        if(spi==spirit){
                            currentCount++
                            removedOutput.add(i)
                            if(currentCount>=count)break
                        }
                    }
                    if(currentCount<count)for(i in (vortexentity.input.size-1)downTo 0){
                        val spi=vortexentity.input[i]
                        if(spi==spirit){
                            currentCount++
                            removedInput.add(i)
                            if(currentCount>=count)break
                        }
                    }
                    return SpiritSource.SpiritOutputFlux({
                        vortexentity.age=0
                        var i=0
                        for(id in removedOutput){
                            i++
                            if(i>it)break
                            vortexentity.output.removeAt(id)
                        }
                        for(id in removedInput){
                            i++
                            if(i>it)break
                            vortexentity.input.removeAt(id)
                        }
                        vortexentity.markDirty()
                        vortexentity.sendToClient()
                    }, currentCount)
                }

                override fun last(): Spirit? {
                    if(vortexentity.output.isNotEmpty())return vortexentity.output.last()
                    if(vortexentity.input.isNotEmpty())return vortexentity.input.last()
                    return null
                }
            }
        }
        else return SpiritSource.NONE
    }

    override fun getSpiritTarget(world: ServerWorld, pos: BlockPos): SpiritTarget {
        val vortexentity=world.getBlockEntity(pos)
        if(vortexentity is HexVortexBlockEntity){
            return object: SpiritTarget{
                override fun fill(count: Int, spirit: Spirit): SpiritTarget.SpiritInputFlux {
                    return SpiritTarget.SpiritInputFlux({
                        vortexentity.age=0
                        vortexentity.loading=0
                        for(i in 0..<count)vortexentity.input.add(spirit)
                        vortexentity.markDirty()
                        vortexentity.sendToClient()
                    }, count)
                }
            }
        }
        else return SpiritTarget.NONE
    }

    override fun getSpiritContent(slot: Int, world: World, pos: BlockPos): Sequence<Spirit> {
        val vortex=world.getBlockEntity(pos)
        if(vortex is HexVortexBlockEntity){
            if(slot==0)return vortex.input.asSequence()
            else if(slot==1)return vortex.output.asSequence()
        }
        return listOf<Spirit>().stream().asSequence()
    }

    override fun getSlotCount(): Int = 2
}