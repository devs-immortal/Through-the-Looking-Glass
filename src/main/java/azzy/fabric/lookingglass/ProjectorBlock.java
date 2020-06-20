package azzy.fabric.lookingglass;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import static azzy.fabric.lookingglass.LookingGlass.MODID;

public class ProjectorBlock extends Block implements BlockEntityProvider {
    public ProjectorBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(!world.isClient) {
            ContainerProviderRegistry.INSTANCE.openContainer(new Identifier(MODID, "projector_gui"), player, (packetByteBuf -> packetByteBuf.writeBlockPos(pos)));
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean hasBlockEntity() {
        return true;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView){
        return new ProjectorEntity();
    }
}
