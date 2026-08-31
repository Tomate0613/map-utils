package dev.doublekekse.map_utils.gizmo;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.core.Direction;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Gizmo {
    public Vec3 position;

    public Consumer<Vec3> onPositionChange;
    public Supplier<Vec3> getPosition;

    public static Gizmo bind(Supplier<Vec3> getPosition, Consumer<Vec3> onChange) {
        var gizmo = new Gizmo();

        gizmo.getPosition = getPosition;
        gizmo.onPositionChange = onChange;

        gizmo.position = getPosition.get();

        return gizmo;
    }

    public void scroll(Direction direction, double value) {
        var offset = direction.getUnitVec3().scale(.1 * value);
        move(offset);
    }

    public void move(Vec3 offset) {
        position = position.add(offset);
        onPositionChange.accept(position);
    }

    public AABB getAABB() {
        var radius = .1;
        return new AABB(position.x - radius, position.y - radius, position.z - radius, position.x + radius, position.y + radius, position.z + radius);
    }

    public Collection<GizmoAxis> getAxes() {
        var list = new ArrayList<GizmoAxis>(3);

        list.add(new GizmoAxis(this, new Vec3(1, 0, 0), 0xffff0000));
        list.add(new GizmoAxis(this, new Vec3(0, 1, 0), 0xff00ff00));
        list.add(new GizmoAxis(this, new Vec3(0, 0, 1), 0xff0000ff));

        return list;
    }

    public Optional<GizmoTransformation> transformation(Vec3 pos, Vec3 end) {
        return getAABB().clip(pos, end).map(intersection -> GizmoTransformation.of(this, null, intersection, pos));
    }

    public void render(LevelRenderContext ctx, boolean isActive) {
        var poseStack = ctx.poseStack();
        var pose = poseStack.last();
        var aabb = getAABB();

        Gizmos.cuboid(aabb, new GizmoStyle(0, 0, isActive ? 0xff00ffff : 0xffffffff));

        if (!isActive) {
            return;
        }

        for (var axis : getAxes()) {
            Gizmos.arrow(position, position.add(axis.dir()), axis.color());
        }
    }
}
