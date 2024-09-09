package fun.teamti.gravity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fun.teamti.gravity.api.GravityAPI;
import fun.teamti.gravity.util.RotationUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Player.class, priority = 1001)
public abstract class PlayerMixin extends LivingEntity {
    @Shadow
    @Final
    private Abilities abilities;

    @Shadow
    public abstract EntityDimensions getDimensions(Pose pose);

    @Shadow
    protected abstract boolean isStayingOnGroundSurface();

    @Shadow
    protected abstract boolean isAboveGround();

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @WrapOperation(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getLookAngle()Lnet/minecraft/world/phys/Vec3;"
            )
    )
    private Vec3 wrapOperation_travel_getRotationVector_0(Player playerEntity, Operation<Vec3> original) {
        Direction gravityDirection = GravityAPI.getGravityDirection(playerEntity);
        if (gravityDirection == Direction.DOWN) {
            return original.call(playerEntity);
        }

        return RotationUtil.vecWorldToPlayer(original.call(playerEntity), gravityDirection);
    }

    @WrapOperation(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"
            )
    )
    private BlockPos modify_move_multiply_0(double pX, double pY, double pZ, Operation<BlockPos> original) {
        Direction gravityDirection = GravityAPI.getGravityDirection(this);
        if(gravityDirection == Direction.DOWN) {
            return original.call(pX, pY, pZ);
        }

        Vec3 vec3d = RotationUtil.vecPlayerToWorld(0.0D, 1.0D - 0.1D, 0.0D, gravityDirection);
        return original.call( pX - vec3d.x, pY - vec3d.y + (1.0D - 0.1D), pZ - vec3d.z);
    }


    //@Redirect(
    //        method = "travel",
    //        at = @At(
    //                value = "NEW",
    //                target = "Lnet/minecraft/util/math/BlockPos;<init>(DDD)V",
    //                ordinal = 0
    //        )
    //)
    //private BlockPos redirect_travel_new_0(double x, double y, double z) {
    //    Direction gravityDirection = GravityChangerAPI.getGravityDirection((Entity)(Object)this);
    //    if(gravityDirection == Direction.DOWN) {
    //        return new BlockPos(x, y, z);
    //    }
