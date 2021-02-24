package azzy.fabric.lookingglass.item;

import azzy.fabric.lookingglass.block.TTLGBlocks;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AngelBlockItem extends BlockItem {
    public AngelBlockItem(Block angelBlock, Item.Settings angelBlockSettings) {
        super(angelBlock, angelBlockSettings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient)
            return super.use(world, user, hand);

        ItemStack itemStack = user.getStackInHand(hand);

        HitResult result = user.raycast(2 + user.getAttributeValue(ReachEntityAttributes.REACH), 1f, false);
        Vec3d hitVec = result.getPos();
        BlockPos hitPos = new BlockPos(hitVec.x, hitVec.y, hitVec.z);

        world.setBlockState(hitPos, TTLGBlocks.ANGEL_BLOCK.getDefaultState());
        itemStack.decrement(1);

        return TypedActionResult.success(itemStack);
    }
}