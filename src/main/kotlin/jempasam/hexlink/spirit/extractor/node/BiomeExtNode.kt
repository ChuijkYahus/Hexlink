package jempasam.hexlink.spirit.extractor.node

import com.google.gson.JsonObject
import com.mojang.serialization.Codec

object BiomeExtNode : ExtractionNode {
    override fun filter(source: ExtractionNode.Source): ExtractionNode.Source {
        //TODO val biome=source.entity.world.getBiome(BlockPos(source.entity.pos))
        return source.with {
            //TODO spirit=BiomeSpirit(biome)
        }
    }

    object Parser: ExtractionNode.Parser<BiomeExtNode> {
        override fun parse(obj: JsonObject): BiomeExtNode = BiomeExtNode
    }

    val CODEC=Codec.unit(BiomeExtNode)

}