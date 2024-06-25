package dev.doublekekse.map_utils.mixin;

import dev.doublekekse.map_utils.duck.CommandSourceStackDuck;
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
    @Shadow
    @Final
    private int permissionLevel;
    @Unique
    public Map<String, Entity> selectors = new HashMap<>();

    @Override
    public void mapUtils$addSelector(String name, Entity entry) {
        selectors.put(name, entry);
    }

    @Override
    public Entity mapUtils$getSelector(String name) {
        return selectors.get(name);
    }

    @Override
    public void mapUtils$setSelectors(Map<String, Entity> map) {
        selectors = map;
    }

    @Override
    public int mapUtils$permissionLevel() {
        return permissionLevel;
    }


    @Inject(method = {
        "withAnchor",
        "withCallback(Lnet/minecraft/commands/CommandResultCallback;)Lnet/minecraft/commands/ExecutionCommandSource;",
        "withCallback(Lnet/minecraft/commands/CommandResultCallback;)Lnet/minecraft/commands/CommandSourceStack;",
        "withCallback(Lnet/minecraft/commands/CommandResultCallback;Ljava/util/function/BinaryOperator;)Lnet/minecraft/commands/CommandSourceStack;",
        "withEntity",
        "withLevel",
        "withMaximumPermission",
        "withPermission",
        "withPosition",
        "withRotation",
        "withSigningContext",
        "withSource",
        "withSuppressedOutput"
    }, at = @At("TAIL"))
    void copyState(CallbackInfoReturnable<CommandSourceStack> cir) {
        var newStack = (CommandSourceStackDuck) cir.getReturnValue();
        newStack.mapUtils$setSelectors(selectors);
    }


    /*
    @Inject(method = "withAnchor", at = @At("TAIL"))
    @Inject(method = "withCallback(Lnet/minecraft/commands/CommandResultCallback;)Lnet/minecraft/commands/ExecutionCommandSource;", at = @At("TAIL"))
    @Inject(method = "withCallback(Lnet/minecraft/commands/CommandResultCallback;)Lnet/minecraft/commands/CommandSourceStack;", at = @At("TAIL"))
    @Inject(method = "withCallback(Lnet/minecraft/commands/CommandResultCallback;Ljava/util/function/BinaryOperator;)Lnet/minecraft/commands/CommandSourceStack;", at = @At("TAIL"))
    @Inject(method = "withEntity", at = @At("TAIL"))
    @Inject(method = "withLevel", at = @At("TAIL"))
    @Inject(method = "withMaximumPermission", at = @At("TAIL"))
    @Inject(method = "withPermission", at = @At("TAIL"))
    @Inject(method = "withPosition", at = @At("TAIL"))
    @Inject(method = "withRotation", at = @At("TAIL"))
    @Inject(method = "withSigningContext", at = @At("TAIL"))
    @Inject(method = "withSource", at = @At("TAIL"))
    @Inject(method = "withSuppressedOutput", at = @At("TAIL"))
     */

}
