package azzy.fabric.lookingglass.biome;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.trunk.LargeOakTrunkPlacer;

import java.util.function.Predicate;

@SuppressWarnings("deprecation")
public class LookingGlassConfiguredFeatures {

    public static Feature<SingleStateFeatureConfig> BOULDER_FEATURE, UNDERWATER_STATE_FEATURE;
    public static Feature<BiFeatureConfig> UNDERWATER_CRUST_FEATURE, UNDERWATER_PLANT_FEATURE;
    public static Feature<BasaltColumnsFeatureConfig> PILLAR_FEATURE;
    public static RegistryFeature<?, ?> NEBULOUS_SALT_FLATS;
    public static RegistryFeature<?, ?> SALT_DELTA, WHITESTONE_BOULDERS, BRINE_FISSURES, DEEP_BRINE_FISSURES;
    public static RegistryFeature<?, ?> WHITEDUST_DISCS, SALT_DISCS, CRACKED_CRUST, VOLCANIC_CRUST;
    public static RegistryFeature<?, ?> RED_SEAGRASS, SPARSE_RED_SEAGRASS, DENSE_RED_SEAGRASS;

    public static void init() {
        BOULDER_FEATURE = register("boulder", new BoulderFeature(SingleStateFeatureConfig.CODEC));
        UNDERWATER_STATE_FEATURE = register("underwater_state", new UnderwaterStateFeature(SingleStateFeatureConfig.CODEC));
        UNDERWATER_CRUST_FEATURE = register("underwater_crust", new UnderwaterCrustFeature(BiFeatureConfig.CODEC));
        PILLAR_FEATURE = register("pillars", new SaltPillarFeature(BasaltColumnsFeatureConfig.CODEC));
        UNDERWATER_PLANT_FEATURE = register("underwater_plant", new UnderwaterPlantFeature(BiFeatureConfig.CODEC));

        SALT_DELTA = register("salt_delta", Feature.DELTA_FEATURE.configure(new DeltaFeatureConfig(Blocks.WATER.getDefaultState(), LookingGlassBlocks.SALT_CLUSTER_BLOCK.getDefaultState(), UniformIntDistribution.of(2, 5), UniformIntDistribution.of(0, 2))).decorate(Decorator.COUNT_MULTILAYER.configure(new CountConfig(40))));
        BRINE_FISSURES = register("brine_fissures", UNDERWATER_STATE_FEATURE.configure(new SingleStateFeatureConfig(LookingGlassBlocks.BRINE_FISSURE.getDefaultState())).decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP).repeatRandomly(3));
        DEEP_BRINE_FISSURES = register("deep_brine_fissures", UNDERWATER_STATE_FEATURE.configure(new SingleStateFeatureConfig(LookingGlassBlocks.BRINE_FISSURE.getDefaultState())).decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP).repeatRandomly(6));

        WHITEDUST_DISCS = register("whitedust_discs", Feature.DISK.configure(new DiskFeatureConfig(LookingGlassBlocks.WHITEDUST.getDefaultState(), UniformIntDistribution.of(4, 2), 1, ImmutableList.of(LookingGlassBlocks.WHITESTONE_BLOCK.getDefaultState(), Blocks.STONE.getDefaultState()))).decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP));
        SALT_DISCS = register("salt_discs", Feature.DISK.configure(new DiskFeatureConfig(LookingGlassBlocks.SALT_CLUSTER_BLOCK.getDefaultState(), UniformIntDistribution.of(2, 1), 2, ImmutableList.of(LookingGlassBlocks.WHITESTONE_BLOCK.getDefaultState(), Blocks.STONE.getDefaultState()))).decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP));

        CRACKED_CRUST = register("cracked_crust", UNDERWATER_CRUST_FEATURE.configure(new BiFeatureConfig(LookingGlassBlocks.WHITESTONE_BLOCK.getDefaultState(), LookingGlassBlocks.WHITESTONE_CRACKED.getDefaultState(), 0.225F, UniformIntDistribution.of(5, 3))).applyChance(3).decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP));
        VOLCANIC_CRUST = register("volcanic_crust", UNDERWATER_CRUST_FEATURE.configure(new BiFeatureConfig(Blocks.BASALT.getDefaultState(), LookingGlassBlocks.HOT_BASALT.getDefaultState(), 0.195F, UniformIntDistribution.of(4, 2))).applyChance(2).decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP));

        SPARSE_RED_SEAGRASS = register("sparse_red_seagrass", UNDERWATER_PLANT_FEATURE.configure(new BiFeatureConfig(LookingGlassBlocks.RED_SEAGRASS.getDefaultState(), Blocks.SEAGRASS.getDefaultState(), 0.0025F, UniformIntDistribution.of(0))).repeat(10).decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP));
        RED_SEAGRASS = register("red_seagrass", UNDERWATER_PLANT_FEATURE.configure(new BiFeatureConfig(LookingGlassBlocks.RED_SEAGRASS.getDefaultState(), LookingGlassBlocks.TALL_RED_SEAGRASS.getDefaultState(), 0.1F, UniformIntDistribution.of(0))).repeat(16).decorate(Decorator.COUNT_MULTILAYER.configure(new CountConfig(4))));
        DENSE_RED_SEAGRASS = register("dense_red_seagrass", UNDERWATER_PLANT_FEATURE.configure(new BiFeatureConfig(LookingGlassBlocks.RED_SEAGRASS.getDefaultState(), LookingGlassBlocks.TALL_RED_SEAGRASS.getDefaultState(), 0.45F, UniformIntDistribution.of(0))).repeat(40).decorate(Decorator.COUNT_MULTILAYER.configure(new CountConfig(7))));

        NEBULOUS_SALT_FLATS = register("nebulous_salt_flats", Feature.RANDOM_SELECTOR.configure(Configs.END_SALT_FLATS_CONFIG).decorate(new ConfiguredDecorator<>(Decorator.CHANCE, new ChanceDecoratorConfig(50))).decorate(ConfiguredFeatures.Decorators.TOP_SOLID_HEIGHTMAP));
        WHITESTONE_BOULDERS = register("whitestone_boulders", BOULDER_FEATURE.configure(new SingleStateFeatureConfig(LookingGlassBlocks.WHITESTONE_BLOCK.getDefaultState())).decorate(ConfiguredFeatures.Decorators.HEIGHTMAP));
    }

    private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
        return Registry.register(Registry.FEATURE, new Identifier(LookingGlassCommon.MODID, name), feature);
    }

    private static <FC extends FeatureConfig> RegistryFeature<FC, ?> register(String id, ConfiguredFeature<FC, ?> configuredFeature) {
        Identifier registeredID = new Identifier(LookingGlassCommon.MODID, id);
        return new RegistryFeature<>(Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, registeredID, configuredFeature), RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, registeredID));
    }

    public static class Configs {
        public static final RandomPatchFeatureConfig END_SALTS_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(LookingGlassBlocks.NEBULOUS_SALTS.getDefaultState()), SimpleBlockPlacer.INSTANCE).blacklist(ImmutableSet.of(LookingGlassBlocks.NEBULOUS_SALTS.getDefaultState())).spreadZ(128).spreadX(128).spreadY(16).tries(5000).build();
        public static final TreeFeatureConfig END_HALITE_CRYSTAL_CONFIG = new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(Blocks.AIR.getDefaultState()), new SimpleBlockStateProvider(LookingGlassBlocks.NEBULOUS_HALITE.getDefaultState()), new BlobFoliagePlacer(UniformIntDistribution.of(0), UniformIntDistribution.of(0), 0), new LargeOakTrunkPlacer(5, 3, 7), new TwoLayersFeatureSize(3, 0, 0)).build();

        public static final RandomFeatureConfig END_SALT_FLATS_CONFIG = new RandomFeatureConfig(
                ImmutableList.of(Feature.TREE.configure(END_HALITE_CRYSTAL_CONFIG).withChance(0.1F)), Feature.RANDOM_PATCH.configure(END_SALTS_CONFIG)
        );
    }

    public static class Registrar {
        public static void init() {
            register(NEBULOUS_SALT_FLATS, BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.VEGETAL_DECORATION);
            register(WHITESTONE_BOULDERS, BiomeSelectors.categories(Biome.Category.BEACH), GenerationStep.Feature.SURFACE_STRUCTURES);
        }

        private static void register(RegistryFeature<?, ?> feature, Predicate<BiomeSelectionContext> selector, GenerationStep.Feature step) {
            BiomeModifications.addFeature(selector, step, feature.getRegistryKey());
        }
    }

    public static class RegistryFeature<T extends FeatureConfig, V extends Feature<T>> {
        private final ConfiguredFeature<T, V> feature;
        private final RegistryKey<ConfiguredFeature<?, ?>> registryKey;

        public RegistryFeature(ConfiguredFeature<T, V> feature, RegistryKey<ConfiguredFeature<?, ?>> key) {
            this.feature = feature;
            this.registryKey = key;
        }

        public ConfiguredFeature<T, V> getFeature() {
            return feature;
        }

        public RegistryKey<ConfiguredFeature<?, ?>> getRegistryKey() {
            return registryKey;
        }

        public Identifier getID() {
            return registryKey.getValue();
        }
    }
}
