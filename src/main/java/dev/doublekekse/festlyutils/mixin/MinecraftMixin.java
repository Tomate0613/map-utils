package dev.doublekekse.festlyutils.mixin;

import dev.doublekekse.festlyutils.duck.AbstractClientPlayerDuck;
import dev.doublekekse.festlyutils.duck.CameraDuck;
import dev.doublekekse.festlyutils.duck.GuiDuck;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
    public Gui gui;

    @Shadow
    @Final
    public GameRenderer gameRenderer;

    public MinecraftMixin(String string) {
        super(string);
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("HEAD"))
    void disconnect(CallbackInfo ci) {
        resetCameraData();
    }

    @Inject(method = "clearClientLevel", at = @At("HEAD"))
    void clearClientLevel(Screen screen, CallbackInfo ci) {
        resetCameraData();
    }

    @Unique
    void resetCameraData() {
        var camera = (CameraDuck) gameRenderer.getMainCamera();

        if(player != null) {
            ((AbstractClientPlayerDuck) player).festlyUtils$setFov(-1);
        }
        ((GuiDuck) gui).festlyUtils$setOverlay(null, 0);
        camera.festlyUtils$setPosition(null);
        camera.festlyUtils$setRotation(null);
        camera.festlyUtils$setSpline(null, 1);
    }
}
