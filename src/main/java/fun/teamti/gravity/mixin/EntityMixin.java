package fun.teamti.gravity.mixin;

import com.fusionflux.gravity_api.GravityChangerMod;

import fun.teamti.gravity.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.CompatMath;
import com.fusionflux.gravity_api.util.RotationUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;


import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    private Vec3 position;

    @Shadow
    private EntityDimensions dimensions;

    @Shadow
    private float eyeHeight;

    @Shadow
    public double xo;

    @Shadow
    public double yo;

    @Shadow
    public double zo;

    @Shadow
    public abstract double getX();

    @Shadow
    public abstract Vec3 getEyePosition();

    @Shadow
    public abstract double getY();

    @Shadow
    public abstract double getZ();

    @Shadow
    private Level level;

    @Shadow
    public abstract int getBlockX();

    @Shadow
    public abstract int getBlockZ();

    @Shadow
    public boolean noPhysics;

    @Shadow
    public abstract Vec3 getDeltaMovement();

    @Shadow
    public abstract boolean isVehicle();

    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    public abstract Vec3 position();


    @Shadow
    public abstract boolean isPassengerOfSameVehicle(Entity entity);

    @Shadow
    public abstract void push(double deltaX, double deltaY, double deltaZ);

    @Shadow
    protected abstract void onBelowWorld();

    @Shadow
    public abstract double getEyeY();

    @Shadow
    public abstract float getXRot();

    @Shadow
    private static Vec3 collideWithShapes(Vec3 movement, AABB entityBoundingBox, List<VoxelShape> shapes) {
        return null;
    }

    @Shadow
    @Final
    protected RandomSource random;

    @Inject(
            method = "makeBoundingBox",
            at = @At("RETURN"),
            cancellable = true
    )
    private void inject_calculateBoundingBox(CallbackInfoReturnable<AABB> cir) {
        Entity entity = ((Entity) (Object) this);
        if (entity instanceof Projectile) return;

        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;

        AABB box = cir.getReturnValue().move(this.position.reverse());
        if (gravityDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            box = box.move(0.0D, -1.0E-6D, 0.0D);
        }
        cir.setReturnValue(RotationUtil.boxPlayerToWorld(box, gravityDirection).move(this.position));
    }

    @Inject(
            method = "getBoundingBoxForPose",
            at = @At("RETURN"),
            cancellable = true
    )
    private void inject_calculateBoundsForPose(Pose position, CallbackInfoReturnable<AABB> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;

        AABB box = cir.getReturnValue().move(this.position.reverse());
        if (gravityDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            box = box.move(0.0D, -1.0E-6D, 0.0D);
        }
        cir.setReturnValue(RotationUtil.boxPlayerToWorld(box, gravityDirection).move(this.position));
    }

    @Inject(
            method = "calculateViewVector",
            at = @At("RETURN"),
            cancellable = true
    )
    private void inject_getRotationVector(CallbackInfoReturnable<Vec3> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;

        cir.setReturnValue(RotationUtil.vecPlayerToWorld(cir.getReturnValue(), gravityDirection));
    }

    @Inject(
            method = "getBlockPosBelowThatAffectsMyMovement",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_getVelocityAffectingPos(CallbackInfoReturnable<BlockPos> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;

        cir.setReturnValue(CompatMath.fastBlockPos(this.position.add(Vec3.atLowerCornerOf(gravityDirection.getNormal()).scale(0.5000001D))));
    }

    @Inject(
            method = "getEyePosition()Lnet/minecraft/world/phys/Vec3;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_getEyePos(CallbackInfoReturnable<Vec3> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;

        cir.setReturnValue(RotationUtil.vecPlayerToWorld(0.0D, this.eyeHeight, 0.0D, gravityDirection).add(this.position));
    }

    @Inject(
            method = "getEyePosition(F)Lnet/minecraft/world/phys/Vec3;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_getCameraPosVec(float tickDelta, CallbackInfoReturnable<Vec3> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;

        Vec3 Vec3 = RotationUtil.vecPlayerToWorld(0.0D, this.eyeHeight, 0.0D, gravityDirection);

        double d = Mth.lerp((double) tickDelta, this.xo, this.getX()) + Vec3.x;
        double e = Mth.lerp((double) tickDelta, this.yo, this.getY()) + Vec3.y;
        double f = Mth.lerp((double) tickDelta, this.zo, this.getZ()) + Vec3.z;
        cir.setReturnValue(new Vec3(d, e, f));
    }

    @Inject(
            method = "getLightLevelDependentMagicValue",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_getBrightnessAtFEyes(CallbackInfoReturnable<Float> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;


        cir.setReturnValue(this.level.hasChunkAt(this.getBlockX(), this.getBlockZ()) ? this.level.getBrightness(CompatMath.fastBlockPos(this.getEyePosition())) : 0.0F);
    }

    @ModifyVariable(
            method = "move",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true)
    private Vec3 modify_move_Vec3_0_0(Vec3 Vec3) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return Vec3;
        }

        return RotationUtil.vecPlayerToWorld(Vec3, gravityDirection);
    }


    @ModifyArg(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/Vec3;multiply(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
                    ordinal = 0
            ),
            index = 0
    )
    private Vec3 modify_move_multiply_0(Vec3 Vec3) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return Vec3;
        }

        return RotationUtil.maskPlayerToWorld(Vec3, gravityDirection);
    }

    @ModifyVariable(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V",
                    ordinal = 0
            ),
            ordinal = 0,
            argsOnly = true)
    private Vec3 modify_move_Vec3_0_1(Vec3 Vec3) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return Vec3;
        }

        return RotationUtil.vecWorldToPlayer(Vec3, gravityDirection);
    }

    @ModifyVariable(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V",
                    ordinal = 0
            ),
            ordinal = 1
    )
    private Vec3 modify_move_Vec3_1(Vec3 Vec3) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return Vec3;
        }

        return RotationUtil.vecWorldToPlayer(Vec3, gravityDirection);
    }

    @Inject(
            method = "getOnPosLegacy",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_getLandingPos(CallbackInfoReturnable<BlockPos> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;
        BlockPos blockPos = CompatMath.fastBlockPos(RotationUtil.vecPlayerToWorld(0.0D, -0.20000000298023224D, 0.0D, gravityDirection).add(this.position()));
        cir.setReturnValue(blockPos);
    }

    @ModifyVariable(
            method = "collide",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/level/Level;getEntityCollisions(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;",
                    ordinal = 0
            ),
            ordinal = 0,
            argsOnly = true)
    private Vec3 modify_adjustMovementForCollisions_Vec3_0(Vec3 Vec3) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return Vec3;
        }

        return RotationUtil.vecWorldToPlayer(Vec3, gravityDirection);
    }

    @Inject(
            method = "collide",
            at = @At("RETURN"),
            cancellable = true
    )
    private void inject_adjustMovementForCollisions(CallbackInfoReturnable<Vec3> cir) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;

        cir.setReturnValue(RotationUtil.vecPlayerToWorld(cir.getReturnValue(), gravityDirection));
    }

    @ModifyArgs(
            method = "collide",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/AABB;expandTowards(DDD)Lnet/minecraft/world/phys/AABB;"
            )
    )
    private void redirect_adjustMovementForCollisions_stretch_0(Args args) {
        Vec3 rotate = new Vec3(args.get(0), args.get(1), args.get(2));
        rotate = RotationUtil.vecPlayerToWorld(rotate, GravityChangerAPI.getGravityDirection((Entity) (Object) this));
        args.set(0, rotate.x);
        args.set(1, rotate.y);
        args.set(2, rotate.z);
    }

    @ModifyArgs(
            method = "collide",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/AABB;move(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/AABB;",
                    ordinal = 0
            )
    )
    private void redirect_adjustMovementForCollisions_offset_0(Args args) {
        Vec3 rotate = args.get(0);
        rotate = RotationUtil.vecPlayerToWorld(rotate, GravityChangerAPI.getGravityDirection((Entity) (Object) this));
        args.set(0, rotate);
    }

    @ModifyArgs(
            method = "collide",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/AABB;move(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/AABB;",
                    ordinal = 1
            )
    )
    private void redirect_adjustMovementForCollisions_offset_1(Args args) {
        Vec3 rotate = args.get(0);
        rotate = RotationUtil.vecPlayerToWorld(rotate, GravityChangerAPI.getGravityDirection((Entity) (Object) this));
        args.set(0, rotate);
    }

    @ModifyVariable(
            method = "collideBoundingBox",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true)
    private static Vec3 modify_adjustMovementForCollisions_Vec3_0(Vec3 Vec3, Entity entity) {
        if (entity == null) {
            return Vec3;
        }

        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return Vec3;
        }

        return RotationUtil.vecPlayerToWorld(Vec3, gravityDirection);
    }

    @Inject(
            method = "collideBoundingBox",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void inject_adjustMovementForCollisions(Entity entity, Vec3 movement, AABB entityBoundingBox, Level level, List<VoxelShape> collisions, CallbackInfoReturnable<Vec3> cir) {
        if (entity == null) return;

        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) return;

        cir.setReturnValue(RotationUtil.vecWorldToPlayer(cir.getReturnValue(), gravityDirection));
    }

    @Redirect(
            method = "collideBoundingBox",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;collideWithShapes(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/List;)Lnet/minecraft/world/phys/Vec3;",
                    ordinal = 0
            )
    )
    private static Vec3 redirect_adjustMovementForCollisions_adjustMovementForCollisions_0(Vec3 movement, AABB entityBoundingBox, List<VoxelShape> collisions, Entity entity) {
        Direction gravityDirection;
        if (entity == null || (gravityDirection = GravityChangerAPI.getGravityDirection(entity)) == Direction.DOWN) {
            return collideWithShapes(movement, entityBoundingBox, collisions);
        }

        Vec3 playerMovement = RotationUtil.vecWorldToPlayer(movement, gravityDirection);
        double playerMovementX = playerMovement.x;
        double playerMovementY = playerMovement.y;
        double playerMovementZ = playerMovement.z;
        Direction directionX = RotationUtil.dirPlayerToWorld(Direction.EAST, gravityDirection);
        Direction directionY = RotationUtil.dirPlayerToWorld(Direction.UP, gravityDirection);
        Direction directionZ = RotationUtil.dirPlayerToWorld(Direction.SOUTH, gravityDirection);
        if (playerMovementY != 0.0D) {
            playerMovementY = Shapes.collide(directionY.getAxis(), entityBoundingBox, collisions, playerMovementY * directionY.getAxisDirection().getStep()) * directionY.getAxisDirection().getStep();
            if (playerMovementY != 0.0D) {
                entityBoundingBox = entityBoundingBox.move(RotationUtil.vecPlayerToWorld(0.0D, playerMovementY, 0.0D, gravityDirection));
            }
        }

        boolean isZLargerThanX = Math.abs(playerMovementX) < Math.abs(playerMovementZ);
        if (isZLargerThanX && playerMovementZ != 0.0D) {
            playerMovementZ = Shapes.collide(directionZ.getAxis(), entityBoundingBox, collisions, playerMovementZ * directionZ.getAxisDirection().getStep()) * directionZ.getAxisDirection().getStep();
            if (playerMovementZ != 0.0D) {
                entityBoundingBox = entityBoundingBox.move(RotationUtil.vecPlayerToWorld(0.0D, 0.0D, playerMovementZ, gravityDirection));
            }
        }

        if (playerMovementX != 0.0D) {
            playerMovementX = Shapes.collide(directionX.getAxis(), entityBoundingBox, collisions, playerMovementX * directionX.getAxisDirection().getStep()) * directionX.getAxisDirection().getStep();
            if (!isZLargerThanX && playerMovementX != 0.0D) {
                entityBoundingBox = entityBoundingBox.move(RotationUtil.vecPlayerToWorld(playerMovementX, 0.0D, 0.0D, gravityDirection));
            }
        }

        if (!isZLargerThanX && playerMovementZ != 0.0D) {
            playerMovementZ = Shapes.collide(directionZ.getAxis(), entityBoundingBox, collisions, playerMovementZ * directionZ.getAxisDirection().getStep()) * directionZ.getAxisDirection().getStep();
        }

        return RotationUtil.vecPlayerToWorld(playerMovementX, playerMovementY, playerMovementZ, gravityDirection);
    }

    //@Inject(
    //        method = "adjustMovementForCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3;Lnet/minecraft/util/math/Box;Lnet/minecraft/world/World;Ljava/util/List;)Lnet/minecraft/util/math/Vec3;",
    //        at = @At(
    //                value = "INVOKE",
    //                target = "Lcom/google/common/collect/ImmutableList$Builder;addAll(Ljava/lang/Iterable;)Lcom/google/common/collect/ImmutableList$Builder;",
    //                ordinal = 1,
    //                shift = At.Shift.AFTER,
    //                remap = false
    //        ),
    //        locals = LocalCapture.CAPTURE_FAILHARD,
    //        cancellable = true)
    //private static void redirect_adjustMovementForCollisions_adjustMovementForCollisions_0(Entity entity, Vec3 movement, Box entityBoundingBox, World world, List<VoxelShape> collisions, CallbackInfoReturnable<Vec3> cir, ImmutableList.Builder<VoxelShape> builder) {
    //    Direction gravityDirection;
    //    List<VoxelShape> collisionsProper = builder.build();
    //    if(entity == null || (gravityDirection = GravityChangerAPI.getGravityDirection(entity)) == Direction.DOWN) {
    //        return;
    //    }
