package fun.teamti.gravity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import fun.teamti.gravity.api.GravityAPI;
import fun.teamti.gravity.util.RotationUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    // make sure fall distance is correct on server side of the player
    @WrapOperation(
            method = "doCheckFallDamage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;checkFallDamage(DZLnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)V"
            )
    )
    private void wrapCheckFallDamage(
            ServerPlayer instance,
            double pMovementY, boolean pOnGround,
            BlockState blockState, BlockPos blockPos,
            Operation<Void> original,
            @Share("pMovementX") LocalDoubleRef argRefX,
            @Share("pMovementZ") LocalDoubleRef argRefZ
    ) {
        Direction gravityDirection = GravityAPI.getGravityDirection(instance);
        if (gravityDirection == Direction.DOWN) original.call(instance, pMovementY, pOnGround, blockState, blockPos);

        Vec3 localVec = RotationUtil.vecWorldToPlayer(argRefX.get(), pMovementY, argRefZ.get(), gravityDirection);
        original.call(instance, localVec.y(), pOnGround, blockState, blockPos);
    }

    @Inject(
            method = "doCheckFallDamage",
            at = @At("HEAD")
    )
    private void saveDoCheckFallDamageArg(
            double pMovementX, double pMovementY, double pMovementZ,
            boolean pOnGround, CallbackInfo ci,
            @Share("pMovementX") LocalDoubleRef argRefX,
            @Share("pMovementZ") LocalDoubleRef argRefZ
    ) {
        argRefX.set(pMovementX);
        argRefZ.set(pMovementZ);
    }
}
