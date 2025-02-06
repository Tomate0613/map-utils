package dev.doublekekse.map_utils;

import dev.doublekekse.map_utils.data.MapUtilsSavedData;
import dev.doublekekse.map_utils.packet.*;
import dev.doublekekse.map_utils.registry.*;
import dev.doublekekse.map_utils.state.CameraOverrideState;
import dev.doublekekse.map_utils.timer.CommandCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.timers.TimerCallbacks;

public class MapUtils implements ModInitializer {
    public static final String MOD_ID = "map_utils";

    public static void invalidateData(MinecraftServer server) {
        var savedData = MapUtilsSavedData.getServerData(server);
        savedData.setDirty();

        for (var player : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(player, new ClientboundSyncDataPacket(savedData));
        }
    }

    public static void syncData(PacketSender packetSender, MinecraftServer server) {
        var savedData = MapUtilsSavedData.getServerData(server);
        packetSender.sendPacket(new ClientboundSyncDataPacket(savedData));
    }

    @Override
    public void onInitialize() {
        CameraOverrideState.reset();

        TimerCallbacks.SERVER_CALLBACKS.register(new CommandCallback.Serializer());

        MapUtilsBlocks.register();
        MapUtilsBlockEntities.register();
        MapUtilsCreativeTabs.register();
        MapUtilsPackets.register();
        MapUtilsCommands.register();

        ServerPlayConnectionEvents.JOIN.register((listener, packetSender, server) -> syncData(packetSender, server));
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
