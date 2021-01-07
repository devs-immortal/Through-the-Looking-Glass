package azzy.fabric.lookingglass.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.InvocationTargetException;

public class FalsePlayerDamageSource extends EntityDamageSource {

    private static PlayerEntity fakePlayer = null;
    private final boolean bypass, unblockable, outOfWorld;

    static {
        try {
            fakePlayer = (PlayerEntity) PlayerEntity.class.getConstructors()[0].newInstance(null, BlockPos.ORIGIN, 0F, null);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public FalsePlayerDamageSource(String name, boolean bypass, boolean unblockable, boolean outOfWorld) {
        super(name, fakePlayer);
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
