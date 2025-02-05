package dev.doublekekse.map_utils.packet.handler;

import dev.doublekekse.map_utils.client.MapUtilsClient;
import dev.doublekekse.map_utils.packet.*;
import dev.doublekekse.map_utils.state.CameraOverrideState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class CameraHandlers {
    public static void handleOverlay(CameraOverlayPacket packet, ClientPlayNetworking.Context context) {
        CameraOverrideState.overlayLocation = packet.overlayLocation();
        CameraOverrideState.overlayOpacity = packet.overlayOpacity();
    }

    public static void handlePosition(CameraPositionPacket packet, ClientPlayNetworking.Context context) {
        CameraOverrideState.position = packet.position();
        CameraOverrideState.interpolatePosition = packet.interpolate();
    }

    public static void handleRotation(CameraRotationPacket packet, ClientPlayNetworking.Context context) {
        CameraOverrideState.rotation = packet.rotation();
        CameraOverrideState.interpolateRotation = packet.interpolate();
    }

    public static void handleSpline(CameraSplinePacket packet, ClientPlayNetworking.Context context) {
        CameraOverrideState.spline = MapUtilsClient.clientSavedData.paths.get(packet.path());
        CameraOverrideState.splineDuration = packet.splineDuration();

        CameraOverrideState.fov = 1;
        CameraOverrideState.interpolatePosition = false;
        CameraOverrideState.interpolateRotation = false;

        CameraOverrideState.splineTicks = 0;
    }

    public static void handleFov(CameraFovPacket packet, ClientPlayNetworking.Context context) {
        CameraOverrideState.fov = packet.fov();
    }
}
