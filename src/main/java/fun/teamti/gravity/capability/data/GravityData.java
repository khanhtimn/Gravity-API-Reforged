package fun.teamti.gravity.capability.data;

import fun.teamti.gravity.event.GravityUpdateEvent;
import fun.teamti.gravity.GravityMod;
import fun.teamti.gravity.util.RotationAnimation;
import fun.teamti.gravity.init.ModConfig;
import fun.teamti.gravity.init.ModNetwork;
import fun.teamti.gravity.mixin.EntityAccessor;
import fun.teamti.gravity.api.GravityAPI;
import fun.teamti.gravity.api.RotationParameters;
import fun.teamti.gravity.network.GravityDataSyncPacket;
import fun.teamti.gravity.util.ClientUtil;
import fun.teamti.gravity.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

/**
 * The gravity is determined by the follows:
 * 1. base gravity
 * 2. gravity modifier, can override base gravity (determined from modifier events)
 * 3. gravity effects, can override modified gravity
 * The result of applying 1 and 2 is called modified gravity and is synced.
 * The result of 3 is current gravity and is not synced.
 * The gravity effect should be applied both on client and server, except for remote players.
 * (The client player's gravity attributes are separately computed.
 * Other client entities' are synced from server.)
 */
public class GravityData implements INBTSerializable<CompoundTag> {

    // Only used on client, not synchronized.
    @Nullable
    private final RotationAnimation animation;

    private final Entity entity;

    private boolean initialized = false;

    // not synchronized
    private Direction prevGravityDirection = Direction.DOWN;

    public double getPrevGravityStrength() {
        return prevGravityStrength;
    }

    private double prevGravityStrength = 1.0;

    // the base gravity direction
    Direction baseGravityDirection = Direction.DOWN;

    // the base gravity strength
    private double baseGravityStrength = 1.0;

    public @Nullable RotationParameters getCurrentRotationParameters() {
        return currentRotationParameters;
    }

    @Nullable
    RotationParameters currentRotationParameters = RotationParameters.getDefault();

    private Direction currGravityDirection = Direction.DOWN;

    public double getCurrentGravityStrength() {
        return currGravityStrength;
    }

    private double currGravityStrength = 1.0;
    private double currentEffectPriority = Double.MIN_VALUE;

    private boolean isFiringUpdateEvent = false;

    private @Nullable GravityDirEffect delayApplyDirEffect = null;
    private double delayApplyStrengthEffect = 1.0;

    // if it equals entity.tickCount,
    // it means that the gravity update event has already fired in this tick
    private long lastUpdateTickCount = 0;

    // only used on server side
    private boolean needsSync = false;

    public GravityData(Entity entity) {
        this.entity = entity;
        if (entity.level().isClientSide()) {
            animation = new RotationAnimation();
        } else {
            animation = null;
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("baseGravityDirection", baseGravityDirection.getName());
        tag.putString("currentGravityDirection", currGravityDirection.getName());
        tag.putDouble("baseGravityStrength", baseGravityStrength);
        tag.putDouble("currentGravityStrength", currGravityStrength);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains("baseGravityDirection")) {
            baseGravityDirection = Direction.byName(tag.getString("baseGravityDirection"));
        } else {
            baseGravityDirection = Direction.DOWN;
        }

        if (tag.contains("baseGravityStrength")) {
            baseGravityStrength = tag.getDouble("baseGravityStrength");
        } else {
            baseGravityStrength = 1.0;
        }

        // the current gravity is serialized to avoid unnecessary gravity rotation when entering world
        // do not deserialize it when for client player when not initializing
        if (!initialized || shouldAcceptServerSync()) {
            if (tag.contains("currentGravityDirection")) {
                currGravityDirection = Direction.byName(tag.getString("currentGravityDirection"));
            } else {
                currGravityDirection = Direction.DOWN;
            }

            if (tag.contains("currentGravityStrength")) {
                currGravityStrength = tag.getDouble("currentGravityStrength");
            } else {
                currGravityStrength = 1.0;
            }
        }

        if (!initialized) {
            prevGravityDirection = currGravityDirection;
            prevGravityStrength = currGravityStrength;
            initialized = true;
            applyGravityDirectionChange(
                    prevGravityDirection, currGravityDirection, currentRotationParameters, true
            );
        }
    }

