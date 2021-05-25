package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.EnchantedSaltLampEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class EnchantedSaltLampBlock extends SaltLampBlock implements BlockEntityProvider {

    public EnchantedSaltLampBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new EnchantedSaltLampEntity();
    }
}
