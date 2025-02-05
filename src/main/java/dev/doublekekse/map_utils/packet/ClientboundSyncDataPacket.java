package dev.doublekekse.map_utils.packet;

import dev.doublekekse.map_utils.MapUtils;
import dev.doublekekse.map_utils.client.MapUtilsClient;
import dev.doublekekse.map_utils.data.MapUtilsSavedData;
import dev.doublekekse.map_utils.gizmo.Gizmos;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record ClientboundSyncDataPacket(MapUtilsSavedData savedData) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncDataPacket> STREAM_CODEC = CustomPacketPayload.codec(ClientboundSyncDataPacket::write, ClientboundSyncDataPacket::load);
    public static final CustomPacketPayload.Type<ClientboundSyncDataPacket> TYPE = new CustomPacketPayload.Type<>(MapUtils.identifier("clientbound_sync_data"));

    private static ClientboundSyncDataPacket load(FriendlyByteBuf buf) {
        var savedData = new MapUtilsSavedData();
        savedData.loadPaths(Objects.requireNonNull(buf.readNbt()));

        return new ClientboundSyncDataPacket(savedData);
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(savedData.savePaths());
    }

    public static void handle(ClientboundSyncDataPacket packet, ClientPlayNetworking.Context context) {
        MapUtilsClient.clientSavedData = packet.savedData;
        Gizmos.update();
    }
}
