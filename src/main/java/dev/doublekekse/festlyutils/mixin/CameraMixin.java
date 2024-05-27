package dev.doublekekse.festlyutils.mixin;

import dev.doublekekse.festlyutils.curve.SplineInterpolation;
import dev.doublekekse.festlyutils.curve.PositionAndRotation;
import dev.doublekekse.festlyutils.duck.AbstractClientPlayerDuck;
import dev.doublekekse.festlyutils.duck.CameraDuck;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
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
public abstract class CameraMixin implements CameraDuck {
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
    Vec3 customPosition;
    @Unique
    Vec3 oldCameraPosition;
    @Unique
    Vec2 customRotation;
    @Unique
    Vec2 oldCameraRotation;
    @Unique
    float progress = 0;
    @Unique
    PositionAndRotation[] splinePositionAndRotations;
    @Unique
    float lastTimeSinceLastTick;
    @Unique
    float timeSinceLastUpdate;

    @Unique
    float cameraSpeed = 1;

    @Inject(method = "setup", at = @At("TAIL"))
    void setup(BlockGetter blockGetter, Entity entity, boolean bl, boolean bl2, float timeSinceLastTick, CallbackInfo ci) {
        var deltaTime = timeSinceLastTick - lastTimeSinceLastTick;
        lastTimeSinceLastTick = timeSinceLastTick;
        timeSinceLastUpdate += deltaTime;

        // If the time since last update is larger than one tick, then we should stop interpolating
        if (timeSinceLastUpdate > 1) {
            timeSinceLastUpdate = 1;

            oldCameraPosition = customPosition;
            oldCameraRotation = customRotation;
        }

        progress += deltaTime / 1000 * cameraSpeed;

        if (progress > 1 && splinePositionAndRotations != null) {
            splinePositionAndRotations = null;
            customPosition = null;
            customRotation = null;
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.festlyUtils$setFov(-1);
            }
        }

        if (splinePositionAndRotations != null) {
            var positionAndRotation = SplineInterpolation.generateSmoothPath(splinePositionAndRotations, progress);

            customPosition = positionAndRotation.position();
            customRotation = positionAndRotation.rotation();
        }

        if (oldCameraPosition == null) {
            oldCameraPosition = position;
        }

        if (oldCameraRotation == null) {
            oldCameraRotation = new Vec2(getXRot(), getYRot());
        }

        if (customPosition != null) {
            detached = true;
            var distance = oldCameraPosition.distanceToSqr(customPosition);

            // If the distance is less than 5 blocks, then we interpolate
            if (distance < 5 && splinePositionAndRotations == null) {
                var newPosition = oldCameraPosition.lerp(customPosition, timeSinceLastUpdate);
                setPosition(newPosition);
            } else {
                setPosition(customPosition);
            }
        }

        if (customRotation != null) {
            this.setRotation(customRotation.x, customRotation.y);
            var dotProduct = oldCameraRotation.dot(customRotation);

            // If the angle is less than 90 degrees, then we interpolate
            if (dotProduct > 0 && splinePositionAndRotations == null) {
                var newRotation = SplineInterpolation.lerpRotation(oldCameraRotation, customRotation, timeSinceLastUpdate);
                setRotation(newRotation.x, newRotation.y);
            } else {
                setRotation(customRotation.x, customRotation.y);
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    void tick(CallbackInfo ci) {
        lastTimeSinceLastTick = 0;
    }

    @Override
    public void festlyUtils$setPosition(Vec3 position) {
        oldCameraPosition = customPosition;
        this.customPosition = position;
        timeSinceLastUpdate = 0;
    }

    @Override
    public void festlyUtils$setRotation(Vec2 rotation) {
        oldCameraRotation = customRotation;
        this.customRotation = rotation;
        timeSinceLastUpdate = 0;
    }

    @Override
    public void festlyUtils$setSpline(PositionAndRotation[] positionAndRotations, float cameraSpeed) {
        this.splinePositionAndRotations = positionAndRotations;
        this.progress = 0;
        this.cameraSpeed = cameraSpeed;
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.festlyUtils$setFov(1);
        }
    }
}
