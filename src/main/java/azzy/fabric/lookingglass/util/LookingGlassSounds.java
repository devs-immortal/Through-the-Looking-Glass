package azzy.fabric.lookingglass.util;

import azzy.fabric.lookingglass.LookingGlassCommon;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LookingGlassSounds {

    public static final SoundEvent FINIS_PLACE = register("finis_place");
    public static final SoundEvent FINIS_IMPACT = register("finis_impact");
    public static final SoundEvent FINIS_FALL = register("finis_fall");
    public static final SoundEvent FINIS_BREAK = register("finis_break");

    public static final BlockSoundGroup FINIS = new BlockSoundGroup(1.0F, 1.0F, FINIS_BREAK, FINIS_IMPACT, FINIS_PLACE, FINIS_IMPACT, FINIS_FALL);
    public static final BlockSoundGroup ELDENMETAL = new BlockSoundGroup(1.0F, 1.0F, SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, SoundEvents.BLOCK_SHROOMLIGHT_PLACE, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundEvents.BLOCK_RESPAWN_ANCHOR_AMBIENT, SoundEvents.BLOCK_BEACON_DEACTIVATE);

    private static SoundEvent register(String name) {
        Identifier id = new Identifier(LookingGlassCommon.MODID, name);
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
    }
}
