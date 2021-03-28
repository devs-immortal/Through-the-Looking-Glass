package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class UnstableAltarEntity extends LookingGlassBE {
    public boolean isMultiBlockFormed = false;
    public List<BlockPos> multiBlockPositionsList = new ArrayList<>();
    public List<LookingGlassBE> multiBlockBlockEntitiesList = new ArrayList<>();

    public UnstableAltarEntity() {
        super(LookingGlassBlocks.UNSTABLE_ALTAR_ENTITY, 1);
    }
}
