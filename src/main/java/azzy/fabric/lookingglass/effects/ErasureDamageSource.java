package azzy.fabric.lookingglass.effects;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

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
