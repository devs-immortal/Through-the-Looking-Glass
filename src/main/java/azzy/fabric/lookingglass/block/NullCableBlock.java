package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.NullCableEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class NullCableBlock extends PowerPipeBlock {

    public NullCableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new NullCableEntity();
    }
}
