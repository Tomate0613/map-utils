package dev.doublekekse.map_utils.duck;

import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

// Currently needed since `Inventory#save` doesn't save equipment
public interface InventoryDuck {
    void mapUtils$saveEquipment(ValueOutput.TypedOutputList<ItemStackWithSlot> typedOutputList);
    void mapUtils$loadEquipment(ValueInput.TypedInputList<ItemStackWithSlot> typedInputList);
}
