package dev.doublekekse.map_utils.registry;

import dev.doublekekse.map_utils.MapUtils;
import dev.doublekekse.map_utils.block.TriggerableRedstoneBlock;
import dev.doublekekse.map_utils.block.VariableRedstoneBlock;
import dev.doublekekse.map_utils.block.timer.TimerBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;

public class MapUtilsBlocks {
    public static final VariableRedstoneBlock VARIABLE_REDSTONE_BLOCK = register(
        VariableRedstoneBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.FIRE)
            .requiresCorrectToolForDrops()
            .strength(5.0F, 6.0F)
            .sound(SoundType.METAL)
            .isRedstoneConductor(Blocks::never)
        , "variable_redstone_block", true);

    public static final TriggerableRedstoneBlock TRIGGERABLE_REDSTONE_BLOCK = register(
        TriggerableRedstoneBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.FIRE)
            .requiresCorrectToolForDrops()
            .strength(5.0F, 6.0F)
            .sound(SoundType.METAL)
            .isRedstoneConductor(Blocks::never)
        , "triggerable_redstone_block", true);

    public static final TimerBlock TIMER_BLOCK = register(TimerBlock::new,
        BlockBehaviour.Properties.of()
            .sound(SoundType.COPPER_BULB)
        , "timer_block",
        true
    );

    private static <T extends Block> T register(Function<BlockBehaviour.Properties, T> blockFactory, BlockBehaviour.Properties properties, String path, boolean shouldRegisterItem) {
        var blockKey = ResourceKey.create(Registries.BLOCK, MapUtils.id(path));
        var block = blockFactory.apply(properties.setId(blockKey));

        if (shouldRegisterItem) {
            var itemKey = ResourceKey.create(Registries.ITEM, MapUtils.id(path));

            var blockItem = new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(itemKey));
            Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
        }

        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
    }

    public static void register() {
    }
}
