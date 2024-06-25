package dev.doublekekse.festlyutils.packet;

import dev.doublekekse.festlyutils.FestlyUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record CameraPositionPacket(Vec3 position) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, CameraPositionPacket> STREAM_CODEC = CustomPacketPayload.codec(CameraPositionPacket::write, CameraPositionPacket::new);
    public static final CustomPacketPayload.Type<CameraPositionPacket> TYPE = new CustomPacketPayload.Type<>(FestlyUtils.identifier("camera_position_packet"));

    CameraPositionPacket(FriendlyByteBuf buf) {
        this(buf.readNullable(FriendlyByteBuf::readVec3));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeNullable(position, FriendlyByteBuf::writeVec3);
    }

    public static void handle(CameraPositionPacket packet, ClientPlayNetworking.Context context) {
        var client = Minecraft.getInstance();
        var camera = client.gameRenderer.getMainCamera();

        camera.festlyUtils$setPosition(packet.position);
    }
}
