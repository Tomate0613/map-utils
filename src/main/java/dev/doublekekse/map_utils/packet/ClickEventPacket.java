package dev.doublekekse.map_utils.packet;

import dev.doublekekse.map_utils.MapUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ClickEventPacket(ClickEvent.Action action, String value) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, ClickEventPacket> STREAM_CODEC = CustomPacketPayload.codec(ClickEventPacket::write, ClickEventPacket::new);
    public static final CustomPacketPayload.Type<ClickEventPacket> TYPE = new CustomPacketPayload.Type<>(MapUtils.id("click_event_packet"));

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

}
