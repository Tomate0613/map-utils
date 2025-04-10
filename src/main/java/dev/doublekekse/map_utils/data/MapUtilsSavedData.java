package dev.doublekekse.map_utils.data;

import dev.doublekekse.map_utils.MapUtils;
import dev.doublekekse.map_utils.curve.SplinePath;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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

    public void setInventory(String id, ListTag inventory) {
        inventories.put(id, inventory);
        setDirty();
    }

    public ListTag getInventory(String id, boolean remove) {
        if (inventories.get(id) == null) {
            return null;
        }

        var inventory = inventories.getList(id, CompoundTag.TAG_COMPOUND);

        if (remove) {
            inventories.remove(id);
            setDirty();
        }

        return inventory;
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
