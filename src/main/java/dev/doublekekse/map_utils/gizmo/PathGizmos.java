package dev.doublekekse.map_utils.gizmo;

import dev.doublekekse.map_utils.client.MapUtilsClient;
import dev.doublekekse.map_utils.curve.SplinePath;
import dev.doublekekse.map_utils.packet.ServerboundModifyControlPointPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PathGizmos {
    public static List<Gizmo> gizmos = new ArrayList<>();
    public static @Nullable Gizmo selectedGizmo = null;
    public static @Nullable GizmoTransformation transformation = null;

    public static void render(LevelRenderContext ctx) {
        for (var gizmo : gizmos) {
            gizmo.render(ctx, gizmo == selectedGizmo);
        }
    }

    public static void update() {
        gizmos.clear();
        selectedGizmo = null;
        transformation = null;

        for (var entry : MapUtilsClient.clientSavedData.paths.entrySet()) {
            forPath(entry.getKey(), entry.getValue());
        }
    }

    public static void forPath(String id, SplinePath path) {
        var controlPoints = path.controlPoints();
        for (int i = 0; i < controlPoints.size(); i++) {
            int finalI = i;
            var controlPoint = controlPoints.get(finalI);
            PathGizmos.gizmos.add(Gizmo.bind(controlPoint::position, (pos) -> {
                var newControlPoint = controlPoint.withPosition(pos);
                controlPoints.set(finalI, newControlPoint);
                ClientPlayNetworking.send(new ServerboundModifyControlPointPacket(id, finalI, newControlPoint));
            }));
        }
    }
}
