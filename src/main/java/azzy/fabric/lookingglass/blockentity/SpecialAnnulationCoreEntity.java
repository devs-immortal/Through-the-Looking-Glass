package azzy.fabric.lookingglass.blockentity;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;

public class SpecialAnnulationCoreEntity extends BlockEntity {

    private ServerPlayerEntity player;

    public SpecialAnnulationCoreEntity() {
        super();
        if(!world.isClient()) {
            player = new ServerPlayerEntity(world.getServer(), world, new GameProfile(null, "" + this.hashCode()), new ServerPlayerInteractionManager((ServerWorld) world));
        }
    }

    public void attack() {
    }
}
