package dev.doublekekse.festlyutils.mixin;

import dev.doublekekse.festlyutils.duck.CommandSourceStackDuck;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(CommandSourceStack.class)
public class CommandSourceStackMixin implements CommandSourceStackDuck {
    @Shadow @Final private int permissionLevel;
    @Unique
    public Map<String, Entity> selectors = new HashMap<>();

    @Override
    public void festlyUtils$addSelector(String name, Entity entry) {
        selectors.put(name, entry);
    }

    @Override
    public Entity festlyUtils$getSelector(String name) {
        return selectors.get(name);
    }

    @Override
    public void festlyUtils$setSelectors(Map<String, Entity> map) {
        selectors = map;
    }

    @Override
    public int festlyUtils$permissionLevel() {
        return permissionLevel;
    }


    @Inject(method = "/with.*/", at = @At("TAIL"))
    void copyState(CallbackInfoReturnable<CommandSourceStack> cir) {
        var newStack = cir.getReturnValue();
        newStack.festlyUtils$setSelectors(selectors);
    }
}
