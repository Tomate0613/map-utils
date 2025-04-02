package dev.doublekekse.map_utils.mixin;

import dev.doublekekse.map_utils.state.CameraOverrideState;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {
    @Inject(method = "getFieldOfViewModifier", at = @At("HEAD"), cancellable = true)
    void getFieldOfViewModifier(CallbackInfoReturnable<Float> cir) {
        if (CameraOverrideState.fov != -1) {
            cir.setReturnValue(CameraOverrideState.fov);
        }
    }
}
