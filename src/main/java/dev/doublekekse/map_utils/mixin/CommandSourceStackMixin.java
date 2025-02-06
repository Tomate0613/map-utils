package dev.doublekekse.map_utils.mixin;

import dev.doublekekse.map_utils.duck.CommandSourceStackDuck;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CommandSourceStack.class)
public class CommandSourceStackMixin implements CommandSourceStackDuck {

    @Shadow
    @Final
    private int permissionLevel;

    @Override
    public int mapUtils$permissionLevel() {
        return permissionLevel;
    }
}
