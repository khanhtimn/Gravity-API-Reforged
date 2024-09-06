package fun.teamti.gravity.init;

import fun.teamti.gravity.GravityMod;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;

public class ModPotion {

    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTIONS, GravityMod.MOD_ID);

    public static final EnumMap<Direction, Potion> DIRECTION_POTION_MAP = new EnumMap<>(Direction.class);

    static {
        for (Direction direction : Direction.values()) {
            Potion potion = new Potion(
                    new MobEffectInstance(
                            ModEffect.DIRECTION_EFFECT_MAP.get(direction), 9600, 0
                    )
            );
            DIRECTION_POTION_MAP.put(direction, potion);
        }
    }

    public static final RegistryObject<Potion> DOWN_POTION = POTIONS.register("gravity_down_0",
            () -> DIRECTION_POTION_MAP.get(Direction.DOWN)
    );

    public static final RegistryObject<Potion> UP_POTION = POTIONS.register("gravity_up_0",
            () -> DIRECTION_POTION_MAP.get(Direction.UP)
    );

    public static final RegistryObject<Potion> NORTH_POTION = POTIONS.register("gravity_north_0",
            () -> DIRECTION_POTION_MAP.get(Direction.NORTH)
    );

    public static final RegistryObject<Potion> SOUTH_POTION = POTIONS.register("gravity_south_0",
            () -> DIRECTION_POTION_MAP.get(Direction.SOUTH)
    );

    public static final RegistryObject<Potion> WEST_POTION = POTIONS.register("gravity_west_0",
            () -> DIRECTION_POTION_MAP.get(Direction.WEST)
    );

    public static final RegistryObject<Potion> EAST_POTION = POTIONS.register("gravity_east_0",
            () -> DIRECTION_POTION_MAP.get(Direction.EAST)
    );

    public static final RegistryObject<Potion> STRENGTH_DECR_POTION_0 = POTIONS.register("gravity_decr_0",
            () -> new Potion(
                    new MobEffectInstance(
                            ModEffect.STRENGTH_DECREASE.get(), 9600, 0
                    )
            )
    );

    public static final RegistryObject<Potion> STRENGTH_DECR_POTION_1 = POTIONS.register("gravity_decr_1",
            () -> new Potion(
                    new MobEffectInstance(
                            ModEffect.STRENGTH_DECREASE.get(), 9600, 1
                    )
            )
    );

    public static final RegistryObject<Potion> STRENGTH_INCR_POTION_0 = POTIONS.register("gravity_incr_0",
            () -> new Potion(
                    new MobEffectInstance(
                            ModEffect.STRENGTH_INCREASE.get(), 9600, 0
                    )
            )
    );

    public static final RegistryObject<Potion> STRENGTH_INCR_POTION_1 = POTIONS.register("gravity_incr_1",
            () -> new Potion(
                    new MobEffectInstance(
                            ModEffect.STRENGTH_INCREASE.get(), 9600, 1
                    )
            )
    );

    public static final RegistryObject<Potion> STRENGTH_REVERSE_POTION_0 = POTIONS.register("gravity_reverse_0",
            () -> new Potion(
                    new MobEffectInstance(
                            ModEffect.STRENGTH_REVERSE.get(), 9600, 1
                    )
            )
    );

    public static final RegistryObject<Potion> STRENGTH_REVERSE_POTION_1 = POTIONS.register("gravity_reverse_1",
            () -> new Potion(
                    new MobEffectInstance(
                            ModEffect.STRENGTH_REVERSE.get(), 9600, 1
                    )
            )
    );
}
