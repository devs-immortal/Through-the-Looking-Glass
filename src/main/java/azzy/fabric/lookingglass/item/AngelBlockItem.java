package azzy.fabric.lookingglass.item;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AngelBlockItem extends Item {
    public AngelBlockItem(FabricItemSettings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos pos = context.getBlockPos();
        World world = context.getWorld();
        ItemStack itemStack = context.getStack();
        BlockState state = world.getBlockState(pos);

        itemStack.useOnBlock(context);
        itemStack.decrement(1);

//        world.setBlockState(pos, state);

        return ActionResult.SUCCESS;
    }

//    @Override
//    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
//        ItemStack itemStack = user.getStackInHand(hand);
//
//        HitResult result = user.raycast(3 + user.getAttributeValue(ReachEntityAttributes.REACH), 1f, false);
//        Vec3d hitVec = result.getPos();
//        BlockPos hitPos = new BlockPos(hitVec.x, hitVec.y, hitVec.z);
//        BlockState state = world.getBlockState(hitPos);
//
//        world.setBlockState(hitPos, state);
//
//        return TypedActionResult.success(itemStack);
//    }
}