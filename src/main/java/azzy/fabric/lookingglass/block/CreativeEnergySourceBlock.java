package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.CreativeEnergySourceEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class CreativeEnergySourceBlock extends LookingGlassBlock implements BlockEntityProvider {

    public CreativeEnergySourceBlock(Settings settings) {
        super(settings, true);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeEnergySourceEntity(pos, state);
    }
}
