package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.SpecialAnnulationCoreEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SpecialAnnullationCoreBlock extends CoreBlock implements BlockEntityProvider {

    public SpecialAnnullationCoreBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity != null)
            ((SpecialAnnulationCoreEntity) blockEntity).attack();
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new SpecialAnnulationCoreEntity();
    }
}
