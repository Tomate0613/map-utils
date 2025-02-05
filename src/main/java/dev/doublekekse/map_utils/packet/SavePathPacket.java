package dev.doublekekse.map_utils.packet;

import dev.doublekekse.map_utils.MapUtils;
import dev.doublekekse.map_utils.curve.SplinePath;
import dev.doublekekse.map_utils.data.MapUtilsSavedData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SavePathPacket(String id, SplinePath path) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, SavePathPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, SavePathPacket::id,
        SplinePath.STREAM_CODEC, SavePathPacket::path,
        SavePathPacket::new
    );
    public static final CustomPacketPayload.Type<SavePathPacket> TYPE = new CustomPacketPayload.Type<>(MapUtils.identifier("save_path"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(ServerPlayNetworking.Context context) {
        var savedData = MapUtilsSavedData.getServerData(context.server());
        savedData.paths.put(id, path);

        context.player().sendSystemMessage(Component.literal("TEST"));
        MapUtils.syncData(context.responseSender(), context.server());
    }
}
