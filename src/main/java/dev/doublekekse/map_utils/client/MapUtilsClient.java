package dev.doublekekse.map_utils.client;

import dev.doublekekse.map_utils.packet.*;
import dev.doublekekse.map_utils.packet.handler.CameraHandlers;
import dev.doublekekse.map_utils.packet.handler.ClickEventHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class MapUtilsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(CameraPositionPacket.TYPE, CameraHandlers::handlePosition);
        ClientPlayNetworking.registerGlobalReceiver(CameraRotationPacket.TYPE, CameraHandlers::handleRotation);
        ClientPlayNetworking.registerGlobalReceiver(CameraSplinePacket.TYPE, CameraHandlers::handleSpline);
        ClientPlayNetworking.registerGlobalReceiver(CameraOverlayPacket.TYPE, CameraHandlers::handleOverlay);
        ClientPlayNetworking.registerGlobalReceiver(CameraFovPacket.TYPE, CameraHandlers::handleFov);
        ClientPlayNetworking.registerGlobalReceiver(ClickEventPacket.TYPE, ClickEventHandler::handle);
    }
}
