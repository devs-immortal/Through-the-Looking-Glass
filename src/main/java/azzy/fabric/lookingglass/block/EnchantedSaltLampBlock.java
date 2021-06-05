package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.EnchantedSaltLampEntity;
import azzy.fabric.lookingglass.blockentity.LookingGlassBE;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnchantedSaltLampBlock extends SaltLampBlock implements BlockEntityProvider {

    public EnchantedSaltLampBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return LookingGlassBE::tickStatic;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EnchantedSaltLampEntity(pos, state);
    }
}
