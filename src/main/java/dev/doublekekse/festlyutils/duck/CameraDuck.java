package dev.doublekekse.festlyutils.duck;

import dev.doublekekse.festlyutils.curve.PositionAndRotation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public interface CameraDuck {
    void festlyUtils$setPosition(Vec3 position);
    void festlyUtils$setRotation(Vec2 position);


    void festlyUtils$setSpline(PositionAndRotation[] positionAndRotations, float cameraSpeed);
}
