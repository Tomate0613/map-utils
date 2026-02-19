package dev.doublekekse.map_utils.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AdditionalCodecs {
    public static final StreamCodec<FriendlyByteBuf, Vec3> VEC3_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull Vec3 decode(FriendlyByteBuf byteBuf) {
            return new Vec3(byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble());
        }

        @Override
        public void encode(FriendlyByteBuf byteBuf, Vec3 vec3) {
            byteBuf.writeDouble(vec3.x);
            byteBuf.writeDouble(vec3.y);
            byteBuf.writeDouble(vec3.z);
        }
    };


    public static final StreamCodec<FriendlyByteBuf, Vec2> VEC2_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull Vec2 decode(FriendlyByteBuf byteBuf) {
            return new Vec2(byteBuf.readFloat(), byteBuf.readFloat());
        }

        @Override
        public void encode(FriendlyByteBuf byteBuf, Vec2 vec2) {
            byteBuf.writeFloat(vec2.x);
            byteBuf.writeFloat(vec2.y);
        }
    };

    public static final Codec<UUID> UUID_CODEC = Codec.STRING.comapFlatMap(
        str -> {
            try {
                return DataResult.success(UUID.fromString(str));
            } catch (IllegalArgumentException e) {
                return DataResult.error(() -> "Invalid UUID format: " + e.getMessage());
            }
        },
        UUID::toString
    );
}
