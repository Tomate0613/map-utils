package dev.doublekekse.festlyutils.packet;

import dev.doublekekse.festlyutils.FestlyUtils;
import dev.doublekekse.festlyutils.duck.GuiDuck;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record CameraOverlayPacket(ResourceLocation overlayLocation, float overlayOpacity) implements FabricPacket {
    public static final PacketType<CameraOverlayPacket> TYPE = PacketType.create(new ResourceLocation(FestlyUtils.MOD_ID, "camera_overlay_packet"), (buf -> new CameraOverlayPacket(buf.readNullable(FriendlyByteBuf::readResourceLocation), buf.readFloat())));

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNullable(overlayLocation, FriendlyByteBuf::writeResourceLocation);
        buf.writeFloat(overlayOpacity);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public static void handle(CameraOverlayPacket packet, LocalPlayer player, PacketSender sender) {
        var client = Minecraft.getInstance();
        var gui = ((GuiDuck) client.gui);

        gui.festlyUtils$setOverlay(packet.overlayLocation, packet.overlayOpacity);
    }
}
