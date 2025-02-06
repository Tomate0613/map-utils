package dev.doublekekse.map_utils.packet;

import dev.doublekekse.map_utils.MapUtils;
import dev.doublekekse.map_utils.block.timer.TimerBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public record SetTimerBlockPacket(
    BlockPos blockPos,
    int duration,
    int interval,
    boolean tickUnloaded
) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, SetTimerBlockPacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, SetTimerBlockPacket::blockPos,
        ByteBufCodecs.VAR_INT, SetTimerBlockPacket::duration,
        ByteBufCodecs.VAR_INT, SetTimerBlockPacket::interval,
        ByteBufCodecs.BOOL, SetTimerBlockPacket::tickUnloaded,
        SetTimerBlockPacket::new
    );
    public static final CustomPacketPayload.Type<SetTimerBlockPacket> TYPE = new CustomPacketPayload.Type<>(MapUtils.id("set_timer_block_packet"));

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(ServerPlayNetworking.Context context) {
        var player = context.player();

        if (player.canUseGameMasterBlocks()) {
            BlockState blockState = player.level().getBlockState(blockPos);

            if (player.level().getBlockEntity(blockPos) instanceof TimerBlockEntity timerBlockEntity) {
                timerBlockEntity.duration = duration;
                timerBlockEntity.interval = interval;
                timerBlockEntity.tickUnloaded = tickUnloaded;

                timerBlockEntity.setChanged();
                player.level().sendBlockUpdated(blockPos, blockState, blockState, 3);
            }
        }
    }
}
