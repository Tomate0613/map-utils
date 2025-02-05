package dev.doublekekse.map_utils.mixin;

import dev.doublekekse.map_utils.client.MapUtilsClient;
import dev.doublekekse.map_utils.gizmo.Gizmo;
import dev.doublekekse.map_utils.gizmo.Gizmos;
import dev.doublekekse.map_utils.state.CameraOverrideState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
    extends ReentrantBlockableEventLoop<Runnable> {
    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    @Final
    public Options options;

    @Shadow
    private int rightClickDelay;

    public MinecraftMixin(String string) {
        super(string);
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("HEAD"))
    void disconnect(CallbackInfo ci) {
        CameraOverrideState.reset();
    }

    @Inject(method = "clearClientLevel", at = @At("HEAD"))
    void clearClientLevel(Screen screen, CallbackInfo ci) {
        CameraOverrideState.reset();
    }

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    void handleKeybinds(CallbackInfo ci) {
        if (!options.keyUse.isDown()) {
            Gizmos.transformation = null;
        }
    }

    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    void startUseItem(CallbackInfo ci) {
        assert player != null;

        if(!MapUtilsClient.pathEditorEnabled) {
            return;
        }

        var pos = player.getEyePosition();
        var end = pos.add(player.getLookAngle().scale(3));

        if (Gizmos.transformation != null) {
            Gizmos.transformation.apply(player);
            ci.cancel();
            return;
        }

        if (Gizmos.selectedGizmo != null) {
            var gizmo = Gizmos.selectedGizmo;
            var gizmoTransformation = gizmo.transformation(pos, end);

            if (gizmoTransformation.isPresent()) {
                Gizmos.transformation = gizmoTransformation.get();
                ci.cancel();
                return;
            }

            var axes = gizmo.getAxes();

            for (var axis : axes) {
                var axisTransformation = axis.transformation(pos, end);
                if (axisTransformation.isPresent()) {
                    Gizmos.transformation = axisTransformation.get();
                    ci.cancel();
                    return;
                }
            }
        }

        Vec3 closest = null;
        Gizmo closestGizmo = null;
        Gizmos.selectedGizmo = null;
        Gizmos.transformation = null;

        for (var gizmo : Gizmos.gizmos) {
            var intersection = gizmo.getAABB().clip(pos, end);
            if (intersection.isPresent()) {
                if (closest == null || intersection.get().lengthSqr() < closest.lengthSqr()) {
                    closest = intersection.get();
                    closestGizmo = gizmo;
                }
            }
        }

        if (closestGizmo == null) {
            return;
        }

        Gizmos.selectedGizmo = closestGizmo;
        rightClickDelay = 3;
        ci.cancel();

    }
}