//
    //    Vec3 playerMovement = RotationUtil.vecWorldToPlayer(movement, gravityDirection);
    //    double playerMovementX = playerMovement.x;
    //    double playerMovementY = playerMovement.y;
    //    double playerMovementZ = playerMovement.z;
    //    Direction directionX = RotationUtil.dirPlayerToWorld(Direction.EAST, gravityDirection);
    //    Direction directionY = RotationUtil.dirPlayerToWorld(Direction.UP, gravityDirection);
    //    Direction directionZ = RotationUtil.dirPlayerToWorld(Direction.SOUTH, gravityDirection);
    //    if (playerMovementY != 0.0D) {
    //        playerMovementY = VoxelShapes.calculateMaxOffset(directionY.getAxis(), entityBoundingBox, collisionsProper, playerMovementY * directionY.getAxisDirection().move()) * directionY.getAxisDirection().move();
    //        if (playerMovementY != 0.0D) {
    //            entityBoundingBox = entityBoundingBox.move(RotationUtil.vecPlayerToWorld(0.0D, playerMovementY, 0.0D, gravityDirection));
    //        }
    //    }
//
    //    boolean isZLargerThanX = Math.abs(playerMovementX) < Math.abs(playerMovementZ);
    //    if (isZLargerThanX && playerMovementZ != 0.0D) {
    //        playerMovementZ = VoxelShapes.calculateMaxOffset(directionZ.getAxis(), entityBoundingBox, collisionsProper, playerMovementZ * directionZ.getAxisDirection().move()) * directionZ.getAxisDirection().move();
    //        if (playerMovementZ != 0.0D) {
    //            entityBoundingBox = entityBoundingBox.move(RotationUtil.vecPlayerToWorld(0.0D, 0.0D, playerMovementZ, gravityDirection));
    //        }
    //    }
