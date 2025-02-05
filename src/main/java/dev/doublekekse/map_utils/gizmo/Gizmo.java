package dev.doublekekse.map_utils.gizmo;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

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
        var normal = direction.getNormal();
        var offset = new Vec3(normal.getX() * .1 * value, normal.getY() * .1 * value, normal.getZ() * .1 * value);
        move(offset);
    }

    public void move(Vec3 offset) {
        position = position.add(offset);
        onPositionChange.accept(position);
    }

    void drawLine(WorldRenderContext ctx, PoseStack.Pose pose, Vector3f offset, int color) {
        var normal = ctx.camera().getLookVector().mul(-1);
        var lineConsumer = ctx.consumers().getBuffer(RenderType.LINES);

        var fPos = position.toVector3f();

        lineConsumer.addVertex(pose, fPos).setColor(color).setNormal(pose, normal.x, normal.y, normal.z);
        lineConsumer.addVertex(pose, fPos.add(offset)).setColor(color).setNormal(pose, normal.x, normal.y, normal.z);
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

    public void render(WorldRenderContext ctx, boolean isActive) {
        var poseStack = ctx.matrixStack();
        var pose = poseStack.last();
        var aabb = getAABB();

        DebugRenderer.renderFilledBox(poseStack, ctx.consumers(), aabb, 1, isActive ? 0 : 1, 1, 1);

        if (!isActive) {
            return;
        }

        for (var axis : getAxes()) {
            drawLine(ctx, pose, axis.dir().toVector3f(), axis.color());
        }
    }
}
