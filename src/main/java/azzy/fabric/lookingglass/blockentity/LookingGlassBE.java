package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.util.InventoryWrapper;
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class LookingGlassBE extends BlockEntity implements SidedInventory, InventoryWrapper, BlockEntityClientSerializable {

    protected final int offset = LookingGlassCommon.RANDOM.nextInt(20);
    protected DefaultedList<ItemStack> inventory;

    public LookingGlassBE(BlockEntityType<?> type, int invSize) {
        super(type);
        inventory = DefaultedList.ofSize(invSize, ItemStack.EMPTY);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Inventories.toTag(tag, inventory);
        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        Inventories.fromTag(tag, inventory);
        super.fromTag(state, tag);
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        Inventories.fromTag(tag, inventory);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        Inventories.toTag(tag, inventory);
        return tag;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        int[] result = new int[inventory.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = i;
        }
        return result;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public ItemStack getStack(int slot) {
        if(!world.isClient()) {
            markDirty();
            sync();
        }
        return InventoryWrapper.super.getStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        InventoryWrapper.super.setStack(slot, stack);
        if(!world.isClient()) {
            markDirty();
            sync();
        }
    }

    @Override
    public ItemStack removeStack(int slot, int count) {
        if(!world.isClient()) {
            markDirty();
            sync();
        }
        return InventoryWrapper.super.removeStack(slot, count);
    }
}
