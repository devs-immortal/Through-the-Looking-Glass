package azzy.fabric.lookingglass.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.EntityDamageSource;
import org.jetbrains.annotations.Nullable;

public class PiercingEntityDamageSource extends EntityDamageSource {

    public PiercingEntityDamageSource(String name, @Nullable Entity source, boolean fire) {
        super(name, source);
        if(fire)
            setFire();
    }

    @Override
    public boolean bypassesArmor() {
        return true;
    }
}
