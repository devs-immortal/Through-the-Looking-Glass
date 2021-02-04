package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.CreativeEnergySourceEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class CreativeEnergySourceBlock extends LookingGlassBlock implements BlockEntityProvider {

    public CreativeEnergySourceBlock(Settings settings) {
        super(settings, true);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new CreativeEnergySourceEntity();
    }
}
