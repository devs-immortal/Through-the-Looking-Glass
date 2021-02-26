package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.effects.LookingGlassEffects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@SuppressWarnings({"deprecated"})
public class CursedEarthBlock extends LookingGlassBlock {
    public static final UUID CURSE_UUID = UUID.fromString("A223344A-B55B-C66C-D77D-E8888888888E");

    public CursedEarthBlock(FabricBlockSettings settings) {
        super(settings, false);
    }

    /**
     * Called on random growth ticks.  Default growth tick value is 3.  To test, increase the growth tick to, say, 1000 using /gamerule randomTickSpeed 1000
     *
     * @param state  Block State
     * @param world  Serverside world
     * @param pos    Block Position
     * @param random Random Object
     */
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!world.isClient)
            doTick(world, pos, random);
    }

    /**
     * Called to render the block on the client side.
     *
     * @param state  Block State
     * @param world  Client side world
     * @param pos    Block position
     * @param random Random Object
     */
    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        // Spawn some particles
        for (int i = 0; i < 5; i++) {
            world.addParticle(ParticleTypes.SMOKE, pos.getX() + random.nextDouble(), pos.getY() + 1.01, pos.getZ() + random.nextDouble(), 0, 0, 0);
        }
    }

    /**
     * Properties of cursed earth
     * If cursed earth is on fire, it'll spread the fire to nearby cursed earth blocks
     * If it's on fire, it will also burn itself out by turning into sand, gravel or dirt
     * If it's in the dark, it will spread itself at a very slow rate to nearby dirt, grass or path blocks
     * If it's in the dark, it will spawn mobs that are more powerful than usual, but also die within a set timer.
     *
     * @param world  The world object
     * @param pos    The position of the cursed earth block
     * @param random A Random object
     */
    protected void doTick(ServerWorld world, BlockPos pos, Random random) {
        int light = world.getLightLevel(pos.up());

        // Light level 14 is torch.
        // Light level 15 is fire, lava, etc.
        if (light >= 14) {
            // For every nearby block, if we're on fire, spread it to nearby blocks.
            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    for (int z = -1; z < 2; z++) {
                        BlockState blockState = world.getBlockState(pos.up());
                        boolean onFire = (blockState.getMaterial() == Material.FIRE);

                        BlockPos tmpPos = pos.add(x, y, z);
                        // If the nearby block is a cursed earth block
                        if (onFire && (world.getBlockState(tmpPos).getBlock() == LookingGlassBlocks.CURSED_EARTH_BLOCK)) {
                            if (random.nextInt(5) == 0) {
                                // 1 in 5 chance to spread fire to this neighbor.
                                world.setBlockState(tmpPos.up(), Blocks.FIRE.getDefaultState());
                            }
                        }
                        // If the nearby block is on fire
                        else if (world.getBlockState(tmpPos.up()).getMaterial() == Material.FIRE) {
                            if (random.nextInt(5) == 0) {
                                // 1 in 5 chance to catch self fire.
                                world.setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
                            }
                        }
                    }
                }
            }
        } else if (light <= 7) {
            // It's dark.  Spread the curse to nearby blocks.
            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    for (int z = -1; z < 2; z++) {
                        BlockPos tmpPos = pos.add(x, y, z);
                        Block neighbor = world.getBlockState(tmpPos).getBlock();
                        if ((neighbor == Blocks.DIRT) || (neighbor == Blocks.GRASS_BLOCK) || (neighbor == Blocks.GRASS_PATH) || (neighbor == Blocks.COARSE_DIRT)) {
                            if (random.nextInt(10) == 0) {
                                // 1 in 10 chance to turn neighbor into cursed earth.
                                world.setBlockState(tmpPos, LookingGlassBlocks.CURSED_EARTH_BLOCK.getDefaultState());
                            }
                        }
                    }
                }
            }
        }

        BlockState blockState = world.getBlockState(pos.up());
        boolean onFire = (blockState.getMaterial() == Material.FIRE);

        if (onFire) {
            // If the block catches on fire, it turns to dirt, gravel or sand.
            turnCursedEarthToSlag(world, pos, random);
        }

        // TODO 4:  Spawn random mobs as required when it's dark.
        if (light <= 7) {
            Box boundingBox = new Box(pos);
            boundingBox = boundingBox.expand(7, 7, 7);
            List<Entity> entitiesAround = world.getOtherEntities(null, boundingBox, Entity::isLiving);

            if (entitiesAround.size() > 8) {
                // Too many entities nearby.  Do nothing.
                return;
            }

            spawnMobs(world, pos, random);
        }
    }

    /**
     * This method spawns the cursed mobs and adds the curse effect to them.
     * The effect makes the mobs stronger, faster, healthier... and die in 60 seconds :)
     *
     * @param world  The server world instance
     * @param pos    The position of the cursed earth
     * @param random The random object
     */
    private void spawnMobs(ServerWorld world, BlockPos pos, Random random) {
        Biome biome = world.getBiome(pos);
        SpawnSettings spawnSettings = biome.getSpawnSettings();
        List<SpawnSettings.SpawnEntry> list = spawnSettings.getSpawnEntry(SpawnGroup.MONSTER);

        if (!list.isEmpty()) {
            SpawnSettings.SpawnEntry spawnEntry = WeightedPicker.getRandom(random, list);

            try {
                if (spawnEntry.type.isSummonable() && SpawnHelper.canSpawn(SpawnRestriction.getLocation(spawnEntry.type), world, pos.up(), spawnEntry.type)) {
                    LivingEntity spawnedEntity = (LivingEntity) spawnEntry.type.spawn(world, null, null, null, pos, SpawnReason.COMMAND, true, false);
                    if (spawnedEntity == null) {
                        throw new Exception("Unable to spawn mob at position (x, y, z): (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ").");
                    }
                    // Set the duration at which the entity will die.
                    spawnedEntity.addStatusEffect(new StatusEffectInstance(LookingGlassEffects.CURSE_EFFECT, 1200));
                }
            } catch (Exception spawnException) {
                LookingGlassCommon.FFLog.debug("Error spawning new mob.", spawnException);
            }
        }
    }

    private void turnCursedEarthToSlag(ServerWorld world, BlockPos pos, Random random) {
        int replaceFlag = random.nextInt(10);

        switch (replaceFlag) {
            case 0:
                // Replace to dirt.
                world.setBlockState(pos, Blocks.DIRT.getDefaultState());
                return;
            case 1:
                // Replace to gravel.
                world.setBlockState(pos, Blocks.GRAVEL.getDefaultState());
                return;
            case 2:
                // Replace to sand.
                world.setBlockState(pos, Blocks.SAND.getDefaultState());
        }
    }
}