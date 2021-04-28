package azzy.fabric.lookingglass.entity;

import azzy.fabric.lookingglass.LookingGlassCommon;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class LookingGlassEntities {

    public static final EntityType<FlarefinKoiEntity> FLAREFIN_KOI_ENTITY_TYPE = register("flarefin_koi", FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, FlarefinKoiEntity::new).dimensions(EntityDimensions.fixed(1F, 0.3125F)).build(), FlarefinKoiEntity.createFlarefinAttributes());

    public static final EntityType<RevolverShotEntity> REVOLVER_SHOT_ENTITY_TYPE = register("revolver_shot", FabricEntityTypeBuilder.<RevolverShotEntity>create(SpawnGroup.MISC, RevolverShotEntity::new).dimensions(EntityDimensions.fixed(.1F,.1F)).trackRangeBlocks(4).trackedUpdateRate(10).build());

    public static final EntityType<TossedCoinEntity> TOSSED_COIN_ENTITY_TYPE = register("tossed_coin", FabricEntityTypeBuilder.<TossedCoinEntity>create(SpawnGroup.MISC, TossedCoinEntity::new).dimensions(EntityDimensions.fixed(.25F, .25F)).trackRangeBlocks(4).trackedUpdateRate(10).build());

    public static void init() {
        Registry.register(Registry.PAINTING_MOTIVE, new Identifier(LookingGlassCommon.MODID, "test"), new PaintingMotive(128, 128));
    }

    public static <E extends LivingEntity> EntityType<E> register(String name, EntityType<E> type, @Nullable DefaultAttributeContainer.Builder attributes) {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(LookingGlassCommon.MODID, name), type);
        if(attributes != null)
            FabricDefaultAttributeRegistry.register(type, attributes);
        return type;
    }

    public static <E extends Entity> EntityType<E> register(String name, EntityType<E> type) {
        return Registry.register(Registry.ENTITY_TYPE, new Identifier(LookingGlassCommon.MODID, name), type);
    }
}
