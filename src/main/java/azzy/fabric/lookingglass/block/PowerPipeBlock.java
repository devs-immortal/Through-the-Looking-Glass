package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.PowerPipeEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class EnergyPipeBlock extends PipeBlock {

    public EnergyPipeBlock(Settings settings) {
        super(settings, true);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new PowerPipeEntity();
    }
}
