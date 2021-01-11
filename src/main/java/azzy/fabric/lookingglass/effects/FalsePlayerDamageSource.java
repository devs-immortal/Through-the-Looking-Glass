package azzy.fabric.lookingglass.effects;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;

import java.lang.reflect.Field;
import java.util.UUID;

public class FalsePlayerDamageSource extends ErasureDamageSource {

    Field aaaAAA;

    {
        try {
            aaaAAA = EntityDamageSource.class.getDeclaredField("source");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public FalsePlayerDamageSource(String name, ServerPlayerEntity player, boolean bypass, boolean unblockable, boolean outOfWorld) {
        super(name, null, bypass, unblockable, outOfWorld);
        aaaAAA.setAccessible(true);
        try {
            aaaAAA.set(this, player);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
