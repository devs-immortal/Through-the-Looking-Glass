package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.SiliconCableEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class SiliconCableBlock extends PowerPipeBlock {

    public SiliconCableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new SiliconCableEntity();
    }
}
