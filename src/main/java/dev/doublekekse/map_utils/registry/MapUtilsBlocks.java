package dev.doublekekse.map_utils.registry;

import dev.doublekekse.map_utils.MapUtils;
import dev.doublekekse.map_utils.block.EntityBarrier;
import dev.doublekekse.map_utils.block.VariableRedstoneBlock;
import dev.doublekekse.map_utils.block.timer.TimerBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class MapUtilsBlocks {
    public static final VariableRedstoneBlock VARIABLE_REDSTONE_BLOCK = registerWithItem(
        new VariableRedstoneBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.FIRE)
            .requiresCorrectToolForDrops()
            .strength(5.0F, 6.0F)
            .sound(SoundType.METAL)
            .isRedstoneConductor(Blocks::never)
        ), "variable_redstone_block");
    public static final EntityBarrier ENTITY_BARRIER_BLOCK = registerWithItem(
        new EntityBarrier(BlockBehaviour.Properties.of()
            .strength(-1.0F, 3600000.8F)
            .mapColor(MapColor.NONE)
            .noLootTable()
            .noOcclusion()
            .isValidSpawn(Blocks::never)
            .noTerrainParticles()
            .pushReaction(PushReaction.BLOCK)
        ), "entity_barrier"
    );
    public static final TimerBlock TIMER_BLOCK = registerWithItem(
        new TimerBlock(BlockBehaviour.Properties.of()
            .sound(SoundType.COPPER_BULB)
        ), "timer_block"
    );

    private static <T extends Block> T register(T block, String path) {
        ResourceLocation blockId = MapUtils.id(path);
        return Registry.register(BuiltInRegistries.BLOCK, blockId, block);
    }

    private static <T extends Block> T registerWithItem(T block, String path) {
        ResourceLocation blockId = MapUtils.id(path);
        Registry.register(BuiltInRegistries.ITEM, blockId, new BlockItem(block, new Item.Properties()));
        return Registry.register(BuiltInRegistries.BLOCK, blockId, block);
    }

    public static void register() {
    }
}
