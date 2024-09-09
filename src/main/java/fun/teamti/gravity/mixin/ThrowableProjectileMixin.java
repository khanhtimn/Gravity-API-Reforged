package fun.teamti.gravity.mixin;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import fun.teamti.gravity.api.GravityAPI;
import fun.teamti.gravity.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ThrowableProjectile.class)
public abstract class ThrowableProjectileMixin {

    @Shadow
    protected abstract float getGravity();

    @ModifyVariable(
            method = "tick()V",
            at = @At(
                    value = "STORE"
            ),
            ordinal = 0
    )
    public Vec3 tick(Vec3 modify) {
        modify = new Vec3(modify.x, modify.y + this.getGravity(), modify.z);
        modify = RotationUtil.vecWorldToPlayer(modify, GravityAPI.getGravityDirection((ThrowableProjectile) (Object) this));
        modify = new Vec3(modify.x, modify.y - this.getGravity(), modify.z);
        modify = RotationUtil.vecPlayerToWorld(modify, GravityAPI.getGravityDirection((ThrowableProjectile) (Object) this));
        return modify;
    }

    @Redirect(
            method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getX()D"
            )
    )
    private static double redirect_init_getX_0(LivingEntity owner) {
        Direction gravityDirection = GravityAPI.getGravityDirection(owner);
        if (gravityDirection == Direction.DOWN) return owner.getX();

        Vec3 pos = owner.getEyePosition().subtract(RotationUtil.vecPlayerToWorld(0.0D, (double) 0.1F, 0.0D, gravityDirection));
        return pos.x;
    }

    @Redirect(
            method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getEyeY()D"
            )
    )
    private static double redirect_init_getEyeY_0(LivingEntity owner) {
        Direction gravityDirection = GravityAPI.getGravityDirection(owner);
        if (gravityDirection == Direction.DOWN) return owner.getEyeY();

        Vec3 pos = owner.getEyePosition().subtract(RotationUtil.vecPlayerToWorld(0.0D, (double) 0.1F, 0.0D, gravityDirection));
        return pos.y;
    }

    @Redirect(
            method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getZ()D"
            )
    )
    private static double redirect_init_getZ_0(LivingEntity owner) {
        Direction gravityDirection = GravityAPI.getGravityDirection(owner);
        if (gravityDirection == Direction.DOWN) return owner.getZ();

        Vec3 pos = owner.getEyePosition().subtract(RotationUtil.vecPlayerToWorld(0.0D, (double) 0.1F, 0.0D, gravityDirection));
        return pos.z;
    }

    @ModifyReturnValue(method = "getGravity", at = @At("RETURN"))
    private float multiplyGravity(float original) {
        return original * (float) GravityAPI.getGravityStrength(((Entity) (Object) this));
    }
}
