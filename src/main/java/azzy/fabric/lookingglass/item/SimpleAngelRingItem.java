package azzy.fabric.lookingglass.item;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import static azzy.fabric.lookingglass.LookingGlassConstants.*;

// This ring gives creative flight when it's equipped.
public class SimpleAngelRingItem extends TrinketItem {
    public SimpleAngelRingItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getEntityWorld().isClient() && entity instanceof PlayerEntity) {
            ANGEL_RING.grantTo((PlayerEntity) entity, VanillaAbilities.ALLOW_FLYING);
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getEntityWorld().isClient() && entity instanceof PlayerEntity) {
            ANGEL_RING.revokeFrom((PlayerEntity) entity, VanillaAbilities.ALLOW_FLYING);
        }
    }
}
