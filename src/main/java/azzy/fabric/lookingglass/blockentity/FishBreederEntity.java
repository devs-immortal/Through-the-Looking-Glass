package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.TTLGBlocks;
import azzy.fabric.lookingglass.item.TTLGItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class FishBreederEntity extends LookingGlassBE {

    public FishBreederEntity() {
        super(TTLGBlocks.FISH_BREEDER_ENTITY, 1);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return stack.getItem() == TTLGItems.FISH_FEED && inventory.get(0).getCount() < 16;
    }
}
