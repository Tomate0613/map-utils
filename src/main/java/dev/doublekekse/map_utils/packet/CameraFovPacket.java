package dev.doublekekse.map_utils.packet;

import dev.doublekekse.map_utils.MapUtils;
import dev.doublekekse.map_utils.state.CameraOverrideState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record CameraFovPacket(float fov) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, CameraFovPacket> STREAM_CODEC = CustomPacketPayload.codec(CameraFovPacket::write, CameraFovPacket::new);
    public static final CustomPacketPayload.Type<CameraFovPacket> TYPE = new CustomPacketPayload.Type<>(MapUtils.identifier("camera_fov_packet"));

    CameraFovPacket(FriendlyByteBuf buf) {
        this(buf.readFloat());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeFloat(fov);
    }

    public static void handle(CameraFovPacket packet, ClientPlayNetworking.Context context) {
        CameraOverrideState.fov = packet.fov;
    }
}
