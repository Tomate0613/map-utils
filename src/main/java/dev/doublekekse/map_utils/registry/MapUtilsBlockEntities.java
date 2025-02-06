package dev.doublekekse.map_utils.registry;

import dev.doublekekse.map_utils.MapUtils;
import dev.doublekekse.map_utils.block.timer.TimerBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static dev.doublekekse.map_utils.registry.MapUtilsBlocks.TIMER_BLOCK;

public class MapUtilsBlockEntities {
    public static final BlockEntityType<TimerBlockEntity> TIMER_BLOCK_ENTITY = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        MapUtils.id("timer_block"),
        BlockEntityType.Builder.of(TimerBlockEntity::new, TIMER_BLOCK).build()
    );

    public static void register() {

    }
}
