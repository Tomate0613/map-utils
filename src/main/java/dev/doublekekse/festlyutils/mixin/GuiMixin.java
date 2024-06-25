package dev.doublekekse.festlyutils.mixin;

import dev.doublekekse.festlyutils.duck.GuiDuck;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin implements GuiDuck {
    @Shadow
    protected abstract void renderTextureOverlay(GuiGraphics guiGraphics, ResourceLocation resourceLocation, float f);

    @Shadow
    protected abstract void renderSpyglassOverlay(GuiGraphics guiGraphics, float f);

    @Unique
    ResourceLocation overlayLocation;
    @Unique
    float overlayOpacity;

    @Inject(method = "renderCameraOverlays", at = @At("HEAD"))
    void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (overlayLocation == null) {
            return;
        }

        if (overlayLocation.getPath().equals("textures/misc/spyglass_scope.png")) {
            renderSpyglassOverlay(guiGraphics, overlayOpacity);
            return;
        }

        renderTextureOverlay(guiGraphics, overlayLocation, overlayOpacity);
    }

    @Override
    public void festlyUtils$setOverlay(ResourceLocation overlayLocation, float opacity) {
        this.overlayLocation = overlayLocation;
        this.overlayOpacity = opacity;
    }
}
