package azzy.fabric.lookingglass.biome;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class BrineOceanBiome {

    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> BRINE_SURFACE_BUILDER = SurfaceBuilder.DEFAULT
            .withConfig(new TernarySurfaceConfig(
                    LookingGlassBlocks.WHITEDUST.getDefaultState(),
                    LookingGlassBlocks.WHITESTONE_BLOCK.getDefaultState(),
                    LookingGlassBlocks.WHITESTONE_BLOCK.getDefaultState()
            )
    );

    public static final Biome BRINE_DELTA = createBrineBiome(-0.05F, 0.125F, false);
    public static final Biome BRINE_OCEAN = createBrineBiome(-0.8F, 0.25F, true);
    public static final Biome BRINE_CRAGS = createBrineBiome(-0.2F, 0.4F, false);

    public static Biome createBrineBiome(float depth, float scale, boolean dust) {
        SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addDesertMobs(spawnSettings);
        DefaultBiomeFeatures.addBatsAndMonsters(spawnSettings);

        GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();
        generationSettings.surfaceBuilder(BRINE_SURFACE_BUILDER);
        generationSettings.feature(GenerationStep.Feature.SURFACE_STRUCTURES, LookingGlassConfiguredFeatures.SALT_DELTA.getFeature());
        generationSettings.feature(GenerationStep.Feature.SURFACE_STRUCTURES, LookingGlassConfiguredFeatures.WHITESTONE_BOULDERS.getFeature());
        generationSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, LookingGlassConfiguredFeatures.BRINE_FISSURES.getFeature());

        DefaultBiomeFeatures.addDefaultUndergroundStructures(generationSettings);
        DefaultBiomeFeatures.addLandCarvers(generationSettings);
        DefaultBiomeFeatures.addDungeons(generationSettings);
        DefaultBiomeFeatures.addMineables(generationSettings);
        DefaultBiomeFeatures.addDefaultOres(generationSettings);
        DefaultBiomeFeatures.addDefaultDisks(generationSettings);
        DefaultBiomeFeatures.addFrozenTopLayer(generationSettings);

        return new Biome.Builder()
                .precipitation(Biome.Precipitation.NONE)
                .category(Biome.Category.OCEAN)
                .depth(depth)
                .scale(scale)
                .temperature(1.8F)
                .downfall(0.0F)
                .effects(new BiomeEffects.Builder()
                .waterColor(0xa9fffc)
                .waterFogColor(0x8ceae6)
                .fogColor(0xffebd4)
                .skyColor(0xc0e5f2)
                .build())
                .spawnSettings(spawnSettings.build())
                .generationSettings(generationSettings.build())
                .build();
    }
}
