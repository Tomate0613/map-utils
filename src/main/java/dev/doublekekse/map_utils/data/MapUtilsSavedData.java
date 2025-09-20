package dev.doublekekse.map_utils.data;

import com.mojang.serialization.Codec;
import dev.doublekekse.map_utils.MapUtils;
import dev.doublekekse.map_utils.curve.SplinePath;
import dev.doublekekse.map_utils.duck.InventoryDuck;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

// TODO: Abstract mod compat into single class modules. Sorta like what Switchy's got going on.
public class MapUtilsSavedData extends SavedData {
    public CompoundTag inventories = new CompoundTag();
    public Map<String, List<CompoundTag>> pets = new HashMap<>();
    public Map<String, SplinePath> paths = new HashMap<>();

    public static final Codec<MapUtilsSavedData> CODEC = CompoundTag.CODEC.xmap(
        MapUtilsSavedData::load,
        MapUtilsSavedData::save
    );

    public void setPets(String id, List<CompoundTag> pets) {
        this.pets.put(id, pets);
    }

    public List<CompoundTag> getPets(String id) {
        return this.pets.get(id);
    }

    public void saveInventories(Player player, String id, boolean remove) {
        var inventory = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, player.level().registryAccess());
        player.getInventory().save(inventory.list("minecraft:inventory", ItemStackWithSlot.CODEC));
        ((InventoryDuck)player.getInventory()).mapUtils$saveEquipment(inventory.list("minecraft:equipment", ItemStackWithSlot.CODEC));

        if (remove) {
            player.getInventory().clearContent();
        }

        inventories.put(id, inventory.buildResult());
        setDirty();
    }

    public boolean loadInventories(Player player, String id, boolean remove) {
        var tag = inventories.get(id);
        if (tag == null || tag.asCompound().isEmpty()) {
            return false;
        }

        var inventory = TagValueInput.create(ProblemReporter.DISCARDING, player.level().registryAccess(), tag.asCompound().get());

        inventory.list("minecraft:inventory", ItemStackWithSlot.CODEC).ifPresent(itemStackWithSlots -> player.getInventory().load(itemStackWithSlots));
        inventory.list("minecraft:equipment", ItemStackWithSlot.CODEC).ifPresent(itemStackWithSlots -> ((InventoryDuck) player.getInventory()).mapUtils$loadEquipment(itemStackWithSlots));

        if (remove) {
            inventories.remove(id);
            setDirty();
        }

        return true;
    }

    public @NotNull CompoundTag save() {
        var tag = new CompoundTag();

        tag.put("inventories", inventories);
        tag.put("paths", savePaths());
        tag.put("pets", savePets());

        return tag;
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
        for (var entry : tag.entrySet()) {
            var listTag = entry.getValue().asList().get();
            var list = new ArrayList<>(listTag);

            pets.put(entry.getKey(), (List<CompoundTag>) (Object) list);
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
        for (var entry : pathsTag.entrySet()) {
            var list = entry.getValue().asList().get();
            paths.put(entry.getKey(), SplinePath.read(list));
        }
    }

    public static MapUtilsSavedData load(CompoundTag compoundTag) {
        var data = new MapUtilsSavedData();

        data.inventories = compoundTag.getCompound("inventories").get();
        data.loadPaths(compoundTag.getCompound("paths").get());
        data.loadPets(compoundTag.getCompound("pets").get());

        return data;
    }

    public static MapUtilsSavedData getServerData(MinecraftServer server) {
        DimensionDataStorage persistentStateManager = server.overworld().getDataStorage();
        MapUtilsSavedData data = persistentStateManager.computeIfAbsent(TYPE);
        data.setDirty();

        return data;
    }

    private static final SavedDataType<MapUtilsSavedData> TYPE = new SavedDataType<>(MapUtils.MOD_ID, MapUtilsSavedData::new,
        MapUtilsSavedData.CODEC,
        null);
}
