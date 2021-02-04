package azzy.fabric.lookingglass.item;

import dev.technici4n.fasttransferlib.api.energy.EnergyApi;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergyProbeItem extends Item {

    public EnergyProbeItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        EnergyIo io = EnergyApi.SIDED.get(world, context.getBlockPos(), context.getSide());
        if(io != null) {
                if(!world.isClient())
                    context.getPlayer().sendMessage(new LiteralText(io.getEnergy() + "Lu"), true);
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(context);
    }
}
