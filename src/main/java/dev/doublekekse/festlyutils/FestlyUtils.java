package dev.doublekekse.festlyutils;

import dev.doublekekse.festlyutils.command.*;
import dev.doublekekse.festlyutils.command.argument.PathArgumentType;
import dev.doublekekse.festlyutils.block.VariableRedstoneBlock;
import dev.doublekekse.festlyutils.timer.CommandCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.timers.TimerCallbacks;


public class FestlyUtils implements ModInitializer {
    public static final String MOD_ID = "festly-utils";
    public static final VariableRedstoneBlock VARIABLE_REDSTONE_BLOCK = new VariableRedstoneBlock(FabricBlockSettings.create().mapColor(MapColor.FIRE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL).isRedstoneConductor(Blocks::never));
    public static final BlockItem VARIABLE_REDSTONE_BLOCK_ITEM = new BlockItem(VARIABLE_REDSTONE_BLOCK, new FabricItemSettings());

    @Override
    public void onInitialize() {
        TimerCallbacks.SERVER_CALLBACKS.register(new CommandCallback.Serializer());

        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(MOD_ID, "variable_redstone_block"), VARIABLE_REDSTONE_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "variable_redstone_block"), VARIABLE_REDSTONE_BLOCK_ITEM);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(content -> {
            content.accept(VARIABLE_REDSTONE_BLOCK_ITEM);
        });

        ArgumentTypeRegistry.registerArgumentType(new ResourceLocation(MOD_ID, "path"), PathArgumentType.class, SingletonArgumentInfo.contextFree(PathArgumentType::path));

        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                ScheduleCommandExtension.register(dispatcher);
                ClickEventCommand.register(dispatcher);
                InventoryCommand.register(dispatcher);
                CameraCommand.register(dispatcher);
                RedstoneCommand.register(dispatcher);
            }
        );
    }
}
