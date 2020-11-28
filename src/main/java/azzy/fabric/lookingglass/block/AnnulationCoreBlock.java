package azzy.fabric.lookingglass.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AnnulationCoreBlock extends CoreBlock {

    private final int damage;
    private final boolean allBreak;
    private final DamageSource damageType;

    public AnnulationCoreBlock(Settings settings, int damage, boolean allBreak, DamageSource damageType) {
        super(settings);
        this.damage = damage;
        this.allBreak = allBreak;
        this.damageType = damageType;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);
        entity.damage(damageType, damage);
    }

    public boolean canBreakAll() {
        return allBreak;
    }
}
