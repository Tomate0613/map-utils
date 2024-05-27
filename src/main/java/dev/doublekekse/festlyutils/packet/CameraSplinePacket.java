package dev.doublekekse.festlyutils.packet;

import dev.doublekekse.festlyutils.FestlyUtils;
import dev.doublekekse.festlyutils.curve.PositionAndRotation;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record CameraSplinePacket(PositionAndRotation[] path, float cameraSpeed) implements FabricPacket {
    public static final PacketType<CameraSplinePacket> TYPE = PacketType.create(new ResourceLocation(FestlyUtils.MOD_ID, "camera_spline_packet"), (buf -> {
        var length = buf.readInt();

        if(length == -1) {
            return new CameraSplinePacket(null, 1);
        }

        var path = new PositionAndRotation[length];

        for (int i = 0; i < length; i++) {
            path[i] = PositionAndRotation.read(buf);
        }

        return new CameraSplinePacket(path, buf.readFloat());
    }));

    @Override
    public void write(FriendlyByteBuf buf) {
        if(path == null) {
            buf.writeInt(-1);
            return;
        }

        buf.writeInt(path.length);
        for (PositionAndRotation positionAndRotation : path) {
            positionAndRotation.write(buf);
        }

        buf.writeFloat(cameraSpeed);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public static void handle(CameraSplinePacket packet, LocalPlayer player, PacketSender sender) {
        var client = Minecraft.getInstance();
        var camera = client.gameRenderer.getMainCamera();

        camera.festlyUtils$setPosition(null);
        camera.festlyUtils$setRotation(null);
        camera.festlyUtils$setSpline(packet.path, packet.cameraSpeed);
    }
}
