package azzy.fabric.lookingglass;

import com.chocohead.mm.api.ClassTinkerers;
import com.chocohead.mm.api.EnumAdder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.util.Formatting;

public class LookingGlassEarlyRiser implements Runnable {
	@Override
	public void run() {
		MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
		EnumAdder enumAdder = ClassTinkerers.enumBuilder(
				mappingResolver.mapClassName("intermediary", "net.minecraft.class_1814"),
				"L" + mappingResolver.mapClassName("intermediary", "net.minecraft.class_124") + ";"
		);
		enumAdder.addEnum("NULL", () -> new Object[]{Formatting.DARK_PURPLE });
		enumAdder.build();
	}
}
