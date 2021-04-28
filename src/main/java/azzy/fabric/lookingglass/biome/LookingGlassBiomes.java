package azzy.fabric.lookingglass.biome;

import azzy.fabric.lookingglass.LookingGlassCommon;
import net.fabricmc.fabric.api.biome.v1.OverworldBiomes;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

@SuppressWarnings("deprecation")
public class LookingGlassBiomes {

    public static final RegistryKey<Biome> BRINE_OCEAN_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier(LookingGlassCommon.MODID, "brine_ocean"));
    public static final RegistryKey<Biome> BRINE_DELTA_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier(LookingGlassCommon.MODID, "brine_delta"));
    public static final RegistryKey<Biome> BRINE_CRAGS_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier(LookingGlassCommon.MODID, "brine_crags"));
    public static final RegistryKey<Biome> BRINE_CHASM_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier(LookingGlassCommon.MODID, "brine_chasm"));


    public static void init() {
        Registry.register(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER, new Identifier(LookingGlassCommon.MODID, "brine_surface"), BrineOceanBiome.BRINE_SURFACE_BUILDER);
        Registry.register(BuiltinRegistries.BIOME, BRINE_OCEAN_KEY.getValue(), BrineOceanBiome.BRINE_OCEAN);
        Registry.register(BuiltinRegistries.BIOME, BRINE_DELTA_KEY.getValue(), BrineOceanBiome.BRINE_DELTA);
        Registry.register(BuiltinRegistries.BIOME, BRINE_CRAGS_KEY.getValue(), BrineOceanBiome.BRINE_CRAGS);
        Registry.register(BuiltinRegistries.BIOME, BRINE_CHASM_KEY.getValue(), BrineOceanBiome.BRINE_CHASM);

        OverworldBiomes.addBiomeVariant(BiomeKeys.OCEAN, BRINE_OCEAN_KEY, 0.2);
        OverworldBiomes.addBiomeVariant(BiomeKeys.DEEP_OCEAN, BRINE_CHASM_KEY, 0.15);
        OverworldBiomes.addEdgeBiome(BRINE_OCEAN_KEY, BRINE_DELTA_KEY, 3);
        OverworldBiomes.addHillsBiome(BRINE_OCEAN_KEY, BRINE_CRAGS_KEY, 0.2);
        OverworldBiomes.addHillsBiome(BRINE_OCEAN_KEY, BRINE_DELTA_KEY, 0.3);
        OverworldBiomes.addHillsBiome(BRINE_CHASM_KEY, BRINE_OCEAN_KEY, 0.06);
    }
}
