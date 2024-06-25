package dev.doublekekse.festlyutils.client;

import dev.doublekekse.festlyutils.packet.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class FestlyUtilsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(CameraPositionPacket.TYPE, CameraPositionPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(CameraRotationPacket.TYPE, CameraRotationPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(CameraSplinePacket.TYPE, CameraSplinePacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(CameraOverlayPacket.TYPE, CameraOverlayPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(CameraFovPacket.TYPE, CameraFovPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(ClickEventPacket.TYPE, ClickEventPacket::handle);
    }
}
