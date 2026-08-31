package dev.doublekekse.map_utils.mixin;

import dev.doublekekse.map_utils.state.CameraOverrideState;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public abstract class HudMixin {

    @Shadow
    protected abstract void extractSpyglassOverlay(GuiGraphicsExtractor graphics, float scale);

    @Shadow
    protected abstract void extractTextureOverlay(GuiGraphicsExtractor graphics, Identifier texture, float alpha);

    @Inject(method = "extractCameraOverlays", at = @At("HEAD"))
    void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (CameraOverrideState.overlayLocation == null) {
            return;
        }

        if (CameraOverrideState.overlayLocation.getPath().equals("textures/misc/spyglass_scope.png")) {
            extractSpyglassOverlay(graphics, CameraOverrideState.overlayOpacity);
            return;
        }

        extractTextureOverlay(graphics, CameraOverrideState.overlayLocation, CameraOverrideState.overlayOpacity);
    }
}
