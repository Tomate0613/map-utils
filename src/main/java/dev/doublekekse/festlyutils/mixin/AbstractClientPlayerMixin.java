package dev.doublekekse.festlyutils.mixin;

import dev.doublekekse.festlyutils.duck.AbstractClientPlayerDuck;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin implements AbstractClientPlayerDuck {
    @Unique
    float customFov = -1;

    @Inject(method = "getFieldOfViewModifier", at = @At("HEAD"), cancellable = true)
    void getFieldOfViewModifier(CallbackInfoReturnable<Float> cir) {
        if(customFov != -1) {
            cir.setReturnValue(customFov);
        }
    }

    @Override
    public void festlyUtils$setFov(float fov) {
        customFov = fov;
    }
}
