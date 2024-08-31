package fun.teamti.gravity.mixin;

import fun.teamti.gravity.api.GravityAPI;
import fun.teamti.gravity.capability.data.GravityData;
import fun.teamti.gravity.init.ModCapability;
import fun.teamti.gravity.init.ModTag;
import fun.teamti.gravity.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public abstract class ProjectileMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        Entity entity = ((Entity) (Object) this);
        if (!(ModTag.canChangeGravity(entity))) {
            return;
        }
        entity.getCapability(ModCapability.GRAVITY_DATA).ifPresent(GravityData::tick);
    }

    @ModifyVariable(
            method = "shootFromRotation(Lnet/minecraft/world/entity/Entity;FFFFF)V",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true)
    private float modify_setProperties_pitch(float value, Entity user, float yaw, float roll, float speed, float divergence) {
        Direction gravityDirection = GravityAPI.getGravityDirection(user);
        if (gravityDirection == Direction.DOWN) {
            return value;
        }

        return RotationUtil.rotPlayerToWorld(user.getYRot(), user.getXRot(), gravityDirection).y;
    }

    @ModifyVariable(
            method = "shootFromRotation(Lnet/minecraft/world/entity/Entity;FFFFF)V",
            at = @At("HEAD"),
            ordinal = 1,
            argsOnly = true)
    private float modify_setProperties_yaw(float value, Entity user, float pitch, float roll, float speed, float divergence) {
        Direction gravityDirection = GravityAPI.getGravityDirection(user);
        if (gravityDirection == Direction.DOWN) {
            return value;
        }

        return RotationUtil.rotPlayerToWorld(user.getYRot(), user.getXRot(), gravityDirection).x;
    }
}
