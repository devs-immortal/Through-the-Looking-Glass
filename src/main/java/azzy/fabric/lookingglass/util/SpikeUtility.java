package azzy.fabric.lookingglass.util;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.effects.FalsePlayerDamageSource;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.lang.ref.WeakReference;

public class SpikeUtility {
    private static WeakReference<ServerPlayerEntity> fakePlayer = null;
    private static ServerPlayerEntity fakePlayerEntity = null;

    public static void damageEntity(Entity entity, int damage, World world, int spikeType) {
        // Don't hurt players.  Don't hurt items.
        if ((entity instanceof PlayerEntity) || !(entity instanceof LivingEntity) || (world == null) || (world.getServer() == null))
            return;

        switch (spikeType) {
            case 1:
                // Wooden spikes cause magic damage, but only until the monster gets to 1 health (half heart)
                float health = ((LivingEntity) entity).getHealth();
                if (health > damage)
                    entity.damage(DamageSource.MAGIC, damage);
                else
                    // If the entity has less than "damage" health, just set it to 1.  Shouldn't happen since wooden spikes only do 1 damage right now.
                    entity.damage(DamageSource.MAGIC, (health - 1));
                break;
            case 2:
                // Iron spikes cause magic damage - 4 for now.
                entity.damage(DamageSource.MAGIC, damage);
                break;
            case 3:
            case 4:
                // Diamond spikes and above cause player damage.
                if ((fakePlayer == null) || (fakePlayerEntity == null)) {
                    fakePlayer = new WeakReference<>(new ServerPlayerEntity(world.getServer(), (ServerWorld) world, new GameProfile(null, "TTLG_Spike_Player"), new ServerPlayerInteractionManager((ServerWorld) world)));
                    fakePlayerEntity = fakePlayer.get();

                    if (fakePlayerEntity == null) {
                        LookingGlassCommon.FFLog.warn("Error instantiating fake player for diamond spikes.  Defaulting to magic damage.  Kindly report to mod author(s).");
                        entity.damage(DamageSource.MAGIC, damage);
                        return;
                    }

                    fakePlayerEntity.setInvulnerable(true);
                    fakePlayerEntity.setBoundingBox(new Box(0, 0, 0, 0, 0, 0));
                }

                entity.damage(new FalsePlayerDamageSource("spikes", fakePlayerEntity, true, false, false), damage);
                break;
        }
    }
}