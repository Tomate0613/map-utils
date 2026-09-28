package dev.doublekekse.map_utils.block.annotation;

import com.mojang.serialization.Codec;
import dev.doublekekse.map_utils.registry.MapUtilsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class AnnotationBlockEntity extends BlockEntity {
    String text = "";

    public AnnotationBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MapUtilsBlockEntities.ANNOTATION_BLOCK_ENTITY, worldPosition, blockState);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("text", Codec.STRING, text);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        text = input.read("text", Codec.STRING).orElse("Annotation");
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return saveCustomOnly(registryLookup);
    }

    public void setText(String text) {
        assert level != null;

        this.text = text;

        setChanged();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public String getText() {
        return text;
    }
}
