package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.BlockTesseractEntity;
import azzy.fabric.lookingglass.item.DataShardItem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Optional;

public class BlockTesseractBlock extends AbstractTesseractBlock {

    public BlockTesseractBlock(Settings settings) {
        super(BlockTesseractEntity::new, settings);
    }

    @Override
    void activate(World world, BlockPos pos, Direction dir, BlockPos trigger) {
        BlockEntity entity = world.getBlockEntity(pos);
        if(entity != null) {
            ((BlockTesseractEntity) entity).moveBlock(dir);
        }
    }

    @Override
    boolean setData(World world, BlockPos pos, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        BlockEntity entity = world.getBlockEntity(pos);
        return ((BlockTesseractEntity) entity).setTarget((Optional<Long>) DataShardItem.getData(stack, DataShardItem.DataType.POS));
    }
}
