package dev.doublekekse.map_utils.data;

import dev.doublekekse.map_utils.MapUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

public class MapUtilsSavedData extends SavedData {
    private CompoundTag inventories = new CompoundTag();

    public void addInventory(String saveName, ListTag inventory) {
        inventories.put(saveName, inventory);
        setDirty();
    }

    public ListTag getInventory(String saveName, boolean remove) {
        if(inventories.get(saveName) == null) {
            return null;
        }

        var inventory = inventories.getList(saveName, CompoundTag.TAG_COMPOUND);

        if(remove) {
            inventories.remove(saveName);
            setDirty();
        }

        return inventory;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.put("inventories", inventories);
        return compoundTag;
    }

    public static MapUtilsSavedData load(CompoundTag compoundTag, HolderLookup.Provider provider) {
        var data = new MapUtilsSavedData();

        data.inventories = compoundTag.getCompound("inventories");
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
