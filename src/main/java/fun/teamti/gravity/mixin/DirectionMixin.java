package fun.teamti.gravity.mixin;

import fun.teamti.gravity.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(value = Direction.class, priority = 1001)
public abstract class DirectionMixin {
    @WrapOperation(
            method = "orderedByNearest",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;getViewYRot(F)F",
                    ordinal = 0
            )
    )
    private static float wrapOperation_getEntityFacingOrder_getViewYRot_0(Entity entity, float tickDelta, Operation<Float> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if(gravityDirection == Direction.DOWN) {
            return original.call(entity, tickDelta);
        }

        return RotationUtil.rotPlayerToWorld(original.call(entity, tickDelta), entity.getViewXRot(tickDelta), gravityDirection).x;
    }

    @WrapOperation(
            method = "orderedByNearest",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;getViewXRot(F)F",
                    ordinal = 0
            )
    )
    private static float wrapOperation_getEntityFacingOrder_getViewXRot_0(Entity entity, float tickDelta, Operation<Float> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if(gravityDirection == Direction.DOWN) {
            return original.call(entity, tickDelta);
        }

        return RotationUtil.rotPlayerToWorld(entity.getViewYRot(tickDelta), original.call(entity, tickDelta), gravityDirection).y;
    }

    @WrapOperation(
            method = "getFacingAxis",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;getViewYRot(F)F",
                    ordinal = 0
            )
    )
    private static float wrapOperation_getLookDirectionForAxis_getViewYRot_0(Entity entity, float tickDelta, Operation<Float> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if(gravityDirection == Direction.DOWN) {
            return original.call(entity, tickDelta);
        }

        return RotationUtil.rotPlayerToWorld(original.call(entity, tickDelta), entity.getViewXRot(tickDelta), gravityDirection).x;
    }

    @WrapOperation(
            method = "getFacingAxis",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;getViewYRot(F)F",
                    ordinal = 1
            )
    )
    private static float wrapOperation_getLookDirectionForAxis_getViewYRot_1(Entity entity, float tickDelta, Operation<Float> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if(gravityDirection == Direction.DOWN) {
            return original.call(entity, tickDelta);
        }

        return RotationUtil.rotPlayerToWorld(original.call(entity, tickDelta), entity.getViewXRot(tickDelta), gravityDirection).x;
    }

    @WrapOperation(
            method = "getFacingAxis",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;getViewXRot(F)F",
                    ordinal = 0
            )
    )
    private static float wrapOperation_getLookDirectionForAxis_getViewXRot_0(Entity entity, float tickDelta, Operation<Float> original) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if(gravityDirection == Direction.DOWN) {
            return original.call(entity, tickDelta);
        }

        return RotationUtil.rotPlayerToWorld(entity.getViewYRot(tickDelta), original.call(entity, tickDelta), gravityDirection).y;
    }
}
