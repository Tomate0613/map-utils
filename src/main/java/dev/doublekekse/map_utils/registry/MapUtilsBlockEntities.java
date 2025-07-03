package dev.doublekekse.map_utils.registry;

import dev.doublekekse.map_utils.MapUtils;
import dev.doublekekse.map_utils.block.timer.TimerBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static dev.doublekekse.map_utils.registry.MapUtilsBlocks.TIMER_BLOCK;

public class MapUtilsBlockEntities {
    public static final BlockEntityType<TimerBlockEntity> TIMER_BLOCK_ENTITY = register("timer_block", TimerBlockEntity::new, TIMER_BLOCK);


    private static <T extends BlockEntity> BlockEntityType<T> register(
        String path,
        FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
        Block... blocks
    ) {
        var location = MapUtils.id(path);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, location, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }

    public static void register() {

    }
}
