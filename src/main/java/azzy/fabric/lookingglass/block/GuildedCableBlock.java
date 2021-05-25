package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.GuildedCableEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class GuildedCableBlock extends PowerPipeBlock {

    public GuildedCableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new GuildedCableEntity();
    }
}
