package fun.teamti.gravity.init;

import fun.teamti.gravity.GravityMod;
import fun.teamti.gravity.effect.GravityDirectionMobEffect;
import fun.teamti.gravity.effect.GravityStrengthMobEffect;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;

public class ModEffect {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, GravityMod.MOD_ID);

    public static final EnumMap<Direction, GravityDirectionMobEffect> DIRECTION_EFFECT_MAP =
            new EnumMap<>(Direction.class);

    static {
        for (Direction dir : Direction.values()) {
            GravityDirectionMobEffect effect = new GravityDirectionMobEffect(dir);
            DIRECTION_EFFECT_MAP.put(dir, effect);
        }
    }

    public static RegistryObject<MobEffect> DOWN = MOB_EFFECTS.register("down",
            () -> DIRECTION_EFFECT_MAP.get(Direction.DOWN)
    );

    public static RegistryObject<MobEffect> UP = MOB_EFFECTS.register("up",
            () -> DIRECTION_EFFECT_MAP.get(Direction.UP)
    );

    public static RegistryObject<MobEffect> NORTH = MOB_EFFECTS.register("north",
            () -> DIRECTION_EFFECT_MAP.get(Direction.NORTH)
    );

    public static RegistryObject<MobEffect> SOUTH = MOB_EFFECTS.register("south",
            () -> DIRECTION_EFFECT_MAP.get(Direction.SOUTH)
    );

    public static RegistryObject<MobEffect> WEST = MOB_EFFECTS.register("west",
            () -> DIRECTION_EFFECT_MAP.get(Direction.WEST)
    );

    public static RegistryObject<MobEffect> EAST = MOB_EFFECTS.register("east",
            () -> DIRECTION_EFFECT_MAP.get(Direction.EAST)
    );

    public static RegistryObject<MobEffect> STRENGTH_INCREASE = MOB_EFFECTS.register("strength_increase",
            () -> new GravityStrengthMobEffect(0x98D982, 1.2, 1)
    );

    public static RegistryObject<MobEffect> STRENGTH_DECREASE = MOB_EFFECTS.register("strength_decrease",
            () -> new GravityStrengthMobEffect(0x98D982, 0.7, 1)
    );

    // it turns gravity into levitation but does not change player orientation
    public static RegistryObject<MobEffect> STRENGTH_REVERSE = MOB_EFFECTS.register("strength_reverse",
            () -> new GravityStrengthMobEffect(0x98D982, 1.0, -1)
    );
}
