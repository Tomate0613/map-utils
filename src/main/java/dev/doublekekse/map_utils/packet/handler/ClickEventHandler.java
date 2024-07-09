package dev.doublekekse.map_utils.packet.handler;

import dev.doublekekse.map_utils.packet.ClickEventPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.net.URI;

@Environment(EnvType.CLIENT)
public class ClickEventHandler {
    public static void handle(ClickEventPacket packet, ClientPlayNetworking.Context context) {
        var minecraft = Minecraft.getInstance();
        switch (packet.action()) {
            case SUGGEST_COMMAND -> {
                minecraft.setScreen(new ConfirmScreen((bool) -> {
                    if (bool) {
                        minecraft.setScreen(new ChatScreen(packet.value()));
                    } else {
                        minecraft.setScreen(null);
                    }

                }, Component.translatable("gui.map_utils.suggest_command.confirm"), Component.literal(packet.value())));
            }
            case OPEN_URL -> {
                try {
                    var scheme = new URI(packet.value()).getScheme();
                    if (scheme.equals("https") || scheme.equals("http")) {
                        ConfirmLinkScreen.confirmLinkNow(minecraft.screen, packet.value());
                    }
                } catch (Exception e) {
                    return;
                }

            }
            case OPEN_FILE -> {
                var uri = (new File(packet.value())).toURI();
                ConfirmLinkScreen.confirmLinkNow(minecraft.screen, String.valueOf(uri));
            }
            case COPY_TO_CLIPBOARD -> {
                minecraft.setScreen(new ConfirmScreen((bool) -> {
                    if (bool) {
                        minecraft.keyboardHandler.setClipboard(packet.value());
                    }

                    minecraft.setScreen(null);
                }, Component.translatable("gui.map_utils.copy.confirm"), Component.literal(packet.value())));
            }
        }
    }
}
