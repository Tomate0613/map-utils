package dev.doublekekse.map_utils.packet;

import dev.doublekekse.map_utils.MapUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;

public record CameraRotationPacket(Vec2 rotation, boolean interpolate) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, CameraRotationPacket> STREAM_CODEC = CustomPacketPayload.codec(CameraRotationPacket::write, CameraRotationPacket::new);
    public static final CustomPacketPayload.Type<CameraRotationPacket> TYPE = new CustomPacketPayload.Type<>(MapUtils.id("camera_rotation_packet"));

    CameraRotationPacket(FriendlyByteBuf buf) {
        this(buf.readNullable((b) -> new Vec2(b.readFloat(), b.readFloat())), buf.readBoolean());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeNullable(rotation, (b, rot) -> {
            b.writeFloat(rot.x);
            b.writeFloat(rot.y);
        });
        buf.writeBoolean(interpolate);
    }
}
