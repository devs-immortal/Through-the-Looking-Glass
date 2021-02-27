package azzy.fabric.lookingglass.effects;

import com.google.common.collect.Maps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Map;
import java.util.UUID;

public class CurseEffect extends StatusEffect {
    private final Map<EntityAttribute, EntityAttributeModifier> attributeModifiers = Maps.newHashMap();

    public CurseEffect(StatusEffectType type, int color) {
        super(type, color);
    }

    public StatusEffect addAttributeModifier(EntityAttribute attribute, String uuid, double amount, EntityAttributeModifier.Operation operation) {
        EntityAttributeModifier entityAttributeModifier = new EntityAttributeModifier(UUID.fromString(uuid), this::getTranslationKey, amount, operation);
        this.attributeModifiers.put(attribute, entityAttributeModifier);
        return this;
    }

    public Map<EntityAttribute, EntityAttributeModifier> getAttributeModifiers() {
        return this.attributeModifiers;
    }

    /**
     * Called when the potion effect is applied.
     */
    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        if(!(entity instanceof PlayerEntity)) {
            for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : this.attributeModifiers.entrySet()) {
                EntityAttributeInstance entityAttributeInstance = attributes.getCustomInstance(entry.getKey());
                if (entityAttributeInstance != null) {
                    EntityAttributeModifier entityAttributeModifier = entry.getValue();
                    entityAttributeInstance.removeModifier(entityAttributeModifier);
                    entityAttributeInstance.addPersistentModifier(new EntityAttributeModifier(entityAttributeModifier.getId(), this.getTranslationKey() + " " + amplifier, this.adjustModifierAmount(amplifier, entityAttributeModifier), entityAttributeModifier.getOperation()));
                }
            }
        }
    }

    /**
     * Called when the potion effect is removed.
     */
    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        // This method kills the entity.  First, it tries to remove them elegantly, without them dropping any items.
        // If that fails, it goes ahead and applies an OUT_OF_WORLD damage to kill them dead.
        if(!(entity instanceof PlayerEntity)) {
            entity.remove();
            entity.kill();
        }
        else {
            entity.damage(DamageSource.MAGIC, 5F);
        }
    }
}