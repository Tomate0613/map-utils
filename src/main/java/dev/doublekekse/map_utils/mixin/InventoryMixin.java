package dev.doublekekse.map_utils.mixin;

import dev.doublekekse.map_utils.duck.InventoryDuck;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

// Currently needed since `Inventory#save` doesn't save equipment
@Mixin(Inventory.class)
public abstract class InventoryMixin implements InventoryDuck {
    @Shadow
    @Final
    private EntityEquipment equipment;

    @Shadow
    @Final
    public static Int2ObjectMap<EquipmentSlot> EQUIPMENT_SLOT_MAPPING;

    @Shadow
    public abstract int getContainerSize();

    @Shadow
    public abstract void setItem(int i, ItemStack itemStack);

    @Override
    public void mapUtils$saveEquipment(ValueOutput.TypedOutputList<ItemStackWithSlot> typedOutputList) {
        for (var slot : EQUIPMENT_SLOT_MAPPING.int2ObjectEntrySet()) {
            var itemStack = equipment.get(slot.getValue());

            if (!itemStack.isEmpty()) {
                typedOutputList.add(new ItemStackWithSlot(slot.getIntKey(), itemStack));
            }
        }
    }

    @Override
    public void mapUtils$loadEquipment(ValueInput.TypedInputList<ItemStackWithSlot> typedInputList) {
        this.equipment.clear();

        for (ItemStackWithSlot itemStackWithSlot : typedInputList) {
            if (itemStackWithSlot.isValidInContainer(getContainerSize())) {
                setItem(itemStackWithSlot.slot(), itemStackWithSlot.stack());
            }
        }
    }
}
