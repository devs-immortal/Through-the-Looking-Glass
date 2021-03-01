package azzy.fabric.lookingglass.effects;

import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.lang.reflect.Field;

public class FalsePlayerDamageSource extends ErasureDamageSource {

    public FalsePlayerDamageSource(String name, ServerPlayerEntity player, boolean bypass, boolean unblockable, boolean outOfWorld) {
        super(name, player, bypass, unblockable, outOfWorld);
    }
}
