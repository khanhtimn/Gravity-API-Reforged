package fun.teamti.gravity.api;

import fun.teamti.gravity.init.ModCapability;
import fun.teamti.gravity.capability.data.GravityData;
import fun.teamti.gravity.capability.dimension.DimensionGravityData;
import fun.teamti.gravity.EntityTags;
import fun.teamti.gravity.RotationAnimation;
import fun.teamti.gravity.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

public abstract class GravityAPI {

//    public static GravityData getGravityData(Entity entity) {
//        return entity.getCapability(ModCapability.GRAVITY_DATA).orElse(new GravityDataProvider(entity).getGravityData());
//    }
//
//    public static DimensionGravityData getDimensionGravityData(Level level) {
//        return level.getCapability(ModCapability.DIMENSION_GRAVITY_DATA).orElse(new DimensionGravityDataProvider(level).getDimensionGravityData());
//    }

    /**
     * Returns the applied gravity direction for the given entity
     */
    public static Direction getGravityDirection(Entity entity) {

//        GravityData gravityData = getGravityComponentEarly(entity);
//
//        if (gravityData == null) {
//            return Direction.DOWN;
//        }
//
//        return gravityData.getCurrGravityDirection();
        return entity.getCapability(ModCapability.GRAVITY_DATA).map(GravityData::getCurrGravityDirection).orElse(Direction.DOWN);
    }

    public static double getGravityStrength(Entity entity) {
        return entity.getCapability(ModCapability.GRAVITY_DATA).map(GravityData::getCurrGravityStrength).orElse(1.0);
        //return getGravityData(entity).getCurrGravityStrength();
    }

    public static double getBaseGravityStrength(Entity entity) {
        return entity.getCapability(ModCapability.GRAVITY_DATA).map(GravityData::getBaseGravityStrength).orElse(1.0);
        //return getGravityData(entity).getBaseGravityStrength();
    }

    public static void setBaseGravityStrength(Entity entity, double strength) {
        entity.getCapability(ModCapability.GRAVITY_DATA).ifPresent(data -> data.setBaseGravityStrength(strength));
        //getGravityData(entity).setBaseGravityStrength(strength);
    }

    public static double getDimensionGravityStrength(Level level) {
        return level.getCapability(ModCapability.DIMENSION_GRAVITY_DATA).map(DimensionGravityData::getDimensionGravityStrength).orElse(1.0);
        //return getDimensionGravityData(level).getDimensionGravityStrength();
    }

    public static void setDimensionGravityStrength(Level level, double strength) {
        level.getCapability(ModCapability.DIMENSION_GRAVITY_DATA).ifPresent(data -> data.setDimensionGravityStrength(strength));
        //getDimensionGravityData(level).setDimensionGravityStrength(strength);
    }

    public static void resetGravity(Entity entity) {
        if (!EntityTags.canChangeGravity(entity)) {
            return;
        }
        entity.getCapability(ModCapability.GRAVITY_DATA).ifPresent(GravityData::reset);
        //getGravityData(entity).reset();
    }

    /**
     * Returns the main gravity direction for the given entity
     * This may not be the applied gravity direction for the player, see GravityChangerAPI#getAppliedGravityDirection
     */
    public static Direction getBaseGravityDirection(Entity entity) {
        return entity.getCapability(ModCapability.GRAVITY_DATA).map(GravityData::getBaseGravityDirection).orElse(Direction.DOWN);
        //return getGravityData(entity).getBaseGravityDirection();
    }

    public static void setBaseGravityDirection(Entity entity, Direction gravityDirection) {
        entity.getCapability(ModCapability.GRAVITY_DATA).ifPresent(data -> data.setBaseGravityDirection(gravityDirection));
        //GravityData component = getGravityData(entity);
        //component.setBaseGravityDirection(gravityDirection);
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public static RotationAnimation getRotationAnimation(Entity entity) {
        return entity.getCapability(ModCapability.GRAVITY_DATA).map(GravityData::getRotationAnimation).orElseGet(RotationAnimation::new);
        //return getGravityData(entity).getRotationAnimation();
    }

    /**
     * Instantly set gravity direction on client side without performing animation.
     * Not needed in normal cases.
     * (Used by iPortal)
     */
    public static void instantlySetClientBaseGravityDirection(Entity entity, Direction direction) {
        Validate.isTrue(entity.level().isClientSide(), "should only be used on client");

        //GravityData component = getGravityData(entity);
//        component.setBaseGravityDirection(direction);
//
//        component.updateGravityStatus(false);
//
//        component.forceApplyGravityChange();

        entity.getCapability(ModCapability.GRAVITY_DATA).ifPresent(data -> {
            data.setBaseGravityDirection(direction);
            data.updateGravityStatus(false);
            data.forceApplyGravityChange();
        });

    }

    /**
     * cardinal components initializes the component container in the end of constructor
     * but bounding box calculation can happen inside constructor
     * see {@link dev.onyxstudios.cca.mixin.entity.common.MixinEntity}
     */
//    public static @Nullable GravityData getGravityComponentEarly(Entity entity) {
//        if (!entity.getCapability(ModCapability.GRAVITY_DATA).isPresent()) {
//            return null;
//        }
//        return getGravityData(entity);
//    }

    /**
     * Returns the world relative velocity for the given entity
     * Using minecraft's methods to get the velocity will return entity local velocity
     */
    public static Vec3 getWorldVelocity(Entity entity) {
        return RotationUtil.vecPlayerToWorld(entity.getDeltaMovement(), getGravityDirection(entity));
    }

    /**
     * Sets the world relative velocity for the given player
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

    public static boolean canChangeGravity(Entity entity) {
        return EntityTags.canChangeGravity(entity);
    }

}
