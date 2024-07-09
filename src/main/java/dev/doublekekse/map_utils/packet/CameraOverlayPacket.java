package dev.doublekekse.map_utils.packet;

import dev.doublekekse.map_utils.MapUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record CameraOverlayPacket(ResourceLocation overlayLocation,
                                  float overlayOpacity) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, CameraOverlayPacket> STREAM_CODEC = CustomPacketPayload.codec(CameraOverlayPacket::write, CameraOverlayPacket::new);
    public static final CustomPacketPayload.Type<CameraOverlayPacket> TYPE = new CustomPacketPayload.Type<>(MapUtils.identifier("camera_overlay_packet"));

    CameraOverlayPacket(FriendlyByteBuf buf) {
        this(buf.readNullable(FriendlyByteBuf::readResourceLocation), buf.readFloat());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeNullable(overlayLocation, FriendlyByteBuf::writeResourceLocation);
        buf.writeFloat(overlayOpacity);
    }
}
