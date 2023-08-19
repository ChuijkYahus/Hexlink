package jempasam.hexlink.entity

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.block.HexlinkBlocks
import jempasam.hexlink.entity.block.HexVortexBlockEntity
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object HexlinkEntities {

    fun <T: BlockEntity>register(id: String, type: BlockEntityType<T>): BlockEntityType<T>{
        Registry.register(Registry.BLOCK_ENTITY_TYPE, Identifier(HexlinkMod.MODID, id), type)
        return type
    }

    val HEX_VORTEX = register("hex_vortex",
            FabricBlockEntityTypeBuilder .create(::HexVortexBlockEntity, HexlinkBlocks.VORTEX) .build()
    )
}