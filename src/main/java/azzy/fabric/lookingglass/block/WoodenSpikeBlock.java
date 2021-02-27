package azzy.fabric.lookingglass.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class WoodenSpikeBlock extends LookingGlassBlock {
    public WoodenSpikeBlock(FabricBlockSettings settings) {
        super(settings, false);
    }

    /**
     * I want mobs to be able to spawn in this block, as if this block is like grass.
     *
     * @return true
     */
    @Override
    public boolean canMobSpawnInside() {
        return true;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
        return 0;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if ((entity instanceof PlayerEntity) || !(entity instanceof LivingEntity))
            return;

        // This is a living entity, but not a player entity.
        float currentHealth = ((LivingEntity) entity).getHealth();

        if (currentHealth == 1)
            return;

        entity.damage(DamageSource.MAGIC, 1);
    }
}