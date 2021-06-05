package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class SiliconCableEntity extends PowerPipeEntity {
    public SiliconCableEntity(BlockPos pos, BlockState state) {
        super(LookingGlassBlocks.SILICON_CABLE_ENTITY, pos, state, 64);
    }
}
