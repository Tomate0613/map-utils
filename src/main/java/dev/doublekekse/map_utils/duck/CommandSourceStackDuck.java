package dev.doublekekse.map_utils.duck;

import net.minecraft.world.entity.Entity;

import java.util.Map;

public interface CommandSourceStackDuck {
    void mapUtils$addSelector(String name, Entity entity);
    Entity mapUtils$getSelector(String name);

    void mapUtils$setSelectors(Map<String, Entity> map);
    int mapUtils$permissionLevel();
}
