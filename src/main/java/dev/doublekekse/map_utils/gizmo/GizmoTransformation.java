package dev.doublekekse.map_utils.gizmo;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record GizmoTransformation(
    Gizmo gizmo,
    @Nullable GizmoAxis axis,
    Vec3 startingPos,
    Vec3 startingInteractionPos,
    double startingDist
) {
    public static GizmoTransformation of(Gizmo gizmo, @Nullable GizmoAxis axis, Vec3 intersectionPos, Vec3 eyePosition) {
        return new GizmoTransformation(gizmo, axis, gizmo.position, intersectionPos.subtract(gizmo.position), intersectionPos.distanceTo(eyePosition));
    }

    public void apply(Player player) {
        var pos = player.getEyePosition();
        var target = pos.add(player.getLookAngle().scale(startingDist));
        var offset = target.subtract(gizmo.position).subtract(startingInteractionPos);

        if (axis != null) {
            offset = offset.multiply(axis.dir());
        }

        gizmo.move(offset);
    }
}
