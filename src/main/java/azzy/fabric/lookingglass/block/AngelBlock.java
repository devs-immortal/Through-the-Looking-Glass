package azzy.fabric.lookingglass.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class AngelBlock extends Block {
    public AngelBlock(FabricBlockSettings settings) {
        super(settings.breakInstantly());
    }

    public ActionResult onUse(World world, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(!world.isClient) {

        }
        return ActionResult.SUCCESS;
    }
}