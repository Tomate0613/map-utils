package dev.doublekekse.map_utils;

import dev.doublekekse.map_utils.block.EntityBarrier;
import dev.doublekekse.map_utils.command.*;
import dev.doublekekse.map_utils.command.argument.PathArgumentType;
import dev.doublekekse.map_utils.block.VariableRedstoneBlock;
import dev.doublekekse.map_utils.packet.*;
import dev.doublekekse.map_utils.state.CameraOverrideState;
import dev.doublekekse.map_utils.timer.CommandCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.timers.TimerCallbacks;


public class MapUtils implements ModInitializer {
    public static final String MOD_ID = "map_utils";
    public static final VariableRedstoneBlock VARIABLE_REDSTONE_BLOCK = new VariableRedstoneBlock(BlockBehaviour.Properties.of().mapColor(MapColor.FIRE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL).isRedstoneConductor(Blocks::never));
    public static final BlockItem VARIABLE_REDSTONE_BLOCK_ITEM = new BlockItem(VARIABLE_REDSTONE_BLOCK, new Item.Properties());
    public static final EntityBarrier ENTITY_BARRIER_BLOCK = new EntityBarrier(BlockBehaviour.Properties.of().strength(-1.0F, 3600000.8F).mapColor(MapColor.NONE).noLootTable().noOcclusion().isValidSpawn(Blocks::never).noTerrainParticles().pushReaction(PushReaction.BLOCK));
    public static final BlockItem ENTITY_BARRIER_BLOCK_ITEM = new BlockItem(ENTITY_BARRIER_BLOCK, new Item.Properties());

    @Override
    public void onInitialize() {
        CameraOverrideState.reset();

        TimerCallbacks.SERVER_CALLBACKS.register(new CommandCallback.Serializer());

        Registry.register(BuiltInRegistries.BLOCK, identifier("variable_redstone_block"), VARIABLE_REDSTONE_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, identifier("variable_redstone_block"), VARIABLE_REDSTONE_BLOCK_ITEM);

        Registry.register(BuiltInRegistries.BLOCK, identifier("entity_barrier"), ENTITY_BARRIER_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, identifier("entity_barrier"), ENTITY_BARRIER_BLOCK_ITEM);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(content -> {
            content.accept(VARIABLE_REDSTONE_BLOCK_ITEM);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.OP_BLOCKS).register(content -> {
            content.accept(ENTITY_BARRIER_BLOCK_ITEM);
        });


        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                ScheduleCommandExtension.register(dispatcher);
                ClickEventCommand.register(dispatcher);
                InventoryCommand.register(dispatcher);
                CameraCommand.register(dispatcher);
                RedstoneCommand.register(dispatcher);
                PathCommand.register(dispatcher);
            }
        );

        PathCommand.registerTickListener();

        PayloadTypeRegistry.playS2C().register(CameraFovPacket.TYPE, CameraFovPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(CameraOverlayPacket.TYPE, CameraOverlayPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(CameraPositionPacket.TYPE, CameraPositionPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(CameraRotationPacket.TYPE, CameraRotationPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(CameraSplinePacket.TYPE, CameraSplinePacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ClickEventPacket.TYPE, ClickEventPacket.STREAM_CODEC);

        ArgumentTypeRegistry.registerArgumentType(identifier("path"), PathArgumentType.class, SingletonArgumentInfo.contextFree(PathArgumentType::path));
    }

    public static ResourceLocation identifier(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
