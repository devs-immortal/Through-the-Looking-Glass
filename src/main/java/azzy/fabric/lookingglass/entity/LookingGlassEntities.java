package azzy.fabric.lookingglass.entity;

import azzy.fabric.lookingglass.LookingGlassCommon;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class LookingGlassEntities {

    public static final EntityType<FlarefinKoiEntity> FLAREFIN_KOI_ENTITY_TYPE = register("flarefin_koi", FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, FlarefinKoiEntity::new).dimensions(EntityDimensions.fixed(1F, 0.3125F)).build(), FlarefinKoiEntity.createFlarefinAttributes());


    public static void init() {}

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
