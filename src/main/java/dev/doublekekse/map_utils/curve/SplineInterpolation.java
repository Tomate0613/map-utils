package dev.doublekekse.map_utils.curve;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class SplineInterpolation {
    public static Vec3 interpolatePosition(PositionAndRotation[] positionAndRotations, double t) {
        int n = positionAndRotations.length - 1;
        int startIndex = (int) Math.floor(t * n);
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (startIndex >= n) {
            startIndex = n - 1;
        }

        double u = t * n - startIndex;

        Vec3 p0 = startIndex > 0 ? positionAndRotations[startIndex - 1].position() : positionAndRotations[0].position();
        Vec3 p1 = positionAndRotations[startIndex].position();
        Vec3 p2 = positionAndRotations[startIndex + 1].position();
        Vec3 p3 = startIndex + 2 < positionAndRotations.length ? positionAndRotations[startIndex + 2].position() : positionAndRotations[n].position();

        return interpolateCatmullRom(p0, p1, p2, p3, u);
    }

    private static Vec3 interpolateCatmullRom(Vec3 p0, Vec3 p1, Vec3 p2, Vec3 p3, double u) {
        double u2 = u * u;
        double u3 = u * u * u;

        double[] coefficients = {
            -0.5 * u3 + u2 - 0.5 * u,
            1.5 * u3 - 2.5 * u2 + 1,
            -1.5 * u3 + 2 * u2 + 0.5 * u,
            0.5 * u3 - 0.5 * u2
        };

        double x = (coefficients[0] * p0.x + coefficients[1] * p1.x + coefficients[2] * p2.x + coefficients[3] * p3.x);
        double y = (coefficients[0] * p0.y + coefficients[1] * p1.y + coefficients[2] * p2.y + coefficients[3] * p3.y);
        double z = (coefficients[0] * p0.z + coefficients[1] * p1.z + coefficients[2] * p2.z + coefficients[3] * p3.z);

        return new Vec3(x, y, z);
    }

    public static Vec2 interpolateRotation(PositionAndRotation[] positionAndRotations, float progress) {
        int numRotations = positionAndRotations.length;
        if (numRotations < 2) {
            throw new IllegalArgumentException("At least two rotations are needed for interpolation.");
        }

        int index0 = (int) Math.floor(progress * (numRotations - 1));
        int index1 = Math.min(index0 + 1, numRotations - 1);

        float t = (progress * (numRotations - 1)) - index0;
        return lerpRotation(positionAndRotations[index0].rotation(), positionAndRotations[index1].rotation(), t);
    }

    public static Vec2 lerpRotation(Vec2 start, Vec2 end, float t) {
        return new Vec2(Mth.rotLerp(t, start.x, end.x), Mth.rotLerp(t, start.y, end.y));
    }


    public static PositionAndRotation generateSmoothPath(PositionAndRotation[] positionAndRotations, float progress) {
        // TODO Do not loop twice

        // Interpolate position
        Vec3 interpolatedPosition = interpolatePosition(positionAndRotations, progress);

        // Interpolate rotation
        Vec2 interpolatedRotation = interpolateRotation(positionAndRotations, progress);

        return new PositionAndRotation(interpolatedPosition, interpolatedRotation);
    }
}
