package dev.doublekekse.festlyutils.data;

import dev.doublekekse.festlyutils.FestlyUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

public class FestlyUtilsSavedData extends SavedData {
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

    public static FestlyUtilsSavedData load(CompoundTag compoundTag, HolderLookup.Provider provider) {
        var data = new FestlyUtilsSavedData();

        data.inventories = compoundTag.getCompound("inventories");
        return data;
    }

    public static FestlyUtilsSavedData getServerData(MinecraftServer server) {
        DimensionDataStorage persistentStateManager = server.overworld().getDataStorage();
        FestlyUtilsSavedData data = persistentStateManager.computeIfAbsent(factory, FestlyUtils.MOD_ID);
        data.setDirty();

        return data;
    }

    private static final SavedData.Factory<FestlyUtilsSavedData> factory = new SavedData.Factory<>(
        FestlyUtilsSavedData::new,
        FestlyUtilsSavedData::load,
        null
    );
}
