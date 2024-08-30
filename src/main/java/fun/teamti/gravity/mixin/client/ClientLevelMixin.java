package fun.teamti.gravity.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

//    @Inject(method = "tickNonPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V", shift = At.Shift.AFTER), cancellable = true)
//    private void tick(Entity entity, CallbackInfo ci) {
//        GravityUpdateEvent event = new GravityUpdateEvent(entity);
//        MinecraftForge.EVENT_BUS.post(event);
//
//        if (event.isCanceled()) {
//            ci.cancel();
//        }
//    }
//
//    @Inject(method = "tickPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;rideTick()V", shift = At.Shift.AFTER), cancellable = true)
//    private void tickRiding(Entity vehicle, Entity passenger, CallbackInfo ci) {
//        GravityUpdateEvent event = new GravityUpdateEvent(vehicle);
//        MinecraftForge.EVENT_BUS.post(event);
//
//        if (event.isCanceled()) {
//            ci.cancel();
//        }
//    }
}
