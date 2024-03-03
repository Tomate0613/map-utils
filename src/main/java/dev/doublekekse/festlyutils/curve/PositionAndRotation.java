package dev.doublekekse.festlyutils.curve;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public record PositionAndRotation(Vec3 position, Vec2 rotation) {
    public void write(FriendlyByteBuf buf) {
        buf.writeVec3(position);
        buf.writeFloat(rotation.x);
        buf.writeFloat(rotation.y);
    }

    public static PositionAndRotation read(FriendlyByteBuf buf) {
        var position = buf.readVec3();
        var rotation = new Vec2(buf.readFloat(), buf.readFloat());

        return new PositionAndRotation(position, rotation);
    }
}
