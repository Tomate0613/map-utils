package dev.doublekekse.map_utils.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class VariableRedstoneBlock extends Block {
    public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 15);

    public VariableRedstoneBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWER, 0));
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState blockState, @NonNull Level level, @NonNull BlockPos blockPos, Player player, @NonNull BlockHitResult blockHitResult) {
        var signal = blockState.getValue(POWER);
        var newSignalStrength = signal + (player.isShiftKeyDown() ? -1 : 1);

        if (newSignalStrength < 0 || newSignalStrength > 15) {
            return InteractionResult.FAIL;
        }

        level.setBlockAndUpdate(blockPos, blockState.setValue(POWER, newSignalStrength));
        level.playSound(null, blockPos, !player.isShiftKeyDown() ? SoundEvents.COPPER_BULB_TURN_ON : SoundEvents.COPPER_BULB_TURN_OFF, SoundSource.BLOCKS);

        return InteractionResult.SUCCESS;
    }

    @Override
    public int getSignal(BlockState blockState, @NonNull BlockGetter blockGetter, @NonNull BlockPos blockPos, @NonNull Direction direction) {
        return blockState.getValue(POWER);
    }

    public boolean hasAnalogOutputSignal(@NonNull BlockState blockState) {
        return true;
    }

    public boolean isSignalSource(@NonNull BlockState blockState) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }
}
