package azzy.fabric.lookingglass.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.EntityDamageSource;

public class ErasureDamageSource extends EntityDamageSource {

    private final boolean bypass, unblockable, outOfWorld;

    public ErasureDamageSource(String name, LivingEntity source, boolean bypass, boolean unblockable, boolean outOfWorld) {
        super(name, source);
        this.bypass = bypass;
        this.unblockable = unblockable;
        this.outOfWorld = outOfWorld;
    }

    @Override
    public boolean bypassesArmor() {
        return bypass;
    }

    @Override
    public boolean isUnblockable() {
        return unblockable;
    }

    @Override
    public boolean isOutOfWorld() {
        return outOfWorld;
    }
}
