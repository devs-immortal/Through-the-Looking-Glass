package azzy.fabric.lookingglass.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;

public interface SpecialDamager {
    void SpecialAttack(float damage, Entity target, DamageSource type);
}
