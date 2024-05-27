package dev.doublekekse.festlyutils.packet;

import dev.doublekekse.festlyutils.FestlyUtils;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record CameraFovPacket(float fov) implements FabricPacket {
    public static final PacketType<CameraFovPacket> TYPE = PacketType.create(new ResourceLocation(FestlyUtils.MOD_ID, "camera_fov_packet"), (buf -> new CameraFovPacket(buf.readFloat())));

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeFloat(fov);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public static void handle(CameraFovPacket packet, LocalPlayer player, PacketSender sender) {
        player.festlyUtils$setFov(packet.fov);
    }
}