    public boolean shouldAcceptServerSync() {
        return entity.level().isClientSide() && !ClientUtil.isClientPlayer(entity);
    }

    public void tick() {
        if (!GravityAPI.canChangeGravity(entity)) {
            return;
        }

        updateGravityStatus(true);

        applyGravityChange();

        if (!entity.level().isClientSide()) {
            if (needsSync) {
                needsSync = false;
                //TODO: Sync
                GravityDataSyncPacket.sendToClient(entity, serializeNBT(), ModNetwork.INSTANCE);
            }
        }
    }

    public void updateGravityStatus(boolean sendPacketIfNecessary) {
        // for the remote players and non-player entities,
        // their effect data is not synchronized to the client
        // (possibly for making it harder to cheat for hacked clients)
        // then we don't calculate its gravity in normal way in client
        if (shouldAcceptServerSync()) {
            return;
        }

        Direction oldGravityDirection = currGravityDirection;
        double oldGravityStrength = currGravityStrength;

        Entity vehicle = entity.getVehicle();
        if (vehicle != null) {
            currGravityDirection = GravityAPI.getGravityDirection(vehicle);
            currGravityStrength = GravityAPI.getGravityStrength(vehicle);
        } else {
            currGravityDirection = baseGravityDirection;
            currGravityStrength = baseGravityStrength;
            currGravityStrength *= GravityAPI.getDimensionGravityStrength(entity.level());
            currGravityStrength *= ModConfig.GRAVITY_STRENGTH_MULTIPLIER.get();
            // the rotation parameters is not being reset here
            // the rotation parameter is kept when an effect vanishes
            currentEffectPriority = Double.MIN_VALUE;

            isFiringUpdateEvent = true;
            try {
                GravityUpdateEvent event = new GravityUpdateEvent(entity, this);
                MinecraftForge.EVENT_BUS.post(event);

                if (delayApplyDirEffect != null) {
                    applyGravityDirectionEffect(
                            delayApplyDirEffect.direction(),
                            delayApplyDirEffect.rotationParameters(), delayApplyDirEffect.priority()
                    );
                    delayApplyDirEffect = null;
                }
                currGravityStrength *= delayApplyStrengthEffect;
                delayApplyStrengthEffect = 1.0;
            } finally {
                isFiringUpdateEvent = false;
            }

            if (currentEffectPriority == Double.MIN_VALUE) {
                // if no effect is applied, reset the rotation parameters
                currentRotationParameters = RotationParameters.getDefault();
            }

            lastUpdateTickCount = entity.tickCount;
        }

        if (sendPacketIfNecessary) {
            boolean changed = oldGravityDirection != currGravityDirection ||
                    Math.abs(oldGravityStrength - currGravityStrength) > 0.0001;
            if (changed) {
                sendSyncPacketToOtherPlayers();
            }
        }
    }

    //TODO: Send packet to other players
    private void sendSyncPacketToOtherPlayers() {
        //GravityChangerComponents.GRAVITY_COMP_KEY.sync(entity, this, p -> p != entity);
    }

    public void applyGravityDirectionEffect(
            @NotNull Direction direction,
            @Nullable RotationParameters rotationParameters,
            double priority
    ) {
        if (isFiringUpdateEvent) {
            if (priority > currentEffectPriority) {
                currentEffectPriority = priority;
                currGravityDirection = direction;

                if (rotationParameters != null) {
                    currentRotationParameters = rotationParameters;
                }
            }
        } else {
            // When not firing event, store it on delayApplyEffect.
            // The effect could come from another entity ticking,
            // but there is no guarantee for ticking order between entities.
            // (the ticking order does not change according to EntityTickList)
            if (delayApplyDirEffect == null || priority > delayApplyDirEffect.priority()) {
                delayApplyDirEffect = new GravityDirEffect(
                        direction, rotationParameters, priority
                );
            }
        }
    }

    public void applyGravityStrengthEffect(double strengthMultiplier) {
        if (isFiringUpdateEvent) {
            currGravityStrength *= strengthMultiplier;
        } else {
            delayApplyStrengthEffect *= strengthMultiplier;
        }
    }

