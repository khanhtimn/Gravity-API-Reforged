package fun.teamti.gravity.mixin.client;

import fun.teamti.gravity.capability.data.GravityData;
import fun.teamti.gravity.init.ModCapability;
import fun.teamti.gravity.init.ModTag;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    @Inject(
            method = "tickNonPassenger",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V", shift = At.Shift.AFTER)
    )
    private void tick(Entity p_104640_, CallbackInfo ci) {
        if (!(ModTag.canChangeGravity(p_104640_))) {
            return;
        }
        p_104640_.getCapability(ModCapability.GRAVITY_DATA).ifPresent(GravityData::tick);
    }

    @Inject(
            method = "tickPassenger",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;rideTick()V", shift = At.Shift.AFTER)
    )
    private void tickRiding(Entity vehicle, Entity passenger, CallbackInfo ci) {
        if (!(ModTag.canChangeGravity(passenger))) {
            return;
        }
        passenger.getCapability(ModCapability.GRAVITY_DATA).ifPresent(GravityData::tick);
    }
}