//
    //    return new BlockPos(this.getPos().add(RotationUtil.vecPlayerToWorld(0.0D, 1.0D - 0.1D, 0.0D, gravityDirection)));
    //}

    @Redirect(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;",
                    ordinal = 0
            )
    )
    private ItemEntity redirect_dropItem_new_0(Level world, double x, double y, double z, ItemStack stack) {
        Direction gravityDirection = GravityAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return new ItemEntity(world, x, y, z, stack);
        }

        Vec3 vec3d = this.getEyePosition().subtract(RotationUtil.vecPlayerToWorld(0.0D, 0.3D, 0.0D, gravityDirection));

        return new ItemEntity(world, vec3d.x, vec3d.y, vec3d.z, stack);
    }

    @WrapOperation(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V"
            )
    )
    private void wrapOperation_dropItem_setVelocity(ItemEntity itemEntity, double x, double y, double z, Operation<Void> original) {
        Direction gravityDirection = GravityAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            original.call(itemEntity, x, y, z);
            return;
        }

        Vec3 world = RotationUtil.vecPlayerToWorld(x, y, z, gravityDirection);
        GravityAPI.setWorldVelocity(itemEntity, world);
    }

    @Inject(
            method = "maybeBackOffFromEdge(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/MoverType;)Lnet/minecraft/world/phys/Vec3;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void inject_adjustMovementForSneaking(Vec3 movement, MoverType type, CallbackInfoReturnable<Vec3> cir) {
        Entity this_ = (Entity) (Object) this;
        Direction gravityDirection = GravityAPI.getGravityDirection(this_);
        if (gravityDirection == Direction.DOWN) return;

        Vec3 playerMovement = RotationUtil.vecWorldToPlayer(movement, gravityDirection);

        if (!this.abilities.flying && (type == MoverType.SELF || type == MoverType.PLAYER) && this.isStayingOnGroundSurface() && this.isAboveGround()) {
            double d = playerMovement.x;
            double e = playerMovement.z;
            double var7 = 0.05D;

            while (d != 0.0D && this_.level().noCollision(this, this.getBoundingBox().move(RotationUtil.vecPlayerToWorld(d, (double) (-this.maxUpStep()), 0.0D, gravityDirection)))) {
                if (d < 0.05D && d >= -0.05D) {
                    d = 0.0D;
                } else if (d > 0.0D) {
                    d -= 0.05D;
                } else {
                    d += 0.05D;
                }
            }

            while (e != 0.0D && this_.level().noCollision(this, this.getBoundingBox().move(RotationUtil.vecPlayerToWorld(0.0D, (double) (-this.maxUpStep()), e, gravityDirection)))) {
                if (e < 0.05D && e >= -0.05D) {
                    e = 0.0D;
                } else if (e > 0.0D) {
                    e -= 0.05D;
                } else {
                    e += 0.05D;
                }
            }

            while (d != 0.0D && e != 0.0D && this_.level().noCollision(this, this.getBoundingBox().move(RotationUtil.vecPlayerToWorld(d, (double) (-this.maxUpStep()), e, gravityDirection)))) {
                if (d < 0.05D && d >= -0.05D) {
                    d = 0.0D;
                } else if (d > 0.0D) {
                    d -= 0.05D;
                } else {
                    d += 0.05D;
                }

                if (e < 0.05D && e >= -0.05D) {
                    e = 0.0D;
                } else if (e > 0.0D) {
                    e -= 0.05D;
                } else {
                    e += 0.05D;
                }
            }

            cir.setReturnValue(RotationUtil.vecPlayerToWorld(d, playerMovement.y, e, gravityDirection));
        } else {
            cir.setReturnValue(movement);
        }
    }

    @WrapOperation(
            method = "isAboveGround",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/AABB;move(DDD)Lnet/minecraft/world/phys/AABB;"
            )
    )
    private AABB wrapOperation_method_30263_offset_0(AABB box, double x, double y, double z, Operation<AABB> original) {
        Direction gravityDirection = GravityAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return original.call(box, x, y, z);
        }

        Vec3 world = RotationUtil.vecPlayerToWorld(x, y, z, gravityDirection);
        return original.call(box, world.x, world.y, world.z);
    }

    @WrapOperation(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getYRot()F",
                    ordinal = 0
            )
    )
    private float wrapOperation_attack_getYaw_0(Player attacker, Operation<Float> original, Entity target) {
        Direction targetGravityDirection = GravityAPI.getGravityDirection(target);
        Direction attackerGravityDirection = GravityAPI.getGravityDirection(attacker);
        if (targetGravityDirection == attackerGravityDirection) {
            return original.call(attacker);
        }

        return RotationUtil.rotWorldToPlayer(RotationUtil.rotPlayerToWorld(original.call(attacker), attacker.getXRot(), attackerGravityDirection), targetGravityDirection).x;
    }

    @WrapOperation(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getYRot()F",
                    ordinal = 1
            )
    )
    private float wrapOperation_attack_getYaw_1(Player attacker, Operation<Float> original, Entity target) {
        Direction targetGravityDirection = GravityAPI.getGravityDirection(target);
        Direction attackerGravityDirection = GravityAPI.getGravityDirection(attacker);
        if (targetGravityDirection == attackerGravityDirection) {
            return original.call(attacker);
        }

        return RotationUtil.rotWorldToPlayer(RotationUtil.rotPlayerToWorld(original.call(attacker), attacker.getXRot(), attackerGravityDirection), targetGravityDirection).x;
    }

    @WrapOperation(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getYRot()F",
                    ordinal = 2
            )
    )
    private float wrapOperation_attack_getYaw_2(Player attacker, Operation<Float> original) {
        Direction gravityDirection = GravityAPI.getGravityDirection(attacker);
        if (gravityDirection == Direction.DOWN) {
            return original.call(attacker);
        }

        return RotationUtil.rotPlayerToWorld(original.call(attacker), attacker.getXRot(), gravityDirection).x;
    }

    @WrapOperation(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getYRot()F",
                    ordinal = 3
            )
    )
    private float wrapOperation_attack_getYaw_3(Player attacker, Operation<Float> original) {
        Direction gravityDirection = GravityAPI.getGravityDirection(attacker);
        if (gravityDirection == Direction.DOWN) {
            return original.call(attacker);
        }

        return RotationUtil.rotPlayerToWorld(original.call(attacker), attacker.getXRot(), gravityDirection).x;
    }

    @WrapOperation(
            method = "addParticlesAroundSelf",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
            )
    )
    private void redirect_addDeathParticles_getRandomX_0(
            Level instance, ParticleOptions pParticleData,
            double pX, double pY, double pZ,
            double pXSpeed, double pYSpeed, double pZSpeed,
            Operation<Void> original
    ) {
        Direction gravityDirection = GravityAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) original.call(instance, pParticleData, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);

        Vec3 vec3d = this.position().subtract(RotationUtil.vecPlayerToWorld(this.position().subtract(pX, pY, pZ), gravityDirection));
        original.call(instance, pParticleData, vec3d.x, vec3d.y, vec3d.z, pXSpeed, pYSpeed, pZSpeed);
    }

    @WrapOperation(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/AABB;inflate(DDD)Lnet/minecraft/world/phys/AABB;"
            )
    )
    private AABB wrapOperation_tickMovement_expand_0(AABB box, double x, double y, double z, Operation<AABB> original) {
        Direction gravityDirection = GravityAPI.getGravityDirection((Entity) (Object) this);
        if (gravityDirection == Direction.DOWN) {
            return original.call(box, x, y, z);
        }

        Vec3 vec3d = RotationUtil.maskPlayerToWorld(x, y, z, gravityDirection);
        return original.call(box, vec3d.x, vec3d.y, vec3d.z);
    }


}
