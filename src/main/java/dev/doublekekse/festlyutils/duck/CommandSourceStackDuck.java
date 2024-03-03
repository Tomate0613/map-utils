package dev.doublekekse.festlyutils.duck;

import net.minecraft.world.entity.Entity;

import java.util.Map;

public interface CommandSourceStackDuck {
    void festlyUtils$addSelector(String name, Entity entity);
    Entity festlyUtils$getSelector(String name);

    void festlyUtils$setSelectors(Map<String, Entity> map);
    int festlyUtils$permissionLevel();
}
