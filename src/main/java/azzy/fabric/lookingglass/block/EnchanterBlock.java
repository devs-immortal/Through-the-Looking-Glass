package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.EnchanterEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnchanterBlock extends LookingGlassBlock {

    public EnchanterBlock(Settings settings) {
        super(settings, false);
    }

    @Override
    protected void pulseUpdate(BlockState state, World world, BlockPos pos, boolean on) {
        BlockEntity entity = world.getBlockEntity(pos);
        if(entity != null) {
            ((EnchanterEntity) entity).notifyRedstoneActivation();
        }
    }
}
