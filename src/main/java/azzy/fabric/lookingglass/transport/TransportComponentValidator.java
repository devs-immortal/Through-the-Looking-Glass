package azzy.fabric.lookingglass.transport;

import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import net.minecraft.block.Block;

import java.util.HashSet;

public class TransportComponentValidator {

    private static final HashSet<Object> POWER_CABLES = new HashSet<>();

    public static <T extends Block & EnergyIo> void registerPowerCable(T cable) {
        POWER_CABLES.add(cable);
    }

    public static boolean isPowerCable(Object object) {
        return POWER_CABLES.contains(object);
    }

}
