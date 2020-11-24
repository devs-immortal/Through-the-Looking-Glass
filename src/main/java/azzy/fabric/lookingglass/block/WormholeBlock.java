package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.LookingGlass;
import azzy.fabric.lookingglass.entity.WormholeEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import static azzy.fabric.lookingglass.item.TTLGItems.DATA_SHARD;

public class WormholeBlock extends Block implements BlockEntityProvider {

    public WormholeBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        BlockEntity entity = world.getBlockEntity(pos);
        if(entity instanceof WormholeEntity && stack.getItem() == DATA_SHARD && stack.getOrCreateTag().contains("pos")){
            if (((WormholeEntity) entity).tryAssign(stack))
                world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 0.5f, 2f);
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 5.0, 14.0);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new WormholeEntity();
    }
}
