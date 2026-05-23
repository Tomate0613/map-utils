package dev.doublekekse.map_utils.block.timer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

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
    protected void saveAdditional(@NotNull ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);

        valueOutput.putInt("interval", interval);
        valueOutput.putInt("duration", duration);
        valueOutput.putLong("tick_data", tickData);
        valueOutput.putBoolean("tick_unloaded", tickUnloaded);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput valueInput) {
        super.loadAdditional(valueInput);

        interval = valueInput.getInt("interval").orElse(10);
        duration = valueInput.getInt("duration").orElse(1);
        tickData = valueInput.getLong("tick_data").orElse(0L);
        tickUnloaded = valueInput.getBooleanOr("tick_unloaded", false);
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
        if (level.isClientSide()) {
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
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NonNull Provider provider) {
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
