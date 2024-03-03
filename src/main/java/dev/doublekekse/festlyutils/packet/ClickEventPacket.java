package dev.doublekekse.festlyutils.packet;

import dev.doublekekse.festlyutils.FestlyUtils;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.net.URI;

public record ClickEventPacket(ClickEvent.Action action, String value) implements FabricPacket {
    public static final PacketType<ClickEventPacket> TYPE = PacketType.create(new ResourceLocation(FestlyUtils.MOD_ID, "click_event_packet"), (buf -> new ClickEventPacket(buf.readEnum(ClickEvent.Action.class), buf.readUtf())));

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(action);
        buf.writeUtf(value);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public static void handle(ClickEventPacket packet, LocalPlayer player, PacketSender sender) {
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
                }, Component.translatable("gui.festly-utils.copy.confirm"), Component.literal(packet.value)));
            }
        }
    }
}
