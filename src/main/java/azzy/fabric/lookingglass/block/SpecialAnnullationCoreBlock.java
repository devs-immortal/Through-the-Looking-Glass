package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.SpecialAnnulationCoreEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SpecialAnnullationCoreBlock extends CoreBlock implements BlockEntityProvider {

    public SpecialAnnullationCoreBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        SpecialAnnulationCoreEntity entity = (SpecialAnnulationCoreEntity) world.getBlockEntity(pos);
        ItemStack handStack = player.getStackInHand(hand);
        if(!entity.isItemSet() && handStack != ItemStack.EMPTY) {
            entity.setItem(handStack);
            player.setStackInHand(hand, ItemStack.EMPTY);
            return ActionResult.CONSUME;
        }
        else if(entity.isItemSet() && handStack == ItemStack.EMPTY) {
            player.setStackInHand(hand, entity.getItem());
            return ActionResult.CONSUME;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        ItemScatterer.spawn(world, pos, (Inventory) world.getBlockEntity(pos));
        super.onBreak(world, pos, state, player);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if(!world.isClient()) {
            super.onEntityCollision(state, world, pos, entity);
            if((entity instanceof PlayerEntity && (((PlayerEntity) entity).isCreative())) || entity.isSpectator())
                return;
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(entity instanceof LivingEntity && blockEntity != null && world.getTime() % 20 == 0)
                ((SpecialAnnulationCoreEntity) blockEntity).attack((LivingEntity) entity);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SpecialAnnulationCoreEntity(pos, state);
    }
}
