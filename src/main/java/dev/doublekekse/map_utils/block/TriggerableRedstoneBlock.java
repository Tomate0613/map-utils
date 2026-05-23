package dev.doublekekse.map_utils.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;

public class TriggerableRedstoneBlock extends Block {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public TriggerableRedstoneBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        level.playSound(null, pos, SoundEvents.COPPER_BULB_TURN_ON, SoundSource.BLOCKS);
        turnOn(state, level, pos);

        return InteractionResult.SUCCESS;
    }

    public void turnOn(BlockState state, Level level, BlockPos pos) {
        level.setBlock(pos, state.setValue(POWERED, true), Block.UPDATE_CLIENTS);
        level.updateNeighborsAt(pos, state.getBlock());
        level.scheduleTick(pos, this, 1);
    }

    @Override
    protected void tick(@NonNull BlockState state, @NonNull ServerLevel level, @NonNull BlockPos pos, @NonNull RandomSource random) {
        super.tick(state, level, pos, random);

        level.setBlock(pos, state.setValue(POWERED, false), Block.UPDATE_CLIENTS);

        level.updateNeighborsAt(pos, state.getBlock());
    }

    @Override
    protected int getSignal(final BlockState state, final @NonNull BlockGetter level, final @NonNull BlockPos pos, final @NonNull Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }
}
