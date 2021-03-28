package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class DisplayPedestalEntity extends LookingGlassBE {
    public boolean isMultiBlockFormed = false;
    public List<BlockPos> multiBlockPositionsList = null;
    public List<LookingGlassBE> multiBlockBlockEntitiesList = null;

    public DisplayPedestalEntity() {
        super(LookingGlassBlocks.DISPLAY_PEDESTAL_ENTITY, 1);
    }
}
