package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.VacuumHopperEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

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
        return new VacuumHopperEntity(this == TTLGBlocks.VACUUM_HOPPER_BLOCK ? TTLGBlocks.VACUUM_HOPPER_ENTITY : TTLGBlocks.ADVANCED_VACUUM_HOPPER_ENTITY, inv, delay, radious);
    }
}
