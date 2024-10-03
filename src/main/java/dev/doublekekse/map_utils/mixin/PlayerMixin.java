package dev.doublekekse.map_utils.mixin;

import dev.doublekekse.map_utils.block.timer.TimerBlockEntity;
import dev.doublekekse.map_utils.duck.PlayerDuck;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public class PlayerMixin implements PlayerDuck {
    @Override
    public void mapUtils$openTimerBlock(TimerBlockEntity timerBlockEntity) {

    }
}
