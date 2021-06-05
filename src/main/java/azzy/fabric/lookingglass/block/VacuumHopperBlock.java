package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.LookingGlassBE;
import azzy.fabric.lookingglass.blockentity.VacuumHopperEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class VacuumHopperBlock extends LookingGlassBlock implements BlockEntityProvider {

    private final int delay, radious, inv;

    public VacuumHopperBlock(Settings settings,  int delay, int radius, int inv) {
        super(settings, true);
        this.delay = delay;
        this.radious = radius;
        this.inv = inv;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : LookingGlassBE::tickStatic;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new VacuumHopperEntity(this == LookingGlassBlocks.VACUUM_HOPPER_BLOCK ? LookingGlassBlocks.VACUUM_HOPPER_ENTITY : LookingGlassBlocks.ADVANCED_VACUUM_HOPPER_ENTITY, pos, state, inv, delay, radious);
    }
}
