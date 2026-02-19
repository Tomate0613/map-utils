package dev.doublekekse.map_utils.mixin;

import dev.doublekekse.map_utils.curve.SplinePath;
import dev.doublekekse.map_utils.state.CameraOverrideState;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    private boolean detached;

    @Shadow
    protected abstract void setPosition(Vec3 vec3);

    @Shadow
    protected abstract void setRotation(float f, float g);

    @Shadow
    private Vec3 position;

    @Shadow
    public abstract float getCameraEntityPartialTicks(DeltaTracker deltaTracker);

    @Shadow
    private float yRot;
    @Shadow
    private float xRot;

    @Unique
    Vec3 oldCameraPosition;
    @Unique
    Vec2 oldCameraRotation;
    @Unique
    float lastTimeSinceLastTick;


    @Unique
    private static Vec3 overridePosition;
    @Unique
    private static Vec2 overrideRotation;

    @Inject(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Camera;hudFov:F", shift = At.Shift.AFTER, opcode = Opcodes.PUTFIELD))
    void setup(DeltaTracker deltaTracker, CallbackInfo ci) {
        float partialTicks = getCameraEntityPartialTicks(deltaTracker);
        lastTimeSinceLastTick = partialTicks;

        tickPath(partialTicks);
        tickPosition(partialTicks);
        tickRotation(partialTicks);
    }

    @Unique
    void tickRotation(float timeSinceLastTick) {
        if (overrideRotation == null) {
            return;
        }

        this.setRotation(overrideRotation.x, overrideRotation.y);

        if (CameraOverrideState.interpolateRotation) {
            var newRotation = SplinePath.lerpRotation(oldCameraRotation, overrideRotation, timeSinceLastTick);
            setRotation(newRotation.x, newRotation.y);
        } else {
            setRotation(overrideRotation.x, overrideRotation.y);
        }
    }

    @Unique
    void tickPosition(float timeSinceLastTick) {
        if (overridePosition == null) {
            return;
        }

        detached = true;

        if (CameraOverrideState.interpolatePosition) {
            var newPosition = oldCameraPosition.lerp(overridePosition, timeSinceLastTick);
            setPosition(newPosition);
        } else {
            setPosition(overridePosition);
        }
    }

    @Unique
    void tickPath(float timeSinceLastTick) {
        if (!isFollowingPath()) {
            return;
        }

        var progress = (CameraOverrideState.splineTicks + timeSinceLastTick) / CameraOverrideState.splineDuration;

        if (progress > 1) {
            stopPath();
            return;
        }

        overridePosition = CameraOverrideState.spline.getPosition(progress);
        overrideRotation = CameraOverrideState.spline.getRotation(progress);
    }

    @Unique
    void stopPath() {
        CameraOverrideState.spline = null;
        CameraOverrideState.splineDuration = 1;

        CameraOverrideState.fov = -1;
    }

    @Unique
    boolean isFollowingPath() {
        return CameraOverrideState.spline != null;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    void tick(CallbackInfo ci) {
        lastTimeSinceLastTick = 0;

        oldCameraPosition = overridePosition;
        oldCameraRotation = overrideRotation;

        overridePosition = CameraOverrideState.position;
        overrideRotation = CameraOverrideState.rotation;

        if (oldCameraPosition == null) {
            oldCameraPosition = position;
        }

        if (oldCameraRotation == null) {
            oldCameraRotation = new Vec2(yRot, xRot);
        }

        CameraOverrideState.splineTicks++;
    }
}
