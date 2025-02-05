package dev.doublekekse.map_utils.curve;

import dev.doublekekse.map_utils.utils.AdditionalCodecs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public record SplineControlPoint(Vec3 position, Vec2 rotation) {
    public static final StreamCodec<FriendlyByteBuf, SplineControlPoint> STREAM_CODEC = StreamCodec.composite(
        AdditionalCodecs.VEC_3, SplineControlPoint::position,
        AdditionalCodecs.VEC_2, SplineControlPoint::rotation,
        SplineControlPoint::new
    );

    public SplineControlPoint withPosition(Vec3 newPosition) {
        return new SplineControlPoint(newPosition, rotation);
    }

    public CompoundTag write() {
        var tag = new CompoundTag();

        tag.putDouble("x", position.x);
        tag.putDouble("y", position.y);
        tag.putDouble("z", position.z);

        tag.putFloat("rotation_x", rotation.x);
        tag.putFloat("rotation_y", rotation.y);

        return tag;
    }

    public static SplineControlPoint read(CompoundTag tag) {
        var position = new Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
        var rotation = new Vec2(tag.getFloat("rotation_x"), tag.getFloat("rotation_y"));

        return new SplineControlPoint(position, rotation);
    }
}
