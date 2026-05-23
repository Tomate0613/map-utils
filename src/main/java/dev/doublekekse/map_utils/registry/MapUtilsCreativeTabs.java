package dev.doublekekse.map_utils.registry;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.CreativeModeTabs;

import static dev.doublekekse.map_utils.registry.MapUtilsBlocks.*;

public class MapUtilsCreativeTabs {
    public static void register() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(content -> {
            content.accept(TRIGGERABLE_REDSTONE_BLOCK);
            content.accept(VARIABLE_REDSTONE_BLOCK);
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.OP_BLOCKS).register(content -> {
            content.accept(TRIGGERABLE_REDSTONE_BLOCK);
            content.accept(VARIABLE_REDSTONE_BLOCK);
            content.accept(TIMER_BLOCK);
        });
    }
}
