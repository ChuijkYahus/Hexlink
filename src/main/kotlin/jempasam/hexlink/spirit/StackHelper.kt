package jempasam.hexlink.spirit

import net.minecraft.block.entity.HopperBlockEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.ItemFrameEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

object StackHelper {

    class WorldStack(val stack: ItemStack, val killer: ()->Unit, val update: ()->Unit, val replace: (ItemStack)->Unit)

    // Stack from entity
    fun stack(target:Entity, if_stack:(ItemStack)->Boolean = {true}, if_entity:(Entity)->Boolean = {true}): WorldStack?{
        if(!if_entity(target))return null
        when(target){
            is ItemEntity -> return if(if_stack(target.stack)) WorldStack( // Dropped item world stack
                target.stack,
                {target.kill()},
                {target.stack=target.stack.copy()},
                {target.stack=it}
            ) else null

            is ItemFrameEntity -> return if(if_stack(target.heldItemStack)) WorldStack( // Item frame world stack
                target.heldItemStack,
                {target.heldItemStack=ItemStack.EMPTY},
                {target.heldItemStack=target.heldItemStack},
                {target.heldItemStack=it}
            ) else null

            is LivingEntity ->{
                val hand = when {
                    if_stack(target.mainHandStack) -> Hand.MAIN_HAND
                    if_stack(target.offHandStack) -> Hand.OFF_HAND
                    else -> null
                }
                if(hand!=null) return WorldStack( // Main hand/Off hand living entity world stack
                    target.getStackInHand(hand),
                    {target.setStackInHand(hand, ItemStack.EMPTY)},
                    {target.setStackInHand(hand, target.getStackInHand((hand)))},
                    {target.setStackInHand(hand, it)},
                )

                if(target is PlayerEntity){
                    val inventory = target.inventory
                    for(i in 0 until inventory.size()){
                        val stack= inventory.getStack(i)
                        if(if_stack(stack)) return WorldStack( // Player inventory world stack
                            stack,
                            {inventory.setStack(i, ItemStack.EMPTY)},
                            {inventory.setStack(i, inventory.getStack(i))},
                            {inventory.setStack(i, it)}
                        )
                    }
                }

                return null
            }
            else -> return null
        }
    }

    // Stack from block pos
    fun stack(world: ServerWorld, pos: BlockPos, if_stack:(ItemStack)->Boolean = {true}, if_entity:(Entity)->Boolean = {true}): WorldStack?{
        val bpos=BlockPos(pos)
        val blockInv=HopperBlockEntity.getInventoryAt(world,bpos)
        if(blockInv!=null){
            for (i in 0 until blockInv.size()) {
                val stack = blockInv.getStack(i)
                if (stack.isEmpty || !if_stack(stack)) continue
                return WorldStack(
                    stack,
                    {blockInv.setStack(i, ItemStack.EMPTY)},
                    {blockInv.setStack(i, blockInv.getStack(i))},
                    {blockInv.setStack(i,it)}
                )
            }
        }
        else{
            val entities=world.getOtherEntities(null, Box(bpos))
            for(entity in entities){
                val ret=stack(entity, if_stack, if_entity)
                if(ret!=null)return ret
            }
        }
        return null
    }

    fun inDutyOf(caster: LivingEntity?) = {target:Entity -> target !is PlayerEntity || target!=caster}

}