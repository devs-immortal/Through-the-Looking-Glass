package azzy.fabric.lookingglass.item;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.lang.reflect.Field;

public class WarcrimeItem extends Item {

    private static Field bePos;
    private Field beRemoved;

    public WarcrimeItem(Settings settings) {
        super(settings);
        try {
            bePos = BlockEntity.class.getDeclaredField("pos");
            beRemoved = BlockEntity.class.getDeclaredField("removed");
        } catch (NoSuchFieldException uhoh) {
            uhoh.printStackTrace();
        }
        finally {
            if(bePos != null)
                bePos.setAccessible(true);
            if(beRemoved != null)
                beRemoved.setAccessible(true);
        }

    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockPos newPos = pos.north();
        BlockState state = world.getBlockState(pos);
        BlockEntity entity = world.getBlockEntity(pos);
        if(entity != null && !(entity instanceof PistonBlockEntity)) {
            CompoundTag tag = new CompoundTag();
            entity.toTag(tag);
            world.removeBlockEntity(pos);
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            world.setBlockState(newPos, state);
            BlockEntity newEntity = world.getBlockEntity(newPos);
            newEntity.fromTag(world.getBlockState(newPos), tag);
            try {
                bePos.set(newEntity, newPos);
                beRemoved.set(newEntity, false);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if(!world.isClient() && entity instanceof BlockEntityClientSerializable)
                ((BlockEntityClientSerializable) newEntity).sync();
        }
        return ActionResult.FAIL;
    }
}
