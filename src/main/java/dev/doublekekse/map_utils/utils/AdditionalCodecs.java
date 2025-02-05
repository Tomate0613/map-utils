package dev.doublekekse.map_utils.utils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class AdditionalCodecs {
    public static final StreamCodec<FriendlyByteBuf, Vec3> VEC_3 = new StreamCodec<>() {
        @Override
        public @NotNull Vec3 decode(FriendlyByteBuf byteBuf) {
            return byteBuf.readVec3();
        }

        @Override
        public void encode(FriendlyByteBuf byteBuf, Vec3 vec3) {
            byteBuf.writeVec3(vec3);
        }
    };


    public static final StreamCodec<FriendlyByteBuf, Vec2> VEC_2 = new StreamCodec<>() {
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
}
