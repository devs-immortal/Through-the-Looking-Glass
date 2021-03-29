package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.util.ParticleUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class UnstableAltarEntity extends LookingGlassBE implements Tickable {
    public boolean isMultiBlockFormed = false;
    public List<BlockPos> multiBlockPositionsList = new ArrayList<>();
    public List<LookingGlassBE> multiBlockBlockEntitiesList = new ArrayList<>();

    public UnstableAltarEntity() {
        super(LookingGlassBlocks.UNSTABLE_ALTAR_ENTITY, 1);
    }

    public void tick() {
        // The multiblock isn't formed or the world is invalid.  There's nothing to do here.
        if (!isMultiBlockFormed || (world == null))
            return;

        ItemStack inventory = getStack(0);
        if (inventory.isEmpty()) {
            return;
        }

        if (!world.isClient) {
            // Shouldn't ever happen.  But weirder things have happened.  Here, I'm checking to see if the multiblock only ever needs the unstable altar
            // (I'm thinking about make the # of required display pedestals configurable.
            // This is just a fail-safe check.
            if (multiBlockBlockEntitiesList.size() < 2)
                return;

            for (int i = 1; i < multiBlockBlockEntitiesList.size(); i++) {
                // First entry in the lists is the unstable altar.  The remaining 4 are the display pedestals.
                DisplayPedestalEntity iteratedDisplayPedestalEntity = (DisplayPedestalEntity) multiBlockBlockEntitiesList.get(i);
                ParticleUtils.spawnItemParticles((ServerWorld) world, pos, multiBlockPositionsList.get(i), iteratedDisplayPedestalEntity.getStack(0));
            }
        }
    }
}
