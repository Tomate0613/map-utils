package dev.doublekekse.map_utils.client.path;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.doublekekse.map_utils.client.MapUtilsClient;
import dev.doublekekse.map_utils.curve.SplineControlPoint;
import dev.doublekekse.map_utils.curve.SplinePath;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import org.joml.Vector3f;

import java.util.OptionalDouble;

public class PathRenderer {
    public static void render(LevelRenderContext ctx) {
        if (MapUtilsClient.clientSavedData == null) {
            return;
        }

        for (var entry : MapUtilsClient.clientSavedData.paths.entrySet()) {
            renderPath(ctx, entry.getValue(), entry.getKey());
        }
    }

    public static void renderPath(LevelRenderContext ctx, SplinePath path, String id) {
        renderLine(path);

        for (var controlPoint : path.controlPoints()) {
            renderControlPointRotation(controlPoint);
        }

        int index = 0;
        for (var controlPoint : path.controlPoints()) {
            renderControlPointText(controlPoint, id + ": " + index++);
        }
    }

    static void renderLine(SplinePath path) {
        var count = 20.0 * path.size();
        for (int i = 0; i < count; i++) {
            var currentProgress = i / count;
            var nextProgress = (i + 1) / count;

            var currentPos = path.getPosition(currentProgress);
            var nextPos = path.getPosition(nextProgress);

            Gizmos.line(currentPos, nextPos, 0xffffffff);
        }
    }

    static void renderControlPointRotation(SplineControlPoint controlPoint) {
        var rotation = controlPoint.rotation();

        float yaw = (float) Math.toRadians(rotation.x + 90);
        float pitch = (float) Math.toRadians(-rotation.y);

        float dx = (float) (Math.cos(yaw) * Math.cos(pitch));
        float dy = (float) Math.sin(pitch);
        float dz = (float) (Math.sin(yaw) * Math.cos(pitch));


        Gizmos.arrow(controlPoint.position(), controlPoint.position().add(dx, dy, dz), 0xffffff00);
        //lineConsumer.addVertex(pose, controlPoint.position().toVector3f()).setColor(0xffffff00).setNormal(normal.x, normal.y, normal.z);
        //lineConsumer.addVertex(pose, controlPoint.position().add(dx, dy, dz).toVector3f()).setColor(0xffffff00).setNormal(normal.x, normal.y, normal.z);
    }

    static void renderControlPointText(SplineControlPoint controlPoint, String text) {
        Gizmos.billboardText(text, controlPoint.position().add(0, 0.2, 0), new TextGizmo.Style(0xfff000f0, .1f, OptionalDouble.empty()));
    }
}
