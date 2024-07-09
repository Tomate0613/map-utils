package dev.doublekekse.map_utils.packet;

import dev.doublekekse.map_utils.MapUtils;
import dev.doublekekse.map_utils.curve.PositionAndRotation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record CameraSplinePacket(PositionAndRotation[] path, int splineDuration) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, CameraSplinePacket> STREAM_CODEC = CustomPacketPayload.codec(CameraSplinePacket::write, CameraSplinePacket::load);
    public static final CustomPacketPayload.Type<CameraSplinePacket> TYPE = new CustomPacketPayload.Type<>(MapUtils.identifier("camera_spline_packet"));

    static CameraSplinePacket load(FriendlyByteBuf buf) {
        var length = buf.readInt();

        if (length == -1) {
            return new CameraSplinePacket(null, 1);
        }

        var path = new PositionAndRotation[length];

        for (int i = 0; i < length; i++) {
            path[i] = PositionAndRotation.read(buf);
        }

        return new CameraSplinePacket(path, buf.readInt());
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

        buf.writeInt(splineDuration);
    }
}
