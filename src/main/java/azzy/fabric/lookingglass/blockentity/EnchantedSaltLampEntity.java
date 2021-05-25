package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Box;

import java.util.List;

public class EnchantedSaltLampEntity extends BlockEntity implements Tickable {

    public EnchantedSaltLampEntity() {
        super(LookingGlassBlocks.ENCHANTED_SALT_LAMP_ENTITY);
    }

    @Override
    public void tick() {
        if(world != null && !world.isClient() && (world.getTime() ) % 100 == 0) {
            List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, new Box(pos.add(-5, -7, -5), pos.add(6, 4, 6)), livingEntity -> true);
            for (LivingEntity entity : entities) {
                entity.applyStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1, true, false, true));
            }
        }
    }
}
