package dev.doublekekse.map_utils.mixin;

import dev.doublekekse.map_utils.gizmo.Gizmos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    void onScroll(long l, double d, double e, CallbackInfo ci) {
        double sensitivity = minecraft.options.mouseWheelSensitivity().get();
        boolean discreteMouseScroll = this.minecraft.options.discreteMouseScroll().get();
        double scroll = (discreteMouseScroll ? Math.signum(e) : e) * sensitivity;

        if(Gizmos.selectedGizmo != null) {
            ci.cancel();

            var direction = Minecraft.getInstance().player.getNearestViewDirection();
            Gizmos.selectedGizmo.scroll(direction, scroll);
        }
    }
}
