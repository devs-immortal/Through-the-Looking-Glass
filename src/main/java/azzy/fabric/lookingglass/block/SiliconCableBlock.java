package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.BasePipeEntity;
import azzy.fabric.lookingglass.blockentity.EnchantedCableEntity;
import azzy.fabric.lookingglass.blockentity.SiliconCableEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SiliconCableBlock extends PowerPipeBlock {

    public SiliconCableBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return BasePipeEntity.getTicker(world, type, LookingGlassBlocks.SILICON_CABLE_ENTITY);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SiliconCableEntity(pos, state);
    }
}
