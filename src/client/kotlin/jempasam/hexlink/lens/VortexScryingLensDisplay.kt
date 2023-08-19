package jempasam.hexlink.lens

import at.petrak.hexcasting.api.client.ScryingLensOverlayRegistry.OverlayBuilder
import com.mojang.datafixers.util.Pair
import jempasam.hexlink.block.functionnality.BlockSpiritContainer
import jempasam.hexlink.item.HexlinkItems
import jempasam.hexlink.spirit.Spirit
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class VortexScryingLensDisplay : OverlayBuilder {
    override fun addLines(lines: MutableList<Pair<ItemStack, Text>>, state: BlockState, pos: BlockPos, observer: PlayerEntity, world: World, hitFace: Direction){
        val block=state.block
        if(block is BlockSpiritContainer){
            val addItemToLens={ spirit: Spirit, count: Int ->
                val icon= HexlinkItems.Tablet.defaultStack
                HexlinkItems.Tablet.setSpirit(icon, spirit)
                lines.add(Pair.of(icon, Text.of(count.toString()).copy().append(spirit.getName())))
            }

            var count=0
            var last: Spirit?=null
            var actual_count=0

            val content= block.getSpiritContent(world,pos)

            for(spirit in content){
                if(last==null || last==spirit){
                    actual_count++
                    last=spirit
                }
                else{
                    addItemToLens(last,actual_count)
                    count++
                    if(count>=10)break
                    actual_count=0
                    last=spirit
                }
            }
            if(count<10 && last!=null){
                addItemToLens(last,actual_count)
            }
        }
    }
}