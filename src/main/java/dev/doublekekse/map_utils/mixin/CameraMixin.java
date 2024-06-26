package dev.doublekekse.map_utils.mixin;

import dev.doublekekse.map_utils.curve.SplineInterpolation;
import dev.doublekekse.map_utils.state.CameraOverrideState;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
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
    public abstract float getXRot();

    @Shadow
    public abstract float getYRot();

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

    @Inject(method = "setup", at = @At("TAIL"))
    void setup(BlockGetter blockGetter, Entity entity, boolean bl, boolean bl2, float timeSinceLastTick, CallbackInfo ci) {
        lastTimeSinceLastTick = timeSinceLastTick;

        tickPath(timeSinceLastTick);
        tickPosition(timeSinceLastTick);
        tickRotation(timeSinceLastTick);
    }

    @Unique
    void tickRotation(float timeSinceLastTick) {
        if (overrideRotation == null) {
            return;
        }

        this.setRotation(overrideRotation.x, overrideRotation.y);

        if (CameraOverrideState.interpolateRotation) {
            var newRotation = SplineInterpolation.lerpRotation(oldCameraRotation, overrideRotation, timeSinceLastTick);
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
        System.out.println((CameraOverrideState.splineTicks / (float)CameraOverrideState.splineDuration));

        if (progress > 1) {
            stopPath();
            System.out.println("STOP");
            return;
        }

        var positionAndRotation = SplineInterpolation.generateSmoothPath(CameraOverrideState.spline, progress);

        overridePosition = positionAndRotation.position();
        overrideRotation = positionAndRotation.rotation();
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
            oldCameraRotation = new Vec2(getXRot(), getYRot());
        }

        CameraOverrideState.splineTicks++;
    }
}
