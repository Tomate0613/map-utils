package dev.doublekekse.map_utils.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class EntityBarrier extends Block {
    public enum WhitelistMode implements StringRepresentable {
        PLAYER,
        ENTITY,
        NONE,
        ;

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase();
        }
    }
    public static final EnumProperty<WhitelistMode> WHITELIST_MODE = EnumProperty.create("whitelist_mode", WhitelistMode.class);

    public EntityBarrier(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected @NotNull VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        if(collisionContext instanceof EntityCollisionContext entityCollisionContext) {
            var mode = blockState.getValue(WHITELIST_MODE);
            var entity = entityCollisionContext.getEntity();

            if(entity == null) {
                return Shapes.empty();
            }

            switch (mode) {
                case NONE -> {
                    return Shapes.block();
                }
                case ENTITY -> {
                    return entity.getType().equals(EntityType.PLAYER) ? Shapes.block() : Shapes.empty();
                }
                case PLAYER -> {
                    return entity.getType().equals(EntityType.PLAYER) ? Shapes.empty() : Shapes.block();
                }
            }

            return Shapes.block();
        }else {
            return Shapes.empty();
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WHITELIST_MODE);
    }
}
