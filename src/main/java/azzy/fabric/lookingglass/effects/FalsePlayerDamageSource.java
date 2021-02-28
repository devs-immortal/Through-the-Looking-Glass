package azzy.fabric.lookingglass.effects;

import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.lang.reflect.Field;

public class FalsePlayerDamageSource extends ErasureDamageSource {

    Field sourceField;

    {
        try {
            sourceField = EntityDamageSource.class.getDeclaredField("source");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public FalsePlayerDamageSource(String name, ServerPlayerEntity player, boolean bypass, boolean unblockable, boolean outOfWorld) {
        super(name, null, bypass, unblockable, outOfWorld);
        sourceField.setAccessible(true);
        try {
            sourceField.set(this, player);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
