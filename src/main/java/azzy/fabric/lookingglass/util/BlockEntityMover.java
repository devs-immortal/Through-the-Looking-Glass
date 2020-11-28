package azzy.fabric.lookingglass.util;

import azzy.fabric.lookingglass.blockentity.ChunkLoaderEntity;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.lang.reflect.Field;

public class BlockEntityMover {

    private static Field bePos;
    private static Field beRemoved;

    public static boolean tryMoveEntity(World world, BlockPos pos, Direction facing) {
        return directEntityMove(world, pos, pos.offset(facing));
    }

    public static boolean directEntityMove(World world, BlockPos pos, BlockPos newPos) {
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
            if(newEntity instanceof ChunkLoaderEntity)
                ((ChunkLoaderEntity) newEntity).requestCheck();
            if(!world.isClient() && entity instanceof BlockEntityClientSerializable)
                ((BlockEntityClientSerializable) newEntity).sync();
            return true;
        }
        return false;
    }

    static {
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
}
