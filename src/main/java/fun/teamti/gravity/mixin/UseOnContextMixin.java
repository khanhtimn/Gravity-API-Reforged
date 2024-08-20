package fun.teamti.gravity.mixin;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fun.teamti.gravity.api.GravityAPI;
import fun.teamti.gravity.util.RotationUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(UseOnContext.class)
public abstract class UseOnContextMixin {
    @WrapOperation(
            method = "getRotation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getYRot()F",
                    ordinal = 0
            )
    )
    private float wrapOperation_getPlayerYaw_getYaw_0(Player entity, Operation<Float> original) {
        Direction gravityDirection = GravityAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return original.call(entity);
        }

        return RotationUtil.rotPlayerToWorld(original.call(entity), entity.getXRot(), gravityDirection).x;
    }
}
