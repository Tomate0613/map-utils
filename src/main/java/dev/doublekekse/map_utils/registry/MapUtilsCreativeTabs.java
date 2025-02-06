package dev.doublekekse.map_utils.registry;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;

import static dev.doublekekse.map_utils.registry.MapUtilsBlocks.*;

public class MapUtilsCreativeTabs {
    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(content -> {
            content.accept(VARIABLE_REDSTONE_BLOCK);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.OP_BLOCKS).register(content -> {
            content.accept(ENTITY_BARRIER_BLOCK);
            content.accept(VARIABLE_REDSTONE_BLOCK);
            content.accept(TIMER_BLOCK);
        });
    }
}
