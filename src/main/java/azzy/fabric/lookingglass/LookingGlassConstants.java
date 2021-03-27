package azzy.fabric.lookingglass;

import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;

import java.util.UUID;

public interface LookingGlassConstants {
    AbilitySource ADVANCED_ANGEL_RING_ABILITY_SOURCE = Pal.getAbilitySource(LookingGlassCommon.MODID, "advanced_angel_ring");
    AbilitySource ANGEL_RING = Pal.getAbilitySource(LookingGlassCommon.MODID, "simple_angel_ring");
    String ITEM_LOOKINGGLASS_ANGEL_RING_FLIGHT_DISABLED = "item.lookingglass.angelRing.flightDisabled";
    String ITEM_LOOKINGGLASS_ANGEL_RING_FLIGHT_ENABLED = "item.lookingglass.angelRing.flightEnabled";
    UUID CURSE_UUID = UUID.fromString("A223344A-B55B-C66C-D77D-E8888888888E");
    String ITEM_LOOKINGGLASS_GOLDENLASSO_PEACEFUL = "item.lookingglass.goldenlasso.peaceful";
    String ITEM_LOOKINGGLASS_GOLDENLASSO_TABOO_ENTITY = "item.lookingglass.goldenlasso.taboo_entity";
    String ITEM_LOOKINGGLASS_GOLDENLASSO_HOSTILE_MOB = "item.lookingglass.goldenlasso.hostile_mob";
    String UNSTABLE_MULTI_BLOCK_FORMED = "unstable.multiblock";
}
