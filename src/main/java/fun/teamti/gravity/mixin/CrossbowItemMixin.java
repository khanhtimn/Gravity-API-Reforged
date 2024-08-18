package fun.teamti.gravity.mixin;

import fun.teamti.gravity.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {
    @Redirect(
            method = "shootProjectile",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getX()D",
                    ordinal = 0
            )
    )
    private static double redirect_shoot_getX_0(LivingEntity livingEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(livingEntity);
        if(gravityDirection == Direction.DOWN) {
            return livingEntity.getX();
        }

        return livingEntity.getEyePosition().subtract(RotationUtil.vecPlayerToWorld(0.0D, 0.15000000596046448D, 0.0D, gravityDirection)).x;
    }

    @Redirect(
            method = "shootProjectile",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getEyeY()D",
                    ordinal = 0
            )
    )
    private static double redirect_shoot_getEyeY_0(LivingEntity livingEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(livingEntity);
        if(gravityDirection == Direction.DOWN) {
            return livingEntity.getEyeY();
        }

        
        return livingEntity.getEyePosition().subtract(RotationUtil.vecPlayerToWorld(0.0D, 0.15000000596046448D, 0.0D, gravityDirection)).y + 0.15000000596046448D;
    }

    @Redirect(
            method = "shootProjectile",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getZ()D",
                    ordinal = 0
            )
    )
    private static double redirect_shoot_getZ_0(LivingEntity livingEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(livingEntity);
        if(gravityDirection == Direction.DOWN) {
            return livingEntity.getZ();
        }

        return livingEntity.getEyePosition().subtract(RotationUtil.vecPlayerToWorld(0.0D, 0.15000000596046448D, 0.0D, gravityDirection)).z;
    }
}
