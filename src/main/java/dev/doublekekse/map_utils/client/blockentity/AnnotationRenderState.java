package dev.doublekekse.map_utils.client.blockentity;


import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class AnnotationRenderState extends BlockEntityRenderState {
    public String text;
    public boolean visible;
    public boolean shouldRenderPlaceholder;
}
