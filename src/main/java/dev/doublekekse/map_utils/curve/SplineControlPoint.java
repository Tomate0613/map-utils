package dev.doublekekse.map_utils.curve;

import dev.doublekekse.map_utils.utils.AdditionalCodecs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public record SplineControlPoint(Vec3 position, Vec2 rotation) {
    public static final StreamCodec<FriendlyByteBuf, SplineControlPoint> STREAM_CODEC = StreamCodec.composite(
        AdditionalCodecs.VEC3_STREAM_CODEC, SplineControlPoint::position,
        AdditionalCodecs.VEC2_STREAM_CODEC, SplineControlPoint::rotation,
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
        var position = new Vec3(tag.getDouble("x").orElse(0.0), tag.getDouble("y").orElse(0.0), tag.getDouble("z").orElse(0.0));
        var rotation = new Vec2(tag.getFloat("rotation_x").orElse(0f), tag.getFloat("rotation_y").orElse(0f));

        return new SplineControlPoint(position, rotation);
    }
}
