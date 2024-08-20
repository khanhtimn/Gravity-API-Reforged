package fun.teamti.gravity.config;


import net.minecraftforge.common.ForgeConfigSpec;

public class GravityAPIConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue ROTATION_TIME;
    public static final ForgeConfigSpec.BooleanValue WORLD_VELOCITY;
    public static final ForgeConfigSpec.DoubleValue GRAVITY_STRENGTH_MULTIPLIER;
    public static final ForgeConfigSpec.BooleanValue RESET_GRAVITY_ON_RESPAWN;
    public static final ForgeConfigSpec.BooleanValue VOID_DAMAGE_ABOVE_WORLD;
    public static final ForgeConfigSpec.BooleanValue VOID_DAMAGE_ON_HORIZONTAL_FALL_TOO_FAR;
    public static final ForgeConfigSpec.BooleanValue AUTO_JUMP_ON_GRAVITY_PLATE_INNER_CORNER;
    public static final ForgeConfigSpec.BooleanValue ADJUST_POSITION_AFTER_CHANGING_GRAVITY;

    static {
        BUILDER.push("Global Config");
        ROTATION_TIME = BUILDER
                .comment("""
                        If enabled,
                        Default: 500""")
                .defineInRange("rotationTime", 500, 0, 10000);
        WORLD_VELOCITY = BUILDER
                .comment("""
                        If enabled,
                        Default: false""")
                .define("switch_first_person_scoping", false);
        GRAVITY_STRENGTH_MULTIPLIER = BUILDER
                .comment("""
                        Default: 1.0""")
                .defineInRange("gravityStrengthMultiplier", 1.0, 0.0, 10.0);
        RESET_GRAVITY_ON_RESPAWN = BUILDER
                .comment("""
                        If enabled,
                        Default: true""")
                .define("resetGravityOnRespawn", true);
        VOID_DAMAGE_ABOVE_WORLD = BUILDER
                .comment("""
                        If enabled,
                        Default: true""")
                .define("voidDamageAboveWorld", true);
        VOID_DAMAGE_ON_HORIZONTAL_FALL_TOO_FAR = BUILDER
                .comment("""
                        If enabled,
                        Default: true""")
                .define("voidDamageOnHorizontalFallTooFar", true);
        AUTO_JUMP_ON_GRAVITY_PLATE_INNER_CORNER = BUILDER
                .comment("""
                        If enabled,
                        Default: true""")
                .define("autoJumpOnGravityPlateInnerCorner", true);
        ADJUST_POSITION_AFTER_CHANGING_GRAVITY = BUILDER
                .comment("""
                        If enabled,
                        Default: true""")
                .define("adjustPositionAfterChangingGravity", true);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