    public void applyGravityDirectionChange(
            Direction oldGravity, Direction newGravity,
            RotationParameters rotationParameters, boolean isInitialization
    ) {
        if (!GravityAPI.canChangeGravity(entity)) {
            return;
        }

        // update bounding box
        entity.setBoundingBox(((EntityAccessor) entity).gc_makeBoundingBox());

        // A weird thing is that,
        // using `entity.setPos(entity.position())` to a painting on client side
        // make the painting move wrongly, because Painting overrides `trackingPosition()`.
        // No entity other than Painting overrides that method.
        // It seems to be legacy code from early versions of Minecraft.

        if (isInitialization) {
            return;
        }

        entity.fallDistance = 0;

        long timeMs = entity.level().getGameTime() * 50;

        Vec3 relativeRotationCenter = getLocalRotationCenter(
                entity, oldGravity, newGravity, rotationParameters
        );
        Vec3 oldPos = entity.position();
        Vec3 oldLastTickPos = new Vec3(entity.xOld, entity.yOld, entity.zOld);
        Vec3 rotationCenter = oldPos.add(RotationUtil.vecPlayerToWorld(relativeRotationCenter, oldGravity));
        Vec3 newPos = rotationCenter.subtract(RotationUtil.vecPlayerToWorld(relativeRotationCenter, newGravity));
        Vec3 posTranslation = newPos.subtract(oldPos);
        Vec3 newLastTickPos = oldLastTickPos.add(posTranslation);

        entity.setPos(newPos);
        entity.xo = newLastTickPos.x;
        entity.yo = newLastTickPos.y;
        entity.zo = newLastTickPos.z;
        entity.xOld = newLastTickPos.x;
        entity.yOld = newLastTickPos.y;
        entity.zOld = newLastTickPos.z;

        adjustEntityPosition(oldGravity, newGravity, entity.getBoundingBox());

        if (entity.level().isClientSide()) {
            Validate.notNull(animation, "gravity animation is null");

            int rotationTimeMS = rotationParameters.rotationTimeMS();

            animation.startRotationAnimation(
                    newGravity, oldGravity,
                    rotationTimeMS,
                    entity, timeMs, rotationParameters.rotateView(),
                    relativeRotationCenter
            );
        }

        Vec3 realWorldVelocity = getRealWorldVelocity(entity, oldGravity);
        if (rotationParameters.rotateVelocity()) {
            // Rotate velocity with gravity, this will cause things to appear to take a sharp turn
            Vector3f worldSpaceVec = realWorldVelocity.toVector3f();
            worldSpaceVec.rotate(RotationUtil.getRotationBetween(oldGravity, newGravity));
            entity.setDeltaMovement(RotationUtil.vecWorldToPlayer(new Vec3(worldSpaceVec), newGravity));
        } else {
            // Velocity will be conserved relative to the world, will result in more natural motion
            entity.setDeltaMovement(RotationUtil.vecWorldToPlayer(realWorldVelocity, newGravity));
        }
    }

    // getVelocity() does not return the actual velocity. It returns the velocity plus acceleration.
    // Even if the entity is standing still, getVelocity() will still give a downwards vector.
    // The real velocity is this tick position subtract last tick position
    private static Vec3 getRealWorldVelocity(Entity entity, Direction prevGravityDirection) {
        if (entity.isControlledByLocalInstance()) {
            return new Vec3(
                    entity.getX() - entity.xo,
                    entity.getY() - entity.yo,
                    entity.getZ() - entity.zo
            );
        }

        return RotationUtil.vecPlayerToWorld(entity.getDeltaMovement(), prevGravityDirection);
    }

    @NotNull
    private static Vec3 getLocalRotationCenter(
            Entity entity,
            Direction oldGravity, Direction newGravity, RotationParameters rotationParameters
    ) {
        if (entity instanceof EndCrystal) {
            //In the middle of the block below
            return new Vec3(0, -0.5, 0);
        }

        EntityDimensions dimensions = entity.getDimensions(entity.getPose());
        if (newGravity.getOpposite() == oldGravity) {
            // In the center of the hit-box
            return new Vec3(0, dimensions.height / 2, 0);
        } else {
            return Vec3.ZERO;
        }
    }

