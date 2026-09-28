package dev.doublekekse.map_utils.client.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.doublekekse.map_utils.MapUtils;
import dev.doublekekse.map_utils.block.annotation.AnnotationBlockEntity;
import dev.doublekekse.map_utils.client.MapUtilsClient;
import dev.doublekekse.map_utils.registry.MapUtilsBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public class AnnotationRenderer implements BlockEntityRenderer<AnnotationBlockEntity, AnnotationRenderState> {
    private static final Identifier ITEM_TEXTURE = MapUtils.id("textures/item/annotation_block.png");

    Font font;

    public AnnotationRenderer(BlockEntityRendererProvider.Context context) {
        font = context.font();
    }

    @Override
    public AnnotationRenderState createRenderState() {
        return new AnnotationRenderState();
    }

    @Override
    public void extractRenderState(AnnotationBlockEntity blockEntity, AnnotationRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        var player = Minecraft.getInstance().player;

        state.text = blockEntity.getText();
        state.visible = player != null && player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER) && (player.gameMode() == GameType.CREATIVE || player.gameMode() == GameType.SPECTATOR);
        state.shouldRenderPlaceholder = player != null && player.isHolding(stack -> stack.is(MapUtilsBlocks.ANNOTATION_BLOCK.asItem()));
    }

    private static final Vector3f[] placeholderVertices = new Vector3f[]{
        new Vector3f(-0.5F, -0.5F, 0.0F),
        new Vector3f(0.5F, -0.5F, 0.0F),
        new Vector3f(0.5F, 0.5F, 0.0F),
        new Vector3f(-0.5F, 0.5F, 0.0F)
    };

    private static void placeholderVertex(
        PoseStack.Pose matrix, VertexConsumer vertexConsumer, Vector3f vertex, float u, float v, int color) {
        vertexConsumer.addVertex(matrix, vertex.x(), vertex.y(), vertex.z())
            .setColor(color)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(0xFF)
            .setNormal(0, 1, 0);
    }

    private static void renderPlaceholder(PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        poseStack.pushPose();
        poseStack.translate(0, 0, .0001f);

        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityCutoutCull(AnnotationRenderer.ITEM_TEXTURE), (pose, vertexConsumer) -> {
            int color = 0xA0A0A0;

            placeholderVertex(pose, vertexConsumer, placeholderVertices[0], 0, 1, color);
            placeholderVertex(pose, vertexConsumer, placeholderVertices[1], 1, 1, color);
            placeholderVertex(pose, vertexConsumer, placeholderVertices[2], 1, 0, color);
            placeholderVertex(pose, vertexConsumer, placeholderVertices[3], 0, 0, color);
        });

        poseStack.popPose();
    }

    @Override
    public void submit(AnnotationRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (!state.visible || MapUtilsClient.annotationVisibility == MapUtilsClient.AnnotationVisibility.HIDDEN) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.rotate(camera.orientation);

        if (state.shouldRenderPlaceholder) {
            renderPlaceholder(poseStack, submitNodeCollector);
        }

        var lines = state.text.split("\n");

        for (int i = 0; i < lines.length; i++) {
            var line = lines[i];
            var width = this.font.width(line);

            poseStack.pushPose();
            poseStack.translate(0, -.5 * (i - (.5 * (lines.length - 1))), 0);
            poseStack.scale(1 / 18f, -1 / 18f, 1 / 18f);

            submitNodeCollector.submitText(
                poseStack,
                -width / 2f, -4f,
                Component.literal(line).getVisualOrderText(),
                false,
                MapUtilsClient.annotationVisibility == MapUtilsClient.AnnotationVisibility.ALWAYS_ON_TOP ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL,
                LightCoordsUtil.FULL_BRIGHT,
                0xffe30000,
                0,
                0
            );

            poseStack.popPose();
        }

        poseStack.popPose();
    }
}
