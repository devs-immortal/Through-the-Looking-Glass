package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class GuildedCableEntity extends PowerPipeEntity {
    public GuildedCableEntity(BlockPos pos, BlockState state) {
        super(LookingGlassBlocks.GUILDED_CABLE_ENTITY, pos, state, 256);
    }
}
