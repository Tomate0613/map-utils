package dev.doublekekse.map_utils.curve;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class SplinePath {
    private final List<SplineControlPoint> controlPoints;
    public static final StreamCodec<FriendlyByteBuf, SplinePath> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.collection(ArrayList::new, SplineControlPoint.STREAM_CODEC), SplinePath::controlPoints,
        SplinePath::new
    );

    public SplinePath(List<SplineControlPoint> controlPoints) {
        if (controlPoints == null || controlPoints.isEmpty()) {
            throw new IllegalArgumentException("At least one control point is required.");
        }
        this.controlPoints = controlPoints;
    }

    public Vec3 getPosition(double progress) {
        if(controlPoints.size() == 1) {
            return controlPoints.getFirst().position();
        }

        int n = controlPoints.size() - 1;
        int startIndex = (int) Math.floor(progress * n);
        startIndex = Math.max(0, Math.min(startIndex, n - 1));

        double u = progress * n - startIndex;

        Vec3 p0 = (startIndex > 0) ? controlPoints.get(startIndex - 1).position() : controlPoints.getFirst().position();
        Vec3 p1 = controlPoints.get(startIndex).position();
        Vec3 p2 = controlPoints.get(startIndex + 1).position();
        Vec3 p3 = (startIndex + 2 < controlPoints.size()) ? controlPoints.get(startIndex + 2).position() : controlPoints.get(n).position();

        return interpolateCatmullRom(p0, p1, p2, p3, u);
    }

    public Vec2 getRotation(double progress) {
        if(controlPoints.size() == 1) {
            return controlPoints.getFirst().rotation();
        }

        int numRotations = controlPoints.size();
        if (numRotations < 2) {
            throw new IllegalArgumentException("At least two rotations are needed for interpolation.");
        }

        int index0 = (int) Math.floor(progress * (numRotations - 1));
        int index1 = Math.min(index0 + 1, numRotations - 1);
        double t = (progress * (numRotations - 1)) - index0;

        return lerpRotation(controlPoints.get(index0).rotation(), controlPoints.get(index1).rotation(), (float) t);
    }

    public static Vec2 lerpRotation(Vec2 start, Vec2 end, float t) {
        return new Vec2(Mth.rotLerp(t, start.x, end.x), Mth.rotLerp(t, start.y, end.y));
    }

    private static Vec3 interpolateCatmullRom(Vec3 p0, Vec3 p1, Vec3 p2, Vec3 p3, double u) {
        double u2 = u * u;
        double u3 = u2 * u;

        double[] coefficients = {
            -0.5 * u3 + u2 - 0.5 * u,
            1.5 * u3 - 2.5 * u2 + 1,
            -1.5 * u3 + 2.0 * u2 + 0.5 * u,
            0.5 * u3 - 0.5 * u2
        };

        return new Vec3(
            coefficients[0] * p0.x + coefficients[1] * p1.x + coefficients[2] * p2.x + coefficients[3] * p3.x,
            coefficients[0] * p0.y + coefficients[1] * p1.y + coefficients[2] * p2.y + coefficients[3] * p3.y,
            coefficients[0] * p0.z + coefficients[1] * p1.z + coefficients[2] * p2.z + coefficients[3] * p3.z
        );
    }

    public List<SplineControlPoint> controlPoints() {
        return controlPoints;
    }

    public int size() {
        return controlPoints.size();
    }

    public ListTag write() {
        var tag = new ListTag();
        for (var controlPoint : controlPoints) {
            tag.add(controlPoint.write());
        }

        return tag;
    }

    public static SplinePath read(ListTag tag) {
        var controlPoints = new ArrayList<SplineControlPoint>();

        for (var controlPointTag : tag) {
            controlPoints.add(SplineControlPoint.read((CompoundTag) controlPointTag));
        }

        return new SplinePath(controlPoints);
    }
}
