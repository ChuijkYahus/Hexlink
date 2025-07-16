package jempasam.hexlink.creative_tab

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.item.HexlinkItems
import jempasam.hexlink.spirit.SpecialSpirit
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object HexlinkCreativeTab {

    val MAIN_TAB: ItemGroup = FabricItemGroup.builder()
        .displayName(Text.translatable(Identifier(HexlinkMod.MODID,"hexlink").toTranslationKey("itemGroup")))
            .icon { HexlinkItems.MediumBag.defaultStack }
            .entries{ context, list ->
                val spirits=HexlinkRegistry.SPECIAL_SPIRIT.getEntrySet().map { it.value }.toList()

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

                for(extractor in HexlinkRegistry.EXTRACTOR.getEntrySet()){
                    val stack=HexlinkItems.Crystal.defaultStack
                    HexlinkItems.Crystal.setExtractor(stack, extractor.value)
                    list.add(stack)
                }

                for(type in HexlinkRegistry.SPECIAL_SPIRIT.getEntrySet()){
                    val stack=HexlinkItems.Spirit.defaultStack
                    HexlinkItems.Spirit.setSpirit(stack, SpecialSpirit(type.value))
                    list.add(stack)
                }

            }
            .build()

    fun registerTabs(){
        Registry.register(Registries.ITEM_GROUP, HexlinkMod/"main_tab", MAIN_TAB)
    }

}