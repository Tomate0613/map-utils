package dev.doublekekse.map_utils.packet;

import dev.doublekekse.map_utils.MapUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record ClientboundCreditsPacket(boolean showPoem) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, ClientboundCreditsPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, ClientboundCreditsPacket::showPoem, ClientboundCreditsPacket::new);
    public static final CustomPacketPayload.Type<ClientboundCreditsPacket> TYPE = new CustomPacketPayload.Type<>(MapUtils.id("clientbound_credits"));

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Environment(EnvType.CLIENT)
    public void handle(ClientPlayNetworking.Context context) {
        context.client().setScreenAndShow(new WinScreen(showPoem, () -> {
            context.client().setScreenAndShow(null);
        }));
    }
}
