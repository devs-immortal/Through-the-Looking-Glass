package azzy.fabric.lookingglass.mixin;

import azzy.fabric.lookingglass.effects.FalsePlayerDamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityLootMixin extends Entity {

    public LivingEntityLootMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = {"dropLoot"}, at = {@At("HEAD")}, cancellable = true)
    protected void dropLoot(DamageSource source, boolean causedByPlayer, CallbackInfo ci) {
        if(source instanceof FalsePlayerDamageSource) {
            Identifier identifier = this.getLootTable();
            LootTable lootTable = this.world.getServer().getLootManager().getTable(identifier);
            LootContext.Builder builder = getLootContextBuilder(true, source);
            lootTable.generateLoot(builder.build(LootContextTypes.ENTITY), this::dropStack);
            ci.cancel();
        }
    }

    @Shadow protected abstract LootContext.Builder getLootContextBuilder(boolean causedByPlayer, DamageSource source);

    @Shadow public abstract Identifier getLootTable();
}
