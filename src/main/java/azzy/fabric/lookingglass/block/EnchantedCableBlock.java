package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.EnchantedCableEntity;
import azzy.fabric.lookingglass.blockentity.SiliconCableEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class EnchantedCableBlock extends PowerPipeBlock {

    public EnchantedCableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new EnchantedCableEntity();
    }
}
