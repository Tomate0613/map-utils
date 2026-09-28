package dev.doublekekse.map_utils.packet;

import dev.doublekekse.map_utils.MapUtils;
import dev.doublekekse.map_utils.block.annotation.AnnotationBlockEntity;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerboundUpdateAnnotationPacket(BlockPos pos, String text) implements CustomPacketPayload {
    public static final StreamCodec<ByteBuf, ServerboundUpdateAnnotationPacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, ServerboundUpdateAnnotationPacket::pos,
        ByteBufCodecs.STRING_UTF8, ServerboundUpdateAnnotationPacket::text,
        ServerboundUpdateAnnotationPacket::new
    );
    public static final CustomPacketPayload.Type<ServerboundUpdateAnnotationPacket> TYPE = new CustomPacketPayload.Type<>(MapUtils.id("serverbound_update_annotation"));

    @Override
    public Type<ServerboundUpdateAnnotationPacket> type() {
        return TYPE;
    }

    public void handle(ServerPlayNetworking.Context context) {
        var player = context.player();
        var level = player.level();

        if (!player.canUseGameMasterBlocks()) {
            return;
        }

        player.resetLastActionTime();

        if (level.hasChunkAt(pos)) {
            if (!(level.getBlockEntity(pos) instanceof AnnotationBlockEntity annotation)) {
                return;
            }

            annotation.setText(text);
        }
    }
}
