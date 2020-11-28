package azzy.fabric.lookingglass.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class DataShardItem extends Item {
    public DataShardItem(Settings settings) {
        super(settings);
    }


    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos pos = context.getBlockPos();
        World world = context.getWorld();
        BlockState state = world.getBlockState(pos);
        if(context.getPlayer().isSneaking()) {
            CompoundTag tag = context.getStack().getOrCreateTag();
            clearNBT(context.getStack());
            tag.putLong("pos", pos.asLong());
            tag.putString("type", Registry.BLOCK.getId(world.getBlockState(pos).getBlock()).toString());
            context.getPlayer().getItemCooldownManager().set(this, 5);
            if(!world.isClient()) {
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.PLAYERS, 1f, 1f, true);
            }
        }
        return super.useOnBlock(context);
    }
    private void clearNBT(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.remove("pos");
        tag.remove("type");
        tag.remove("entity");
        tag.remove("inv");
    }
}
