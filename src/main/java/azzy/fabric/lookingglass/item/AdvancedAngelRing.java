package azzy.fabric.lookingglass.item;

import azzy.fabric.lookingglass.LookingGlassCommon;
import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.MessageType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.world.World;

// This Ring will have an expensive recipe.  This is really not a ring.  It's an item that on use, rewards the user with creative flight - forever.
// On use again, it will remove creative flight.  So you get flight without having to carry around the item.
// The recipe cost will reflect the same.
// For a simpler item that gives creative flight on equip, check out the SimpleAngelRing class.
public class AdvancedAngelRing extends Item {
    public static final AbilitySource ADVANCED_ANGEL_RING_ABILITY_SOURCE = Pal.getAbilitySource(LookingGlassCommon.MODID, "advanced_angel_ring");
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public AdvancedAngelRing(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            // Check if the item grants flight.
            if (ADVANCED_ANGEL_RING_ABILITY_SOURCE.grants(user, VanillaAbilities.ALLOW_FLYING)) {
                // If the item is giving flight, remove it.
                ADVANCED_ANGEL_RING_ABILITY_SOURCE.revokeFrom(user, VanillaAbilities.ALLOW_FLYING);
                client.inGameHud.addChatMessage(MessageType.GAME_INFO, new TranslatableText("item.lookingglass.angelRing.flightDisabled"), Util.NIL_UUID);
            } else {
                // Otherwise, grant it.
                ADVANCED_ANGEL_RING_ABILITY_SOURCE.grantTo(user, VanillaAbilities.ALLOW_FLYING);
                client.inGameHud.addChatMessage(MessageType.GAME_INFO, new TranslatableText("item.lookingglass.angelRing.flightEnabled"), Util.NIL_UUID);
            }
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }
}