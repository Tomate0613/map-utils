package dev.doublekekse.festlyutils.packet;

import dev.doublekekse.festlyutils.FestlyUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;

public record CameraRotationPacket(Vec2 rotation) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, CameraRotationPacket> STREAM_CODEC = CustomPacketPayload.codec(CameraRotationPacket::write, CameraRotationPacket::new);
    public static final CustomPacketPayload.Type<CameraRotationPacket> TYPE = new CustomPacketPayload.Type<>(FestlyUtils.identifier("camera_rotation_packet"));

    CameraRotationPacket(FriendlyByteBuf buf) {
        // TODO No cast wtf
        this((Vec2) buf.readNullable((b) -> new Vec2(b.readFloat(), b.readFloat())));
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
    }

    public static void handle(CameraRotationPacket packet, ClientPlayNetworking.Context context) {
        var client = Minecraft.getInstance();
        var camera = client.gameRenderer.getMainCamera();

        camera.festlyUtils$setRotation(packet.rotation);
    }
}
