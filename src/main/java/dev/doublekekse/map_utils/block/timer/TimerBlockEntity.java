package dev.doublekekse.map_utils.block.timer;

import dev.doublekekse.map_utils.MapUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dev.doublekekse.map_utils.registry.MapUtilsBlockEntities.TIMER_BLOCK_ENTITY;

public class TimerBlockEntity extends BlockEntity {
    public int interval = 10;
    public int duration = 1;
    long tickData = 0;
    public boolean tickUnloaded = false;
    boolean enabled = false;

    public TimerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(TIMER_BLOCK_ENTITY, blockPos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);

        compoundTag.putInt("interval", interval);
        compoundTag.putInt("duration", duration);
        compoundTag.putLong("tick_data", tickData);
        compoundTag.putBoolean("tick_unloaded", tickUnloaded);
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);

        interval = compoundTag.getInt("interval");
        duration = compoundTag.getInt("duration");
        tickData = compoundTag.getLong("tick_data");
        tickUnloaded = compoundTag.getBoolean("tick_unloaded");
    }

    public void enable() {
        this.enabled = true;

        assert level != null;
        level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        setChanged();
    }

    public void disable() {
        this.enabled = false;

        assert level != null;
        level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        setChanged();
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, TimerBlockEntity instance) {
        if (level.isClientSide) {
            return;
        }


        if (instance.tickUnloaded) {
            var currentTick = level.getGameTime();

            if (instance.tickData + instance.interval <= currentTick) {
                instance.tickData = currentTick;
                instance.enable();
            }

            if (instance.tickData + instance.duration <= currentTick && instance.enabled) {
                instance.disable();
            }
        } else {
            instance.tickData++;

            if (instance.tickData >= instance.interval) {
                instance.tickData = 0;
                instance.enable();
            }

            if (instance.tickData == instance.duration) {
                instance.disable();
            }
        }
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();

        compoundTag.putInt("interval", interval);
        compoundTag.putInt("duration", duration);
        compoundTag.putBoolean("tick_unloaded", tickUnloaded);

        return compoundTag;
    }

    public int getOutputSignal() {
        return this.enabled ? 15 : 0;
    }
}