//
    //    if (playerMovementX != 0.0D) {
    //        playerMovementX = VoxelShapes.calculateMaxOffset(directionX.getAxis(), entityBoundingBox, collisionsProper, playerMovementX * directionX.getAxisDirection().move()) * directionX.getAxisDirection().move();
    //        if (!isZLargerThanX && playerMovementX != 0.0D) {
    //            entityBoundingBox = entityBoundingBox.move(RotationUtil.vecPlayerToWorld(playerMovementX, 0.0D, 0.0D, gravityDirection));
    //        }
    //    }
//
    //    if (!isZLargerThanX && playerMovementZ != 0.0D) {
    //        playerMovementZ = VoxelShapes.calculateMaxOffset(directionZ.getAxis(), entityBoundingBox, collisionsProper, playerMovementZ * directionZ.getAxisDirection().move()) * directionZ.getAxisDirection().move();
    //    }
    //    cir.setReturnValue(RotationUtil.vecPlayerToWorld(playerMovementX, playerMovementY, playerMovementZ, gravityDirection));
    //}

    @ModifyArgs(
            method = "isInWall",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/AABB;ofSize(Lnet/minecraft/world/phys/Vec3;DDD)Lnet/minecraft/world/phys/AABB;",
                    ordinal = 0
            )
    )
    private void modify_isInsideWall_of_0(Args args) {
        Vec3 rotate = new Vec3(args.get(1), args.get(2), args.get(3));
        rotate = RotationUtil.vecPlayerToWorld(rotate, GravityChangerAPI.getGravityDirection((Entity) (Object) this));
        args.set(1, rotate.x);
        args.set(2, rotate.y);
        args.set(3, rotate.z);
    }

    @ModifyArg(
            method = "getHorizontalFacing",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;fromRotation(D)Lnet/minecraft/util/math/Direction;"
            )
    )
    private double redirect_getHorizontalFacing_getYaw_0(double rotation) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return rotation;
        }

        return RotationUtil.rotPlayerToWorld((float) rotation, this.getXRot(), gravityDirection).x;
    }

    @Inject(
            method = "spawnSprintingParticles",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_spawnSprintingParticles(CallbackInfo ci) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) return;

        ci.cancel();

        Vec3 floorPos = this.position().subtract(RotationUtil.vecPlayerToWorld(0.0D, 0.20000000298023224D, 0.0D, gravityDirection));

        BlockPos blockPos = CompatMath.fastBlockPos(floorPos);
        BlockState blockState = this.level.getBlockState(blockPos);
        if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
            Vec3 particlePos = this.position().add(RotationUtil.vecPlayerToWorld((this.random.nextDouble() - 0.5D) * (double) this.dimensions.width, 0.1D, (this.random.nextDouble() - 0.5D) * (double) this.dimensions.width, gravityDirection));
            Vec3 playerVelocity = this.getDeltaMovement();
            Vec3 particleVelocity = RotationUtil.vecPlayerToWorld(playerVelocity.x * -4.0D, 1.5D, playerVelocity.z * -4.0D, gravityDirection);
            this.level.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), particlePos.x, particlePos.y, particlePos.z, particleVelocity.x, particleVelocity.y, particleVelocity.z);
        }
    }

    @ModifyVariable(
            method = "updateMovementInFluid",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/entity/Entity;getVelocity()Lnet/minecraft/util/math/Vec3;",
                    ordinal = 0
            ),
            ordinal = 1
    )
    private Vec3 modify_updateMovementInFluid_Vec3_0(Vec3 Vec3) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return Vec3;
        }

        return RotationUtil.vecPlayerToWorld(Vec3, gravityDirection);
    }

    @ModifyArg(
            method = "updateMovementInFluid",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Vec3;add(Lnet/minecraft/util/math/Vec3;)Lnet/minecraft/util/math/Vec3;",
                    ordinal = 1
            ),
            index = 0
    )
    private Vec3 modify_updateMovementInFluid_add_0(Vec3 Vec3) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return Vec3;
        }

        return RotationUtil.vecWorldToPlayer(Vec3, gravityDirection);
    }


    @Inject(
            method = "pushAwayFrom",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_pushAwayFrom(Entity entity, CallbackInfo ci) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity) (Object) this);
        Direction otherGravityDirection = GravityChangerAPI.getGravityDirection(entity);

        if (gravityDirection == Direction.DOWN && otherGravityDirection == Direction.DOWN) return;

        ci.cancel();

        if (!this.isPassengerOfSameVehicle(entity)) {
            if (!entity.noPhysics && !this.noPhysics) {
                Vec3 entityOffset = entity.getBoundingBox().getCenter().subtract(this.getBoundingBox().getCenter());

                {
                    Vec3 playerEntityOffset = RotationUtil.vecWorldToPlayer(entityOffset, gravityDirection);
                    double dx = playerEntityOffset.x;
                    double dz = playerEntityOffset.z;
                    double f = Mth.absMax(dx, dz);
                    if (f >= 0.009999999776482582D) {
                        f = Math.sqrt(f);
                        dx /= f;
                        dz /= f;
                        double g = 1.0D / f;
                        if (g > 1.0D) {
                            g = 1.0D;
                        }

                        dx *= g;
                        dz *= g;
                        dx *= 0.05000000074505806D;
                        dz *= 0.05000000074505806D;
                        if (!this.isVehicle()) {
                            this.push(-dx, 0.0D, -dz);
                        }
                    }
                }

                {
                    Vec3 entityEntityOffset = RotationUtil.vecWorldToPlayer(entityOffset, otherGravityDirection);
                    double dx = entityEntityOffset.x;
                    double dz = entityEntityOffset.z;
                    double f = Mth.absMax(dx, dz);
                    if (f >= 0.009999999776482582D) {
                        f = Math.sqrt(f);
                        dx /= f;
                        dz /= f;
                        double g = 1.0D / f;
                        if (g > 1.0D) {
                            g = 1.0D;
                        }

                        dx *= g;
                        dz *= g;
                        dx *= 0.05000000074505806D;
                        dz *= 0.05000000074505806D;
                        if (!entity.hasPassengers()) {
                            entity.addVelocity(dx, 0.0D, dz);
                        }
                    }
                }
            }
        }
    }

    @Inject(
            method = "tryTickInVoid",
            at = @At("HEAD")
    )
    private void inject_attemptTickInVoid(CallbackInfo ci) {
        if (GravityChangerMod.config.voidDamageAboveWorld && this.getY() > (double) (this.level.getTopY() + 256)) {
            this.onBelowWorld();
        }
    }

    @ModifyArgs(
            method = "doesNotCollide(DDD)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;move(DDD)Lnet/minecraft/util/math/Box;",
                    ordinal = 0
            )
    )
    private void redirect_doesNotCollide_offset_0(Args args) {
        Vec3 rotate = new Vec3(args.get(0), args.get(1), args.get(2));
        rotate = RotationUtil.vecPlayerToWorld(rotate, GravityChangerAPI.getGravityDirection((Entity) (Object) this));
        args.set(0, rotate.x);
        args.set(1, rotate.y);
        args.set(2, rotate.z);
    }


    @ModifyVariable(
            method = "updateSubmergedInWaterState",
            at = @At(
                    value = "STORE"
            ),
            ordinal = 0
    )
    private double submergedInWaterEyeFix(double d) {
        d = this.getEyePosition().getY();
        return d;
    }

    @ModifyVariable(
            method = "updateSubmergedInWaterState",
            at = @At(
                    value = "STORE"
            ),
            ordinal = 0
    )
    private BlockPos submergedInWaterPosFix(BlockPos blockpos) {
        blockpos = CompatMath.fastBlockPos(this.getEyePosition());
        return blockpos;
    }


}
