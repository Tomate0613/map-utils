package dev.doublekekse.map_utils.client;

import dev.doublekekse.map_utils.client.path.PathRenderer;
import dev.doublekekse.map_utils.command.ClientPathEditorCommand;
import dev.doublekekse.map_utils.data.MapUtilsSavedData;
import dev.doublekekse.map_utils.gizmo.Gizmos;
import dev.doublekekse.map_utils.packet.*;
import dev.doublekekse.map_utils.packet.handler.CameraHandlers;
import dev.doublekekse.map_utils.packet.handler.ClickEventHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;

public class MapUtilsClient implements ClientModInitializer {
    public static MapUtilsSavedData clientSavedData;
    public static boolean pathEditorEnabled;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(CameraPositionPacket.TYPE, CameraHandlers::handlePosition);
        ClientPlayNetworking.registerGlobalReceiver(CameraRotationPacket.TYPE, CameraHandlers::handleRotation);
        ClientPlayNetworking.registerGlobalReceiver(CameraSplinePacket.TYPE, CameraHandlers::handleSpline);
        ClientPlayNetworking.registerGlobalReceiver(CameraOverlayPacket.TYPE, CameraHandlers::handleOverlay);
        ClientPlayNetworking.registerGlobalReceiver(CameraFovPacket.TYPE, CameraHandlers::handleFov);
        ClientPlayNetworking.registerGlobalReceiver(ClickEventPacket.TYPE, ClickEventHandler::handle);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncDataPacket.TYPE, ClientboundSyncDataPacket::handle);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            ClientPathEditorCommand.register(dispatcher);
        });

        WorldRenderEvents.AFTER_ENTITIES.register((ctx) -> {
            var gameMode = Minecraft.getInstance().gameMode;

            if (!MapUtilsClient.pathEditorEnabled || gameMode == null || gameMode.getPlayerMode().isSurvival()) {
                return;
            }

            var poseStack = ctx.matrixStack();

            if (poseStack == null) {
                return;
            }

            poseStack.pushPose();

            var cPos = ctx.camera().getPosition();
            poseStack.translate(-cPos.x, -cPos.y, -cPos.z);

            PathRenderer.render(ctx);
            Gizmos.render(ctx);


            poseStack.popPose();
        });
    }
}
