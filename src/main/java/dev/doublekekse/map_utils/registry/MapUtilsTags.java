package dev.doublekekse.map_utils.registry;

import dev.doublekekse.map_utils.MapUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class MapUtilsTags {

    public static final TagKey<EntityType<?>> SAVABLE_ENTITIES = TagKey.create(Registries.ENTITY_TYPE, MapUtils.id("savable_entities"));
}
