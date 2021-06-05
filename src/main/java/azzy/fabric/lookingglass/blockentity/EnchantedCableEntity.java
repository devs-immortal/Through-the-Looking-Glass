package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class EnchantedCableEntity extends PowerPipeEntity {
    public EnchantedCableEntity(BlockPos pos, BlockState state) {
        super(LookingGlassBlocks.ENCHANTED_CABLE_ENTITY, pos, state, 1024);
    }
}
