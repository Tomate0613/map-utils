package dev.doublekekse.map_utils.packet;

import dev.doublekekse.map_utils.MapUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record CameraPositionPacket(Vec3 position, boolean interpolate) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, CameraPositionPacket> STREAM_CODEC = CustomPacketPayload.codec(CameraPositionPacket::write, CameraPositionPacket::new);
    public static final CustomPacketPayload.Type<CameraPositionPacket> TYPE = new CustomPacketPayload.Type<>(MapUtils.identifier("camera_position_packet"));

    CameraPositionPacket(FriendlyByteBuf buf) {
        this(buf.readNullable(FriendlyByteBuf::readVec3), buf.readBoolean());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeNullable(position, FriendlyByteBuf::writeVec3);
        buf.writeBoolean(interpolate);
    }
}
