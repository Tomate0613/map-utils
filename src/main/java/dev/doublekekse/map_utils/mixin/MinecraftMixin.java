package dev.doublekekse.map_utils.mixin;

import dev.doublekekse.map_utils.state.CameraOverrideState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
    extends ReentrantBlockableEventLoop<Runnable> {
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
}
