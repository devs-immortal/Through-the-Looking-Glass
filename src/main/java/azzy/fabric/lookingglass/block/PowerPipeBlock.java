package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.PowerPipeEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public abstract class PowerPipeBlock extends PipeBlock {

    public PowerPipeBlock(Settings settings) {
        super(settings, true);
    }
}
