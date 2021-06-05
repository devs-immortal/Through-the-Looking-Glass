package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class NullCableEntity extends PowerPipeEntity {
    public NullCableEntity(BlockPos pos, BlockState state) {
        super(LookingGlassBlocks.NULL_CABLE_ENTITY, pos, state, 16384);
    }
}
