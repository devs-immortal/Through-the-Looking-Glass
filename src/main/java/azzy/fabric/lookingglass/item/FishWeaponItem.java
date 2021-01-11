package azzy.fabric.lookingglass.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.Nullable;

public class FishWeaponItem extends SwordItem {

    private final boolean divine;

    public FishWeaponItem(boolean divine, int attackDamage, Settings settings) {
        super(TTLGToolMaterials.FISH, attackDamage, -2.2F, settings);
        this.divine = divine;
    }

    @Override
    public boolean isDamageable() {
        return !divine;
    }

    @Override
    public boolean isFood() {
        return true;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack out = stack.copy();
        user.eatFood(world, stack);
        if(!this.divine && user instanceof ServerPlayerEntity)
            stack.damage(4, world.getRandom(), (ServerPlayerEntity) user);
        return out;
    }

    public boolean isDivine() {
        return divine;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return divine;
    }
}
