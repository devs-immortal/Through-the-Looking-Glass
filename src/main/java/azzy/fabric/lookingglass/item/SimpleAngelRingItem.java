package azzy.fabric.lookingglass.item;

import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.Slots;
import dev.emi.trinkets.api.TrinketItem;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import static azzy.fabric.lookingglass.LookingGlassConstants.*;

// This ring gives creative flight when it's equipped.
public class SimpleAngelRingItem extends TrinketItem {
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
            // Stupid network handler will be null if the client is just starting up.
            if (((ServerPlayerEntity) player).networkHandler != null)
                player.sendMessage(new TranslatableText(ITEM_LOOKINGGLASS_ANGEL_RING_FLIGHT_ENABLED), true);

            ANGEL_RING.grantTo(player, VanillaAbilities.ALLOW_FLYING);
        }
    }

    @Override
    public void onUnequip(PlayerEntity player, ItemStack stack) {
        if (!player.getEntityWorld().isClient && !player.isCreative() && !player.isSpectator()) {
            // Stupid network handler will be null if the client is just starting up.
            if (((ServerPlayerEntity) player).networkHandler != null)
                player.sendMessage(new TranslatableText(ITEM_LOOKINGGLASS_ANGEL_RING_FLIGHT_DISABLED), true);

            ANGEL_RING.revokeFrom(player, VanillaAbilities.ALLOW_FLYING);
        }
    }
}
