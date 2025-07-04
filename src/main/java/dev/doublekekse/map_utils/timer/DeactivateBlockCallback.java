package dev.doublekekse.map_utils.timer;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.doublekekse.map_utils.registry.MapUtilsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerQueue;
import org.jetbrains.annotations.NotNull;

public record DeactivateBlockCallback(ResourceKey<Level> dimension, BlockPos position)
    implements TimerCallback<MinecraftServer> {
    public static final MapCodec<DeactivateBlockCallback> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(DeactivateBlockCallback::dimension),
            BlockPos.CODEC.fieldOf("position").forGetter(DeactivateBlockCallback::position)
        ).apply(instance, DeactivateBlockCallback::new)
    );

    @Override
    public void handle(MinecraftServer minecraftServer, TimerQueue<MinecraftServer> timerQueue, long l) {
        var level = minecraftServer.getLevel(dimension);
        if (level == null) {
            // TODO
            return;
        }
        if (level.getBlockState(position).getBlock() == MapUtilsBlocks.VARIABLE_REDSTONE_BLOCK) {
            level.setBlockAndUpdate(position, MapUtilsBlocks.VARIABLE_REDSTONE_BLOCK.defaultBlockState());
        }
    }

    @Override
    public @NotNull MapCodec<DeactivateBlockCallback> codec() {
        return CODEC;
    }
}
