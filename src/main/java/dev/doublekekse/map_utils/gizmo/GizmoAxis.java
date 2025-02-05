package dev.doublekekse.map_utils.gizmo;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public record GizmoAxis(Gizmo gizmo, Vec3 dir, int color) {
    public AABB getAABB() {
        return new AABB(gizmo.position, gizmo.position.add(dir)).inflate(.1);
    }

    public Optional<GizmoTransformation> transformation(Vec3 pos, Vec3 end) {
        return getAABB().clip(pos, end).map(intersection -> GizmoTransformation.of(gizmo, this, intersection, pos));
    }
}
