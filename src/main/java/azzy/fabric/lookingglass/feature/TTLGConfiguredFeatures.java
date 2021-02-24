package azzy.fabric.lookingglass.feature;

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

public class TTLGConfiguredFeatures {

    public static Feature<SingleStateFeatureConfig> BOULDER_FEATURE;
    public static RegistryFeature<?, ?> NEBULOUS_SALT_FLATS, WHITESTONE_BOULDERS;

    public static void init() {
        BOULDER_FEATURE = register("boulder", new BoulderFeature(SingleStateFeatureConfig.CODEC));

        NEBULOUS_SALT_FLATS = register("nebulous_salt_flats", Feature.RANDOM_SELECTOR.configure(Configs.END_SALT_FLATS_CONFIG).decorate(new ConfiguredDecorator<>(Decorator.CHANCE, new ChanceDecoratorConfig(50))).decorate(ConfiguredFeatures.Decorators.TOP_SOLID_HEIGHTMAP));
        WHITESTONE_BOULDERS = register("whitestone_boulders", BOULDER_FEATURE.configure(new SingleStateFeatureConfig(LookingGlassBlocks.WHITESTONE_BLOCK.getDefaultState())).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).repeatRandomly(1));
    }

    private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
        return Registry.register(Registry.FEATURE, new Identifier(LookingGlassCommon.MODID, name), feature);
    }

    private static <FC extends FeatureConfig> RegistryFeature<FC, ?> register(String id, ConfiguredFeature<FC, ?> configuredFeature) {
        Identifier registeredID = new Identifier(LookingGlassCommon.MODID, id);
        return new RegistryFeature(Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, registeredID, configuredFeature), RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, registeredID));
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

    private static class RegistryFeature<T extends FeatureConfig, V extends Feature<T>> {
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
