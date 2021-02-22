package azzy.fabric.lookingglass.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AngelBlockItem extends Item {
    public AngelBlockItem(FabricItemSettings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        Vec3d userPos = user.getPos();
        int x = (int) Math.floor(userPos.x);
        int y = (int) Math.floor(user.getEyeY());
        int z = (int) Math.floor(userPos.z);

        Vec3d lookVec = user.getRotationVector();
//        EnumFacing side = null;
        return null;
    }
}