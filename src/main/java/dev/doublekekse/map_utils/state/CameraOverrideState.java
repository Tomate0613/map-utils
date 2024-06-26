package dev.doublekekse.map_utils.state;

import dev.doublekekse.map_utils.curve.PositionAndRotation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class CameraOverrideState {
    public static float fov;

    public static Vec3 position;
    public static Vec2 rotation;

    public static boolean interpolatePosition;
    public static boolean interpolateRotation;

    public static PositionAndRotation[] spline;
    public static int splineDuration;
    public static int splineTicks;

    public static ResourceLocation overlayLocation;
    public static float overlayOpacity;

    public static void reset() {
        fov = -1;

        position = null;
        rotation = null;

        interpolatePosition = false;
        interpolateRotation = false;

        spline = null;
        splineDuration = 1;
        splineTicks = 0;

        overlayLocation = null;
        overlayOpacity = 0;
    }
}
