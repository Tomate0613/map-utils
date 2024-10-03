package dev.doublekekse.map_utils.mixin;

import dev.doublekekse.map_utils.block.timer.TimerBlockEntity;
import dev.doublekekse.map_utils.client.screen.TimerEditScreen;
import dev.doublekekse.map_utils.duck.PlayerDuck;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin implements PlayerDuck {
    @Shadow @Final protected Minecraft minecraft;

    @Override
    public void mapUtils$openTimerBlock(TimerBlockEntity timerBlockEntity) {
        this.minecraft.setScreen(new TimerEditScreen(timerBlockEntity));
    }
}
