package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.util.InventoryWrapper;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class LookingGlassBE extends BlockEntity implements SidedInventory, InventoryWrapper, BlockEntityClientSerializable {

    protected final int offset = LookingGlassCommon.RANDOM.nextInt(20);
    protected DefaultedList<ItemStack> inventory;

    public LookingGlassBE(BlockEntityType<?> type, BlockPos pos, BlockState state, int invSize) {
        super(type, pos, state);
        inventory = DefaultedList.ofSize(invSize, ItemStack.EMPTY);
    }

    public abstract void tick();

    public static <T extends BlockEntity> void tickStatic(World world, BlockPos pos, BlockState state, T t) {
        ((LookingGlassBE) t).tick();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        Inventories.writeNbt(tag, inventory);
        return super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        Inventories.readNbt(tag, inventory);
        super.readNbt(tag);
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        Inventories.readNbt(tag, inventory);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        Inventories.writeNbt(tag, inventory);
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
        if(world != null && !world.isClient()) {
            markDirty();
            sync();
        }
        return InventoryWrapper.super.getStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        InventoryWrapper.super.setStack(slot, stack);
        if(world != null && !world.isClient()) {
            markDirty();
            sync();
        }
    }

    @Override
    public ItemStack removeStack(int slot, int count) {
        if(world != null && !world.isClient()) {
            markDirty();
            sync();
        }
        return InventoryWrapper.super.removeStack(slot, count);
    }
}
