package dev.doublekekse.map_utils.mixin;

import dev.doublekekse.map_utils.state.CameraOverrideState;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow
    protected abstract void renderTextureOverlay(GuiGraphics guiGraphics, ResourceLocation resourceLocation, float f);

    @Shadow
    protected abstract void renderSpyglassOverlay(GuiGraphics guiGraphics, float f);

    @Inject(method = "renderCameraOverlays", at = @At("HEAD"))
    void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (CameraOverrideState.overlayLocation == null) {
            return;
        }

        if (CameraOverrideState.overlayLocation.getPath().equals("textures/misc/spyglass_scope.png")) {
            renderSpyglassOverlay(guiGraphics, CameraOverrideState.overlayOpacity);
            return;
        }

        renderTextureOverlay(guiGraphics, CameraOverrideState.overlayLocation, CameraOverrideState.overlayOpacity);
    }
}
