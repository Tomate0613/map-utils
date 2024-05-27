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
import net.minecraft.world.phys.Vec2;

public record CameraRotationPacket(Vec2 rotation) implements FabricPacket {
    public static final PacketType<CameraRotationPacket> TYPE = PacketType.create(new ResourceLocation(FestlyUtils.MOD_ID, "camera_rotation_packet"), (buf -> new CameraRotationPacket(buf.readNullable((b) -> new Vec2(b.readFloat(), b.readFloat())))));

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNullable(rotation, (b, rot) -> {
            b.writeFloat(rot.x);
            b.writeFloat(rot.y);
        });
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public static void handle(CameraRotationPacket packet, LocalPlayer player, PacketSender sender) {
        var client = Minecraft.getInstance();
        var camera = client.gameRenderer.getMainCamera();

        camera.festlyUtils$setRotation(packet.rotation);
    }
}
