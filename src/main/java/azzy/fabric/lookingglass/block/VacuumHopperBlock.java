package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.VacuumHopperEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class VacuumHopperBlock extends LookingGlassBlock implements BlockEntityProvider {

    private final int delay, radious, inv;

    public VacuumHopperBlock(Settings settings,  int delay, int radious, int inv) {
        super(settings, true);
        this.delay = delay;
        this.radious = radious;
        this.inv = inv;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new VacuumHopperEntity(this == LookingGlassBlocks.VACUUM_HOPPER_BLOCK ? LookingGlassBlocks.VACUUM_HOPPER_ENTITY : LookingGlassBlocks.ADVANCED_VACUUM_HOPPER_ENTITY, inv, delay, radious);
    }
}
