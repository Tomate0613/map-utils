package dev.doublekekse.map_utils.registry;

import dev.doublekekse.map_utils.packet.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class MapUtilsPackets {
    public static void register() {
        PayloadTypeRegistry.playS2C().register(CameraFovPacket.TYPE, CameraFovPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(CameraOverlayPacket.TYPE, CameraOverlayPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(CameraPositionPacket.TYPE, CameraPositionPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(CameraRotationPacket.TYPE, CameraRotationPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(CameraSplinePacket.TYPE, CameraSplinePacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ClickEventPacket.TYPE, ClickEventPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ClientboundSyncDataPacket.TYPE, ClientboundSyncDataPacket.STREAM_CODEC);

        PayloadTypeRegistry.playC2S().register(SetTimerBlockPacket.TYPE, SetTimerBlockPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SavePathPacket.TYPE, SavePathPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ServerboundModifyControlPointPacket.TYPE, ServerboundModifyControlPointPacket.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SetTimerBlockPacket.TYPE, SetTimerBlockPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(SavePathPacket.TYPE, SavePathPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundModifyControlPointPacket.TYPE, ServerboundModifyControlPointPacket::handle);
    }
}
