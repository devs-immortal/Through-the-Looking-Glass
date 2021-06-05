package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.item.LookingGlassItems;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class FishBreederEntity extends LookingGlassBE {

    public FishBreederEntity(BlockPos pos, BlockState state) {
        super(LookingGlassBlocks.FISH_BREEDER_ENTITY, pos, state, 1);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return stack.getItem() == LookingGlassItems.FISH_FEED && inventory.get(0).getCount() < 16;
    }
}
