package azzy.fabric.lookingglass.effects;

import azzy.fabric.lookingglass.LookingGlassCommon;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static azzy.fabric.lookingglass.LookingGlassConstants.CURSE_UUID;

@SuppressWarnings("unused")
public class LookingGlassEffects {
    public static StatusEffect CURSE_EFFECT;

    public static void init() {
        CURSE_EFFECT = registerCurseEffect("curse");
    }

    public static StatusEffect registerCurseEffect(String id) {
        StatusEffect curseEffect = new CurseEffect(StatusEffectType.NEUTRAL, 0x280d04);
        curseEffect = curseEffect.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, CURSE_UUID.toString(), 1.0D, EntityAttributeModifier.Operation.ADDITION);
        curseEffect = curseEffect.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, CURSE_UUID.toString(), 1.2D, EntityAttributeModifier.Operation.MULTIPLY_BASE);
        curseEffect = curseEffect.addAttributeModifier(EntityAttributes.GENERIC_MAX_HEALTH, CURSE_UUID.toString(), 1.5D, EntityAttributeModifier.Operation.MULTIPLY_BASE);

        return Registry.register(Registry.STATUS_EFFECT, new Identifier(LookingGlassCommon.MODID, id), curseEffect);
    }
}
