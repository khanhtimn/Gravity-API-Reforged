package fun.teamti.gravity.init;


import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue ROTATION_TIME;
    public static final ForgeConfigSpec.BooleanValue WORLD_VELOCITY;
    public static final ForgeConfigSpec.DoubleValue GRAVITY_STRENGTH_MULTIPLIER;
    public static final ForgeConfigSpec.BooleanValue RESET_GRAVITY_ON_RESPAWN;
    public static final ForgeConfigSpec.BooleanValue RESET_GRAVITY_ON_CHANGED_DIMENSION;
    public static final ForgeConfigSpec.BooleanValue SPAWNED_ENTITY_INHERIT_OWNER_GRAVITY;
    public static final ForgeConfigSpec.BooleanValue VOID_DAMAGE_ABOVE_WORLD;
    public static final ForgeConfigSpec.BooleanValue VOID_DAMAGE_ON_HORIZONTAL_FALL_TOO_FAR;
    public static final ForgeConfigSpec.BooleanValue AUTO_JUMP_ON_GRAVITY_PLATE_INNER_CORNER;
    public static final ForgeConfigSpec.BooleanValue ADJUST_POSITION_AFTER_CHANGING_GRAVITY;

    static {
        BUILDER.push("Global Config");
        ROTATION_TIME = BUILDER
                .comment("""
                        Camera Rotation Time
                        The time it takes for the camera to rotate
                        from one direction to the next
                        Default: 500""")
                .defineInRange("rotationTime", 500, 0, 10000);
        WORLD_VELOCITY = BUILDER
                .comment("""
                        World Relative Velocity Transfer
                        Makes it so when gravity changes velocity stays world-relative
                        lets you keep moving the same direction when gravity changes
                        Default: false""")
                .define("worldVelocity", false);
        GRAVITY_STRENGTH_MULTIPLIER = BUILDER
                .comment("""
                        World Default Gravity Strength
                        Default: 1.0""")
                .defineInRange("gravityStrengthMultiplier", 1.0, 0.0, 10.0);
        RESET_GRAVITY_ON_RESPAWN = BUILDER
                .comment("""
                        Reset Gravity On Respawn
                        Makes it so that your gravity is reset
                        every time you respawn
                        Default: true""")
                .define("resetGravityOnRespawn", true);
        RESET_GRAVITY_ON_CHANGED_DIMENSION = BUILDER
                .comment("""
                        Reset Gravity On Changing Dimension
                        Makes it so that your gravity is reset
                        every time you change dimensions
                        Default: false""")
                .define("resetGravityChangedDimension", false);
        SPAWNED_ENTITY_INHERIT_OWNER_GRAVITY = BUILDER
                .comment("""
                        Inherit Owner Gravity
                        Makes it so that entities spawned by players
                        inherit the player's gravity
                        Default: true""")
                .define("spawnedEntityInheritOwnerGravity", true);
        VOID_DAMAGE_ABOVE_WORLD = BUILDER
                .comment("""
                        Makes it so that you start taking void damage
                        if you are above y = max_world_height + 256
                        and the gravity direction is up
                        Default: true""")
                .define("voidDamageAboveWorld", true);
        VOID_DAMAGE_ON_HORIZONTAL_FALL_TOO_FAR = BUILDER
                .comment("""
                        Void Damage On Falling Far for Horizontal Gravity
                        If enabled, players will take void damage
                        when falling too far horizontally (> 1024 blocks)
                        Default: true""")
                .define("voidDamageOnHorizontalFallTooFar", true);
        AUTO_JUMP_ON_GRAVITY_PLATE_INNER_CORNER = BUILDER
                .comment("""
                        Auto Jump On Gravity Plate Inner Corner
                        Default: true""")
                .define("autoJumpOnGravityPlateInnerCorner", true);
        ADJUST_POSITION_AFTER_CHANGING_GRAVITY = BUILDER
                .comment("""
                        Adjust Position After Changing Gravity
                        Default: true""")
                .define("adjustPositionAfterChangingGravity", true);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
