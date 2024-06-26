package dev.doublekekse.map_utils.packet;

import dev.doublekekse.map_utils.MapUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URI;

public record ClickEventPacket(ClickEvent.Action action, String value) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, ClickEventPacket> STREAM_CODEC = CustomPacketPayload.codec(ClickEventPacket::write, ClickEventPacket::new);
    public static final CustomPacketPayload.Type<ClickEventPacket> TYPE = new CustomPacketPayload.Type<>(MapUtils.identifier("click_event_packet"));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private ClickEventPacket(FriendlyByteBuf buf) {
        this(
            buf.readEnum(ClickEvent.Action.class),
            buf.readUtf()
        );
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(action);
        buf.writeUtf(value);
    }

    public static void handle(ClickEventPacket packet, ClientPlayNetworking.Context context) {
        var minecraft = Minecraft.getInstance();
        switch (packet.action) {
            case SUGGEST_COMMAND -> {
                minecraft.setScreen(new ChatScreen(packet.value));
            }
            case OPEN_URL -> {
                try {
                    var scheme = new URI(packet.value).getScheme();
                    if (scheme.equals("https") || scheme.equals("http")) {
                        ConfirmLinkScreen.confirmLinkNow(minecraft.screen, packet.value);
                    }
                } catch (Exception e) {
                    return;
                }

            }
            case OPEN_FILE -> {
                var uri = (new File(packet.value)).toURI();
                ConfirmLinkScreen.confirmLinkNow(minecraft.screen, String.valueOf(uri));
            }
            case COPY_TO_CLIPBOARD -> {
                minecraft.setScreen(new ConfirmScreen((bool) -> {
                    if (bool) {
                        minecraft.keyboardHandler.setClipboard(packet.value);
                    }

                    minecraft.setScreen(null);
                }, Component.translatable("gui.map_utils.copy.confirm"), Component.literal(packet.value)));
            }
        }
    }
}
