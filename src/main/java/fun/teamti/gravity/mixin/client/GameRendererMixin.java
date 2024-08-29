package fun.teamti.gravity.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fun.teamti.gravity.util.RotationAnimation;
import fun.teamti.gravity.api.GravityAPI;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    private Camera mainCamera;

    @Inject(
            method = "renderLevel(FJLcom/mojang/blaze3d/vertex/PoseStack;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V",
                    ordinal = 4,
                    shift = At.Shift.AFTER
            )
    )
    private void inject_renderWorld(float tickDelta, long limitTime, PoseStack matrix, CallbackInfo ci) {
        Entity focusedEntity = this.mainCamera.getEntity();
        Direction gravityDirection = GravityAPI.getGravityDirection(focusedEntity);
        RotationAnimation animation = GravityAPI.getRotationAnimation(focusedEntity);
        if (animation == null) {
            return;
        }
        long timeMs = focusedEntity.level().getGameTime() * 50 + (long) (tickDelta * 50);
        Quaternionf currentGravityRotation = animation.getCurrentGravityRotation(gravityDirection, timeMs);

        if (animation.isInAnimation()) {
            // make sure that frustum culling updates when running rotation animation
            Minecraft.getInstance().levelRenderer.needsUpdate();
        }

        matrix.mulPose(currentGravityRotation);
    }
}
