package azzy.fabric.lookingglass.biome;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.entity.LookingGlassEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
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

    public static final Biome BRINE_DELTA = createBrineBiome(-0.05F, 0.13F, false, false);
    public static final Biome BRINE_OCEAN = createBrineBiome(-0.75F, 0.3F, true, false);
    public static final Biome BRINE_CRAGS = createBrineBiome(-0.2F, 0.25F, false, false);
    public static final Biome BRINE_CHASM = createBrineBiome(-1.9F, 0.9F, true, true);

    public static Biome createBrineBiome(float depth, float scale, boolean ocean, boolean deep) {
        SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addDesertMobs(spawnSettings);
        DefaultBiomeFeatures.addBatsAndMonsters(spawnSettings);

        GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();
        generationSettings.surfaceBuilder(BRINE_SURFACE_BUILDER);
        generationSettings.feature(GenerationStep.Feature.SURFACE_STRUCTURES, LookingGlassConfiguredFeatures.SALT_DELTA.getFeature());
        generationSettings.feature(GenerationStep.Feature.SURFACE_STRUCTURES, LookingGlassConfiguredFeatures.WHITESTONE_BOULDERS.getFeature());
        generationSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, deep ? LookingGlassConfiguredFeatures.DEEP_BRINE_FISSURES.getFeature()  : LookingGlassConfiguredFeatures.BRINE_FISSURES.getFeature());

        if(ocean) {
            generationSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, LookingGlassConfiguredFeatures.WHITEDUST_DISCS.getFeature());
            generationSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, LookingGlassConfiguredFeatures.CRACKED_CRUST.getFeature());
            generationSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, LookingGlassConfiguredFeatures.SALT_DISCS.getFeature());

            generationSettings.feature(GenerationStep.Feature.VEGETAL_DECORATION, LookingGlassConfiguredFeatures.RED_SEAGRASS.getFeature());

            spawnSettings.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(LookingGlassEntities.FLAREFIN_KOI_ENTITY_TYPE, 35, 5, 12));
            spawnSettings.spawn(SpawnGroup.UNDERGROUND_WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.AXOLOTL, 8, 1, 3));
            DefaultBiomeFeatures.addWarmOceanMobs(spawnSettings, 8, 3);
        }
        else {
            generationSettings.feature(GenerationStep.Feature.VEGETAL_DECORATION, LookingGlassConfiguredFeatures.SPARSE_RED_SEAGRASS.getFeature());
            spawnSettings.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(LookingGlassEntities.FLAREFIN_KOI_ENTITY_TYPE, 15, 3, 9));
            DefaultBiomeFeatures.addWarmOceanMobs(spawnSettings, 4, 1);
        }

        if(deep) {
            generationSettings.feature(GenerationStep.Feature.UNDERGROUND_ORES, LookingGlassConfiguredFeatures.VOLCANIC_CRUST.getFeature());
        }

        DefaultBiomeFeatures.addDefaultUndergroundStructures(generationSettings);
        DefaultBiomeFeatures.addOceanCarvers(generationSettings);
        DefaultBiomeFeatures.addOceanStructures(generationSettings);
        DefaultBiomeFeatures.addDungeons(generationSettings);
        DefaultBiomeFeatures.addMineables(generationSettings);
        DefaultBiomeFeatures.addDefaultOres(generationSettings);
        DefaultBiomeFeatures.addDefaultDisks(generationSettings);

        return new Biome.Builder()
                .precipitation(Biome.Precipitation.RAIN)
                .category(Biome.Category.OCEAN)
                .depth(depth)
                .scale(scale)
                .temperature(1.8F)
                .downfall(1.5F)
                .effects(new BiomeEffects.Builder()
                .waterColor(0x8ceae6)
                .waterFogColor(0x8ceae6)
                .fogColor(0xffebd4)
                .skyColor(0xc0e5f2)
                .build())
                .spawnSettings(spawnSettings.build())
                .generationSettings(generationSettings.build())
                .build();
    }
}
