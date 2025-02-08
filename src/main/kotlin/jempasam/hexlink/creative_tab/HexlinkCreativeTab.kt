package jempasam.hexlink.creative_tab

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.item.HexlinkItems
import jempasam.hexlink.spirit.SpecialSpirit
import net.minecraft.item.ItemGroup
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object HexlinkCreativeTab {

    val MAIN_TAB: ItemGroup = ItemGroup.create(null,-1)
        .displayName(Text.translatable(Identifier(HexlinkMod.MODID,"hexlink").toTranslationKey("itemGroup")))
            .icon { HexlinkItems.MediumBag.defaultStack }
            .entries{ context, list ->
                val spirits=HexlinkRegistry.SPECIAL_SPIRIT.entrySet.map { it.value }.toList()

                list.add(HexlinkItems.BigTablet.defaultStack)
                list.add(HexlinkItems.BigBag.defaultStack)
                list.add(HexlinkItems.MediumBag.defaultStack)
                list.add(HexlinkItems.SmallBag.defaultStack)
                list.add(HexlinkItems.Tablet.defaultStack)

                list.add(HexlinkItems.PhilosophicalCrystal.let {
                    val stack=it.defaultStack
                    it.getSpirits(stack).apply {
                        add(SpecialSpirit(spirits.random()))
                        add(SpecialSpirit(spirits.random()))
                    }
                    stack
                })

                list.add(HexlinkItems.HauntedCrystal.let {
                    val stack=it.defaultStack
                    it.getSpirits(stack).apply {
                        add(SpecialSpirit(spirits.random()))
                        add(SpecialSpirit(spirits.random()))
                    }
                    stack
                })

                list.add(HexlinkItems.TabletStaff.defaultStack)
                list.add(HexlinkItems.SpiritStaff.defaultStack)
                list.add(HexlinkItems.BigTabletStaff.defaultStack)
                list.add(HexlinkItems.PureMediaStaff.defaultStack)

                list.add(HexlinkItems.Vortex.defaultStack)

                for(i in 1..4){
                    val pigment=HexlinkItems.MixedPigment.defaultStack
                    HexlinkItems.MixedPigment.setColor1(pigment, (Math.random()*0xFFFFFF).toInt())
                    HexlinkItems.MixedPigment.setColor2(pigment, (Math.random()*0xFFFFFF).toInt())
                    list.add(pigment)
                }

                for(extractor in HexlinkRegistry.EXTRACTOR.entrySet){
                    val stack=HexlinkItems.Crystal.defaultStack
                    HexlinkItems.Crystal.setExtractor(stack, extractor.value)
                    list.add(stack)
                }

                for(type in HexlinkRegistry.SPECIAL_SPIRIT.entrySet){
                    val stack=HexlinkItems.Spirit.defaultStack
                    HexlinkItems.Spirit.setSpirit(stack, SpecialSpirit(type.value))
                    list.add(stack)
                }

            }
            .build()

}