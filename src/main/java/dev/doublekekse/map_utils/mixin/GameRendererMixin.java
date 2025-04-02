package dev.doublekekse.map_utils.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.doublekekse.map_utils.state.CameraOverrideState;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @WrapOperation(method = "renderItemInHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;getPlayerMode()Lnet/minecraft/world/level/GameType;"))
    GameType shouldRenderHand(MultiPlayerGameMode instance, Operation<GameType> original) {
        if (CameraOverrideState.position != null) {
            return GameType.SPECTATOR;
        }

        return original.call(instance);
    }

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    void bobView(PoseStack poseStack, float f, CallbackInfo ci) {
        if (CameraOverrideState.position != null) {
            ci.cancel();
        }
    }

    @Inject(method = "bobHurt", at = @At("HEAD"), cancellable = true)
    void bobHurt(PoseStack poseStack, float f, CallbackInfo ci) {
        if (CameraOverrideState.position != null) {
            ci.cancel();
        }
    }
}
