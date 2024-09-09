package fun.teamti.gravity.mixin;

import fun.teamti.gravity.api.GravityAPI;
import fun.teamti.gravity.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Entity {

    public AbstractArrowMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @ModifyVariable(
            method = "tick()V",
            at = @At(
                    value = "STORE"
            ),
            ordinal = 0
    )
    public Vec3 tick(Vec3 modify) {
        modify = new Vec3(modify.x, modify.y + 0.05, modify.z);
        modify = RotationUtil.vecWorldToPlayer(modify, GravityAPI.getGravityDirection(this));
        modify = new Vec3(modify.x, modify.y - 0.05, modify.z);
        modify = RotationUtil.vecPlayerToWorld(modify, GravityAPI.getGravityDirection(this));
        return modify;
    }

    @Redirect(
            method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getX()D"
            )
    )
    private static double modifyarg_init_init_0_x(LivingEntity owner) {
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
    private static double modifyarg_init_init_0_y(LivingEntity owner) {
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
    private static double modifyarg_init_init_0_z(LivingEntity owner) {
        Direction gravityDirection = GravityAPI.getGravityDirection(owner);
        if (gravityDirection == Direction.DOWN) return owner.getZ();

        Vec3 pos = owner.getEyePosition().subtract(RotationUtil.vecPlayerToWorld(0.0D, (double) 0.1F, 0.0D, gravityDirection));
        return pos.z;
    }

    @ModifyConstant(method = "tick()V", constant = @Constant(doubleValue = (double) 0.05F))
    private double multiplyGravity(double constant) {
        return constant * GravityAPI.getGravityStrength(this);
    }
}