    private void adjustEntityPosition(Direction oldGravity, Direction newGravity, AABB entityBoundingBox) {
        if (!ModConfig.ADJUST_POSITION_AFTER_CHANGING_GRAVITY.get()) {
            return;
        }

        if (entity instanceof AreaEffectCloud || entity instanceof AbstractArrow || entity instanceof EndCrystal) {
            return;
        }

        // for example, if gravity changed from down to north, move up
        // if gravity changed from down to up, also move up
        Direction movingDirection = oldGravity.getOpposite();

        Iterable<VoxelShape> collisions = entity.level().getCollisions(
                entity,
                entityBoundingBox.inflate(-0.01) // shrink to avoid floating point error
        );
        AABB totalCollisionBox = null;
        for (VoxelShape collision : collisions) {
            if (!collision.isEmpty()) {
                AABB boundingBox = collision.bounds();
                if (totalCollisionBox == null) {
                    totalCollisionBox = boundingBox;
                } else {
                    totalCollisionBox = totalCollisionBox.minmax(boundingBox);
                }
            }
        }

        if (totalCollisionBox != null) {
            Vec3 positionAdjustmentOffset = getPositionAdjustmentOffset(
                    entityBoundingBox, totalCollisionBox, movingDirection
            );
            if (entity instanceof Player) {
                GravityMod.LOGGER.info("Adjusting player position {} {}", positionAdjustmentOffset, entity);
            }
            entity.setPos(entity.position().add(positionAdjustmentOffset));
        }
    }

    private static Vec3 getPositionAdjustmentOffset(
            AABB entityBoundingBox, AABB nearbyCollisionUnion, Direction movingDirection
    ) {
        Direction.Axis axis = movingDirection.getAxis();
        double offset = 0;
        if (movingDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            double pushing = nearbyCollisionUnion.max(axis);
            double pushed = entityBoundingBox.min(axis);
            if (pushing > pushed) {
                offset = pushing - pushed;
            }
        } else {
            double pushing = nearbyCollisionUnion.min(axis);
            double pushed = entityBoundingBox.max(axis);
            if (pushing < pushed) {
                offset = pushed - pushing;
            }
        }

        return new Vec3(movingDirection.step()).scale(offset);
    }

    public double getBaseGravityStrength() {
        return baseGravityStrength;
    }

    public void setBaseGravityStrength(double strength) {
        if (!GravityAPI.canChangeGravity(entity)) {
            return;
        }
        baseGravityStrength = strength;
        needsSync = true;
    }

    public Direction getCurrGravityDirection() {
        return currGravityDirection;
    }

    public double getCurrGravityStrength() {
        return currGravityStrength;
    }

    public Direction getPrevGravityDirection() {
        return prevGravityDirection;
    }

    public Direction getBaseGravityDirection() {
        return baseGravityDirection;
    }

    public void setBaseGravityDirection(Direction gravityDirection) {
        if (!GravityAPI.canChangeGravity(entity)) {
            return;
        }

        if (baseGravityDirection != gravityDirection) {
            baseGravityDirection = gravityDirection;
            needsSync = true;

            // update gravity immediately
            // avoid having wrong info from getGravityDirection()
            updateGravityStatus(false); // will this cause issue?
        }
    }


    @OnlyIn(Dist.CLIENT)
    public RotationAnimation getRotationAnimation() {
        return animation;
    }

    public void applyGravityChange() {
        if (currentRotationParameters == null) {
            currentRotationParameters = RotationParameters.getDefault();
        }

        if (prevGravityDirection != currGravityDirection) {
            applyGravityDirectionChange(
                    prevGravityDirection, currGravityDirection,
                    currentRotationParameters, false
            );
            prevGravityDirection = currGravityDirection;
        }

        if (Math.abs(currGravityStrength - prevGravityStrength) > 0.0001) {
            prevGravityStrength = currGravityStrength;
        }
    }

    /**
     * Not needed in normal cases.
     * Only used in {@link GravityAPI#instantlySetClientBaseGravityDirection(Entity, Direction)}
     * Used by ImmPtl.
     */
    public void forceApplyGravityChange() {
        prevGravityDirection = currGravityDirection;
        prevGravityStrength = currGravityStrength;
    }

    public void reset() {
        baseGravityDirection = Direction.DOWN;
        baseGravityStrength = 1.0;
        needsSync = true;
    }

    private record GravityDirEffect(
            @NotNull Direction direction,
            @Nullable RotationParameters rotationParameters,
            double priority
    ) {}

}
