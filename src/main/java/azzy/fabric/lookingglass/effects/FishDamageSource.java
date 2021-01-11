package azzy.fabric.lookingglass.effects;

import net.minecraft.entity.damage.DamageSource;

public class FishDamageSource extends DamageSource {

    private final boolean ex;

    public FishDamageSource(boolean ex) {
        super(ex ? "fish_ex" : "fish");
        this.ex = ex;
    }

    @Override
    public boolean bypassesArmor() {
        return ex;
    }

    @Override
    public boolean isSourceCreativePlayer() {
        return ex;
    }

    @Override
    public boolean isUnblockable() {
        return ex;
    }

    @Override
    public boolean isOutOfWorld() {
        return ex;
    }
}
