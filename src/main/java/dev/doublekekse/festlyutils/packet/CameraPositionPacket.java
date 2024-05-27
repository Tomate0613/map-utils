package dev.doublekekse.festlyutils.packet;

import dev.doublekekse.festlyutils.FestlyUtils;
import dev.doublekekse.festlyutils.duck.CameraDuck;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public record CameraPositionPacket(Vec3 position) implements FabricPacket {
    public static final PacketType<CameraPositionPacket> TYPE = PacketType.create(new ResourceLocation(FestlyUtils.MOD_ID, "camera_position_packet"), (buf -> new CameraPositionPacket(buf.readNullable(FriendlyByteBuf::readVec3))));

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNullable(position, FriendlyByteBuf::writeVec3);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public static void handle(CameraPositionPacket packet, LocalPlayer player, PacketSender sender) {
        var client = Minecraft.getInstance();
        var camera = client.gameRenderer.getMainCamera();

        camera.festlyUtils$setPosition(packet.position);
    }
}
