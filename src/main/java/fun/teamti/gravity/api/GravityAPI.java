package fun.teamti.gravity.api;

import fun.teamti.gravity.init.ModCapability;
import fun.teamti.gravity.capability.data.GravityData;
import fun.teamti.gravity.capability.dimension.DimensionGravityData;
import fun.teamti.gravity.init.ModTag;
import fun.teamti.gravity.util.RotationAnimation;
import fun.teamti.gravity.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GravityAPI {

    /**
     * Returns the applied gravity direction for the given entity
     */
    public static Direction getGravityDirection(Entity entity) {
        return entity.getCapability(ModCapability.GRAVITY_DATA).map(GravityData::getCurrGravityDirection).orElse(Direction.DOWN);
    }

    /**
     * Returns the gravity strength for the given entity
     */
    public static double getGravityStrength(Entity entity) {
        return entity.getCapability(ModCapability.GRAVITY_DATA).map(GravityData::getCurrGravityStrength).orElse(1.0);
    }

    /**
     * Returns the base gravity strength for the given entity
     */
    public static double getBaseGravityStrength(Entity entity) {
        return entity.getCapability(ModCapability.GRAVITY_DATA).map(GravityData::getBaseGravityStrength).orElse(1.0);
    }

    /**
     * Sets the base gravity strength for the given entity
     */
    public static void setBaseGravityStrength(Entity entity, double strength) {
        entity.getCapability(ModCapability.GRAVITY_DATA).ifPresent(data -> data.setBaseGravityStrength(strength));
    }

    /**
     * Returns the gravity strength for the given dimension
     */
    public static double getDimensionGravityStrength(Level level) {
        return level.getCapability(ModCapability.DIMENSION_GRAVITY_DATA).map(DimensionGravityData::getDimensionGravityStrength).orElse(1.0);
    }

    /**
     * Sets the gravity strength for the given dimension
     */
    public static void setDimensionGravityStrength(Level level, double strength) {
        level.getCapability(ModCapability.DIMENSION_GRAVITY_DATA).ifPresent(data -> data.setDimensionGravityStrength(strength));
    }

    /**
     * Resets the gravity (Direction.DOWN, Strength == 1.0F) for the given entity
     */
    public static void resetGravity(Entity entity) {
        if (!ModTag.canChangeGravity(entity)) {
            return;
        }
        entity.getCapability(ModCapability.GRAVITY_DATA).ifPresent(GravityData::reset);
    }

    /**
     * Returns the main gravity direction for the given entity. This may not be the applied gravity direction for the player.
     * For the applied gravity direction of the entity, {@link GravityAPI#getGravityDirection(Entity entity)}
     */
    public static Direction getBaseGravityDirection(Entity entity) {
        return entity.getCapability(ModCapability.GRAVITY_DATA).map(GravityData::getBaseGravityDirection).orElse(Direction.DOWN);
    }

    /**
     * Sets the base gravity direction for the given entity. This may not be the applied gravity direction for the player.
     */
    public static void setBaseGravityDirection(Entity entity, Direction gravityDirection) {
        entity.getCapability(ModCapability.GRAVITY_DATA).ifPresent(data -> data.setBaseGravityDirection(gravityDirection));
    }

    /**
     * Applies gravity direction effect for the given entity with a priority.
     */
    public static void applyGravityDirectionEffect(
            Entity entity,
            @NotNull Direction direction,
            @Nullable RotationParameters rotationParameters,
            double priority
    ) {
        entity.getCapability(ModCapability.GRAVITY_DATA).ifPresent(data -> {
                    data.applyGravityDirectionEffect(direction, rotationParameters, priority);
                    if (!entity.level().isClientSide()) data.setNeedsSync(true);
                }
        );
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public static RotationAnimation getRotationAnimation(Entity entity) {
        return entity.getCapability(ModCapability.GRAVITY_DATA).map(GravityData::getRotationAnimation).orElseGet(RotationAnimation::new);
    }

    /**
     * Instantly set gravity direction on client side without performing animation.
     * Not needed in normal cases.
     * (Used by iPortal)
     */
    public static void instantlySetClientBaseGravityDirection(Entity entity, Direction direction) {

        Validate.isTrue(entity.level().isClientSide(), "Should only be used on client");

        entity.getCapability(ModCapability.GRAVITY_DATA).ifPresent(data -> {
            data.setBaseGravityDirection(direction);
            data.updateGravityStatus(false);
            data.forceApplyGravityChange();
        });

    }

    /**
     * Returns the world relative velocity for the given entity
     * Using minecraft's methods to get the velocity will return entity local velocity
     */
    public static Vec3 getWorldVelocity(Entity entity) {
        return RotationUtil.vecPlayerToWorld(entity.getDeltaMovement(), getGravityDirection(entity));
    }

    /**
     * Sets the world relative velocity for the given entity
     * Using minecraft's methods to set the velocity of an entity will set player relative velocity
     */
    public static void setWorldVelocity(Entity entity, Vec3 worldVelocity) {
        entity.setDeltaMovement(RotationUtil.vecWorldToPlayer(worldVelocity, getGravityDirection(entity)));
    }

    /**
     * Returns eye position offset from feet position for the given entity
     */
    public static Vec3 getEyeOffset(Entity entity) {
        return RotationUtil.vecPlayerToWorld(0, (double) entity.getEyeHeight(), 0, getGravityDirection(entity));
    }

    /**
     * Returns whether an entity can change gravity
     */
    public static boolean canChangeGravity(Entity entity) {
        return ModTag.canChangeGravity(entity);
    }

}
