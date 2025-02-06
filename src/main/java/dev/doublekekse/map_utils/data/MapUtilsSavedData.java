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

import java.util.HashMap;
import java.util.Map;

public class MapUtilsSavedData extends SavedData {
    public CompoundTag inventories = new CompoundTag();
    public Map<String, SplinePath> paths = new HashMap<>();

    public void addInventory(String saveName, ListTag inventory) {
        inventories.put(saveName, inventory);
        setDirty();
    }

    public ListTag getInventory(String saveName, boolean remove) {
        if (inventories.get(saveName) == null) {
            return null;
        }

        var inventory = inventories.getList(saveName, CompoundTag.TAG_COMPOUND);

        if (remove) {
            inventories.remove(saveName);
            setDirty();
        }

        return inventory;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.put("inventories", inventories);
        compoundTag.put("paths", savePaths());

        return compoundTag;
    }

    public CompoundTag savePaths() {
        var pathsTag = new CompoundTag();
        for (var entry : paths.entrySet()) {
            pathsTag.put(entry.getKey(), entry.getValue().write());
        }

        return pathsTag;
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
