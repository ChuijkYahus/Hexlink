package jempasam.hexlink.spirit

import com.google.common.base.Predicates
import net.fabricmc.fabric.api.entity.FakePlayer
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.registry.Registries
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import kotlin.jvm.optionals.getOrNull

class ItemSpirit(val item: Item): Spirit {

    private var color: Int=0
    init{
        if(item.isFireproof)
            color=DyeColor.ORANGE.fireworkColor
        else if(item.isFood)
            color=DyeColor.BROWN.fireworkColor
        else if(item.isEnchantable(item.defaultStack))
            color=DyeColor.YELLOW.fireworkColor
        else if(item.hasGlint(item.defaultStack))
            color=DyeColor.MAGENTA.fireworkColor
        else if(item is BlockItem)
            color=item.block.defaultMapColor.color
        else if(item.getRarity(item.defaultStack)!=Rarity.COMMON)
            color=item.getRarity(item.defaultStack).formatting.colorValue ?: DyeColor.ORANGE.signColor
        else
            color=DyeColor.ORANGE.signColor
    }


    override fun manifestAt(caster: LivingEntity?, world: ServerWorld, position: Vec3d, count: Int): Spirit.Manifestation {
        return Spirit.Manifestation(1,count){
            val player = FakePlayer.get(world)
            player.setStackInHand(Hand.MAIN_HAND, ItemStack(item,it))
            for(i in 0..<it){
                item.useOnBlock(
                    ItemUsageContext(
                        player,
                        Hand.MAIN_HAND,
                        BlockHitResult(position, Direction.UP, BlockPos.ofFloored(position), true)
                    )
                )
            }
        }
    }

    override fun manifestIn(caster: LivingEntity?, world: ServerWorld, entity: Entity, count: Int): Spirit.Manifestation {
        return Spirit.Manifestation(1,count){
            val player = FakePlayer.get(world)
            player.setStackInHand(Hand.MAIN_HAND,ItemStack(item,it))
            if(entity==caster){
                for(i in 0..<it){
                    val action=item.getUseAction(player.mainHandStack)
                    val success=item.use(world, player, Hand.MAIN_HAND)
                    if(action!=UseAction.NONE && action!=UseAction.BLOCK && success.result.isAccepted){
                        item.onStoppedUsing(player.mainHandStack, world, caster, 0)
                        item.finishUsing(player.mainHandStack, world, caster)
                    }
                }
            }
            else{
                for(i in 0..<it) {
                    if (entity is LivingEntity) {
                        item.useOnEntity(player.mainHandStack, player, entity, Hand.MAIN_HAND)
                    }
                    entity.interact(player, Hand.MAIN_HAND)
                }
            }
        }
    }



    override fun lookIn(caster: LivingEntity?, world: ServerWorld, entity: Entity): Boolean {
        val stack= StackHelper.stack(entity, if_entity=StackHelper.inDutyOf(caster)) ?: return false
        val item=stack.stack.item
        return item==this.item
    }

    override fun lookAt(caster: LivingEntity?, world: ServerWorld, position: Vec3d): Boolean {
        val entities=world.getEntitiesByType( EntityType.ITEM, Box.of(position, 0.7, 0.7, 0.7), Predicates.alwaysTrue())
        return entities.any { entity -> entity.stack.item==item }
    }



    override fun equals(other: Any?): Boolean = other is ItemSpirit && item===other.item

    override fun hashCode(): Int = item.hashCode()*36

    override fun getColor(): Int{
        return color
    }

    override fun getName(): Text = item.name

    override fun serialize(): NbtElement {
        return NbtString.of(Registries.ITEM.getId(item).toString())
    }



    override fun getType(): Spirit.SpiritType<*> = Type

    object Type: Spirit.SpiritType<ItemSpirit>{
        override fun getName(): Text {
            return Text.translatable("hexlink.spirit.item")
        }

        override fun deserialize(nbt: NbtElement): ItemSpirit? {
            if(nbt is NbtString){
                val type=Registries.ITEM.getOrEmpty(Identifier(nbt.asString())).getOrNull()
                return if(type!=null) ItemSpirit(type) else null
            }
            else throw IllegalArgumentException()
        }
    }



}