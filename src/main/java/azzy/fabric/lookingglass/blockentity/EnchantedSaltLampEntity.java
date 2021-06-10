package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;

public class EnchantedSaltLampEntity extends BlockEntity implements LookingGlassTickable {

    public EnchantedSaltLampEntity(BlockPos pos, BlockState state) {
        super(LookingGlassBlocks.ENCHANTED_SALT_LAMP_ENTITY, pos, state);
    }

    @Override
    public void tick() {
        if(world != null && !world.isClient() && (world.getTime() ) % 100 == 0) {
            List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, new Box(pos.add(-5, -7, -5), pos.add(6, 4, 6)), livingEntity -> true);
            for (LivingEntity entity : entities) {
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1, true, false, true), null);
            }
        }
    }
}
