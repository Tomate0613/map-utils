package dev.doublekekse.festlyutils.mixin;

import dev.doublekekse.festlyutils.duck.CommandSourceStackDuck;
import dev.doublekekse.festlyutils.duck.EntitySelectorDuck;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;

@Mixin(EntitySelector.class)
public class EntitySelectorMixin implements EntitySelectorDuck {
    @Unique
    private String userSelectorName = null;

    @Inject(method = "findEntitiesRaw", at = @At("HEAD"), cancellable = true)
    void findEntitiesRaw(CommandSourceStack commandSourceStack, CallbackInfoReturnable<List<? extends Entity>> cir) {
        if(userSelectorName != null) {
            var entity = ((CommandSourceStackDuck)commandSourceStack).festlyUtils$getSelector(userSelectorName);

            if(entity == null) {
                cir.setReturnValue(Collections.emptyList());
                return;
            }

            cir.setReturnValue(List.of(entity));
        }
    }

    @Override
    public void festlyUtils$userSelectorName(String name) {
        userSelectorName = name;
    }
}
