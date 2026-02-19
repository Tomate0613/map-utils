package dev.doublekekse.map_utils.packet;

import dev.doublekekse.map_utils.MapUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record CameraPositionPacket(Vec3 position, boolean interpolate) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, CameraPositionPacket> STREAM_CODEC = CustomPacketPayload.codec(CameraPositionPacket::write, CameraPositionPacket::new);
    public static final CustomPacketPayload.Type<CameraPositionPacket> TYPE = new CustomPacketPayload.Type<>(MapUtils.id("camera_position_packet"));

    CameraPositionPacket(FriendlyByteBuf buf) {
        this(buf.readNullable((a) -> new Vec3(a.readDouble(), a.readDouble(), a.readDouble())), buf.readBoolean());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeNullable(position, (a, b) -> {
            a.writeDouble(b.x);
            a.writeDouble(b.y);
            a.writeDouble(b.z);
        });

        buf.writeBoolean(interpolate);
    }
}
