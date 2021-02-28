package azzy.fabric.lookingglass.item;

import azzy.fabric.lookingglass.LookingGlassCommon;
import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.Slots;
import dev.emi.trinkets.api.TrinketItem;
import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;

// This ring gives creative flight when it's equipped.
public class SimpleAngelRingItem extends TrinketItem {
    public static final AbilitySource ANGEL_RING = Pal.getAbilitySource(LookingGlassCommon.MODID, "simple_angel_ring");

    public SimpleAngelRingItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canWearInSlot(String group, String slot) {
        // Checks if the trinket can be worn in this slot.
        return (group.equals(SlotGroups.HAND) || group.equals(SlotGroups.OFFHAND)) && slot.equals(Slots.RING);
    }

    @Override
    public void onEquip(PlayerEntity player, ItemStack stack) {
        if (!player.getEntityWorld().isClient) {
            player.sendMessage(new TranslatableText("item.lookingglass.angelRing.flightEnabled"), true);
            ANGEL_RING.grantTo(player, VanillaAbilities.ALLOW_FLYING);
        }
    }

    @Override
    public void onUnequip(PlayerEntity player, ItemStack stack) {
        if (!player.getEntityWorld().isClient && !player.isCreative() && !player.isSpectator()) {
            player.sendMessage(new TranslatableText("item.lookingglass.angelRing.flightDisabled"), true);
            ANGEL_RING.revokeFrom(player, VanillaAbilities.ALLOW_FLYING);
        }
    }
}