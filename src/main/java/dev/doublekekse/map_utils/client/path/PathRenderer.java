package dev.doublekekse.map_utils.client.path;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.doublekekse.map_utils.client.MapUtilsClient;
import dev.doublekekse.map_utils.curve.SplineControlPoint;
import dev.doublekekse.map_utils.curve.SplinePath;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import org.joml.Vector3f;

public class PathRenderer {
    public static void render(WorldRenderContext ctx) {
        if (MapUtilsClient.clientSavedData == null) {
            return;
        }

        for (var entry : MapUtilsClient.clientSavedData.paths.entrySet()) {
            renderPath(ctx, entry.getValue(), entry.getKey());
        }
    }

    public static void renderPath(WorldRenderContext ctx, SplinePath path, String id) {
        var lineConsumer = ctx.consumers().getBuffer(RenderType.LINES);
        var poseStack = ctx.matrixStack();
        var pose = poseStack.last();
        var normal = ctx.camera().getLookVector().mul(-1);

        renderLine(path, pose, lineConsumer, normal);

        for (var controlPoint : path.controlPoints()) {
            renderControlPointRotation(controlPoint, pose, lineConsumer, normal);
        }

        renderPathName(path, id, poseStack, ctx);
    }

    static void renderLine(SplinePath path, PoseStack.Pose pose, VertexConsumer lineConsumer, Vector3f normal) {
        var count = 20.0 * path.size();
        for (int i = 0; i < count; i++) {
            var currentProgress = i / count;
            var nextProgress = (i + 1) / count;

            var currentPos = path.getPosition(currentProgress);
            var nextPos = path.getPosition(nextProgress);

            lineConsumer.addVertex(pose, currentPos.toVector3f()).setColor(0xffffffff).setNormal(pose, normal.x, normal.y, normal.z);
            lineConsumer.addVertex(pose, nextPos.toVector3f()).setColor(0xffffffff).setNormal(pose, normal.x, normal.y, normal.z);
        }
    }

    static void renderControlPointRotation(SplineControlPoint controlPoint, PoseStack.Pose pose, VertexConsumer lineConsumer, Vector3f normal) {
        var rotation = controlPoint.rotation();

        float yaw = (float) Math.toRadians(rotation.x + 90);
        float pitch = (float) Math.toRadians(-rotation.y);

        float dx = (float) (Math.cos(yaw) * Math.cos(pitch));
        float dy = (float) Math.sin(pitch);
        float dz = (float) (Math.sin(yaw) * Math.cos(pitch));

        lineConsumer.addVertex(pose, controlPoint.position().toVector3f()).setColor(0xffffff00).setNormal(normal.x, normal.y, normal.z);
        lineConsumer.addVertex(pose, controlPoint.position().add(dx, dy, dz).toVector3f()).setColor(0xffffff00).setNormal(normal.x, normal.y, normal.z);
    }

    static void renderPathName(SplinePath path, String pathId, PoseStack poseStack, WorldRenderContext ctx) {
        var controlPoint = path.controlPoints().getFirst();

        poseStack.pushPose();
        poseStack.translate(controlPoint.position().x, controlPoint.position().y, controlPoint.position().z);
        poseStack.mulPose(ctx.camera().rotation().mul(-1));
        poseStack.scale(.01f, -.01f, .01f);

        var font = Minecraft.getInstance().font;
        var width = font.width(pathId);
        font.drawInBatch(pathId, -width / 2f, -30, 0xffffff, false, poseStack.last().pose(), ctx.consumers(), Font.DisplayMode.POLYGON_OFFSET, 0, 0xf000f0);

        poseStack.popPose();
    }
}
