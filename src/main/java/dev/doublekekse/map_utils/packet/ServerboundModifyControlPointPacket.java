package dev.doublekekse.map_utils.packet;

import dev.doublekekse.map_utils.MapUtils;
import dev.doublekekse.map_utils.curve.SplineControlPoint;
import dev.doublekekse.map_utils.data.MapUtilsSavedData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ServerboundModifyControlPointPacket(
    String pathId,
    int controlPointIndex,
    SplineControlPoint controlPoint
) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundModifyControlPointPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, ServerboundModifyControlPointPacket::pathId,
        ByteBufCodecs.INT, ServerboundModifyControlPointPacket::controlPointIndex,
        SplineControlPoint.STREAM_CODEC, ServerboundModifyControlPointPacket::controlPoint,
        ServerboundModifyControlPointPacket::new
    );
    public static final CustomPacketPayload.Type<ServerboundModifyControlPointPacket> TYPE = new CustomPacketPayload.Type<>(MapUtils.id("serverbound_modify_control_point_packet"));

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ServerboundModifyControlPointPacket packet, ServerPlayNetworking.Context context) {
        var savedData = MapUtilsSavedData.getServerData(context.server());

        savedData.paths.get(packet.pathId).controlPoints().set(packet.controlPointIndex, packet.controlPoint);
    }
}
