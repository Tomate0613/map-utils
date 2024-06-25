package dev.doublekekse.festlyutils.packet;

import dev.doublekekse.festlyutils.FestlyUtils;
import dev.doublekekse.festlyutils.curve.PositionAndRotation;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record CameraSplinePacket(PositionAndRotation[] path, float cameraSpeed) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, CameraSplinePacket> STREAM_CODEC = CustomPacketPayload.codec(CameraSplinePacket::write, CameraSplinePacket::load);
    public static final CustomPacketPayload.Type<CameraSplinePacket> TYPE = new CustomPacketPayload.Type<>(FestlyUtils.identifier("camera_spline_packet"));

    static CameraSplinePacket load(FriendlyByteBuf buf) {
        var length = buf.readInt();

        if (length == -1) {
            return new CameraSplinePacket(null, 1);
        }

        var path = new PositionAndRotation[length];

        for (int i = 0; i < length; i++) {
            path[i] = PositionAndRotation.read(buf);
        }

        return new CameraSplinePacket(path, buf.readFloat());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void write(FriendlyByteBuf buf) {
        if (path == null) {
            buf.writeInt(-1);
            return;
        }

        buf.writeInt(path.length);
        for (PositionAndRotation positionAndRotation : path) {
            positionAndRotation.write(buf);
        }

        buf.writeFloat(cameraSpeed);
    }

    public static void handle(CameraSplinePacket packet, ClientPlayNetworking.Context context) {
        var client = Minecraft.getInstance();
        var camera = client.gameRenderer.getMainCamera();

        camera.festlyUtils$setPosition(null);
        camera.festlyUtils$setRotation(null);
        camera.festlyUtils$setSpline(packet.path, packet.cameraSpeed);
    }
}
