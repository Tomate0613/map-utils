package dev.doublekekse.map_utils.block.annotation;

import dev.doublekekse.map_utils.client.screen.AnnotationEditScreen;
import dev.doublekekse.map_utils.registry.MapUtilsBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class AnnotationBlock extends BaseEntityBlock {
    public AnnotationBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new AnnotationBlockEntity(worldPosition, blockState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!player.canUseGameMasterBlocks() || !(level.getBlockEntity(pos) instanceof AnnotationBlockEntity annotation)) {
            return InteractionResult.FAIL;
        }

        if (level.isClientSide()) {
            Minecraft.getInstance().setScreenAndShow(new AnnotationEditScreen(annotation));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(final Level level, final BlockPos pos, final BlockState state, @Nullable final LivingEntity by, final ItemStack itemStack) {
        if (level.isClientSide() || !state.is(MapUtilsBlocks.ANNOTATION_BLOCK) || !(level.getBlockEntity(pos) instanceof AnnotationBlockEntity annotation)) {
            return;
        }

        Minecraft.getInstance().setScreenAndShow(new AnnotationEditScreen(annotation));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }
}
