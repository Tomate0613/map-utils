package dev.doublekekse.map_utils.data;

import dev.doublekekse.map_utils.MapUtils;
import dev.doublekekse.map_utils.curve.SplinePath;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.*;

// TODO: Abstract mod compat into single class modules. Sorta like what Switchy's got going on.
public class MapUtilsSavedData extends SavedData {
    public CompoundTag inventories = new CompoundTag();
    public Map<String, List<CompoundTag>> pets = new HashMap<>();
    public Map<String, SplinePath> paths = new HashMap<>();

    public void setPets(String id, List<CompoundTag> pets) {
        this.pets.put(id, pets);
    }

    public List<CompoundTag> getPets(String id) {
        return this.pets.get(id);
    }

    public void saveInventories(Player player, String id, boolean remove) {
        CompoundTag inventory = new CompoundTag();
        inventory.put("minecraft:inventory", player.getInventory().save(new ListTag()));

        if (FabricLoader.getInstance().isModLoaded("accessories")) {
            Optional<AccessoriesCapability> capability = AccessoriesCapability.getOptionally(player);
            if (capability.isPresent() && capability.get().isEquipped(stack -> !stack.isEmpty())) {
                CompoundTag accessoriesTag = new CompoundTag();
                for (var container : capability.get().getContainers().values()) {
                    CompoundTag containerTag = new CompoundTag();
                    ListTag accessories = container.getAccessories().createTag(player.level().registryAccess());
                    ListTag cosmeticAccessories = container.getCosmeticAccessories().createTag(player.level().registryAccess());

                    if (accessories.isEmpty() && cosmeticAccessories.isEmpty())
                        continue;

                    if (accessories.isEmpty()) {
                        containerTag.put("accessories", accessories);
                    }
                    if (cosmeticAccessories.isEmpty()) {
                        containerTag.put("cosmetic_accessories", cosmeticAccessories);
                    }

                    accessoriesTag.put(container.getSlotName(), containerTag);
                }

                inventory.put("accessories:accessories", accessoriesTag);

                if (remove) {
                    capability.get().reset(false);
                }
            }
        } else if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
            if (component.isPresent() && component.get().isEquipped(stack -> !stack.isEmpty())) {
                CompoundTag trinketsTag = new CompoundTag();
                component.get().writeToNbt(trinketsTag, player.level().registryAccess());

                inventory.put("trinkets:trinkets", trinketsTag);

                if (remove) {
                    component.get().getAllEquipped().forEach(tuple -> {
                        tuple.getA().inventory().clearContent();
                        tuple.getA().inventory().update();
                    });
                }
            }
        }

        if (remove) {
            player.getInventory().clearContent();
        }

        inventories.put(id, inventory);
        setDirty();
    }

    public boolean loadInventories(Player player, String id, boolean remove) {
        if (inventories.get(id) == null) {
            return false;
        }

        if (CompoundTag.TYPE.equals(Objects.requireNonNull(inventories.get(id)).getType())) {
            var inventory = inventories.getCompound(id);
            if (inventory.contains("minecraft:inventory", Tag.TAG_LIST)) {
                player.getInventory().load(inventory.getList("minecraft:inventory", Tag.TAG_COMPOUND));
            }

            if (FabricLoader.getInstance().isModLoaded("accessories") && inventory.contains("accessories:accessories")) {
                Optional<AccessoriesCapability> optionalCap = AccessoriesCapability.getOptionally(player);
                if (optionalCap.isPresent()) {
                    AccessoriesCapability capability = optionalCap.get();
                    for (var container : capability.getContainers().values()) {
                        CompoundTag containerTag = inventory.getCompound("accessories:accessories").getCompound(container.getSlotName());
                        if (!containerTag.isEmpty()) {
                            ListTag accessoriesTag = containerTag.getList("accessories", Tag.TAG_LIST);
                            ListTag cosmeticAccessoriesTag = containerTag.getList("cosmetic_accessories", Tag.TAG_LIST);
                            container.getAccessories().fromTag(accessoriesTag, player.level().registryAccess());
                            container.getCosmeticAccessories().fromTag(cosmeticAccessoriesTag, player.level().registryAccess());
                        } else {
                            container.getAccessories().clearContent();
                            container.getCosmeticAccessories().clearContent();
                        }
                        container.update();
                    }
                }
            }

            if (FabricLoader.getInstance().isModLoaded("trinkets") && inventory.contains("trinkets:trinkets")) {
                Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
                component.ifPresent(trinketComponent -> trinketComponent.readFromNbt(inventory.getCompound("trinkets:trinkets"), player.level().registryAccess()));
            }
        } else if (ListTag.TYPE.equals(Objects.requireNonNull(inventories.get(id)).getType())) {
            var inventory = inventories.getList(id, CompoundTag.TAG_COMPOUND);
            player.getInventory().load(inventory);
        }

        if (remove) {
            inventories.remove(id);
            setDirty();
        }

        return true;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.put("inventories", inventories);
        compoundTag.put("paths", savePaths());
        compoundTag.put("pets", savePets());

        return compoundTag;
    }


    private CompoundTag savePets() {
        var tag = new CompoundTag();


        for (var entry : pets.entrySet()) {
            var listTag = new ListTag();
            listTag.addAll(entry.getValue());
            tag.put(entry.getKey(), listTag);
        }

        return tag;
    }

    @SuppressWarnings("unchecked")
    private void loadPets(CompoundTag tag) {
        for (var key : tag.getAllKeys()) {
            var listTag = tag.getList(key, ListTag.TAG_COMPOUND);
            var list = new ArrayList<>(listTag);

            pets.put(key, (List<CompoundTag>) (Object) list);
        }
    }

    public CompoundTag savePaths() {
        var tag = new CompoundTag();
        for (var entry : paths.entrySet()) {
            tag.put(entry.getKey(), entry.getValue().write());
        }

        return tag;
    }

    public void loadPaths(CompoundTag pathsTag) {
        for (var key : pathsTag.getAllKeys()) {
            var list = pathsTag.getList(key, ListTag.TAG_COMPOUND);
            paths.put(key, SplinePath.read(list));
        }
    }

    public static MapUtilsSavedData load(CompoundTag compoundTag, HolderLookup.Provider provider) {
        var data = new MapUtilsSavedData();

        data.inventories = compoundTag.getCompound("inventories");
        data.loadPaths(compoundTag.getCompound("paths"));
        data.loadPets(compoundTag.getCompound("pets"));

        return data;
    }

    public static MapUtilsSavedData getServerData(MinecraftServer server) {
        DimensionDataStorage persistentStateManager = server.overworld().getDataStorage();
        MapUtilsSavedData data = persistentStateManager.computeIfAbsent(factory, MapUtils.MOD_ID);
        data.setDirty();

        return data;
    }

    private static final SavedData.Factory<MapUtilsSavedData> factory = new SavedData.Factory<>(
        MapUtilsSavedData::new,
        MapUtilsSavedData::load,
        null
    );
}
