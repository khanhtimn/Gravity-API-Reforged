package fun.teamti.gravity.mixin.debug;

import fun.teamti.gravity.api.GravityAPI;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity_Debug {

    @Shadow
    private Vec3 position;

    @Inject(
        method = "setPosRaw", at = @At("HEAD")
    )
    private void debugOnSetPos(double x, double y, double z, CallbackInfo ci) {
        Entity this_ = (Entity) (Object) this;
        if (this_ instanceof ItemEntity) {
            String str = "%s ItemEntity#setPosRaw(%s, %s, %s) grav %s %s".formatted(
                this_.level().isClientSide() ? "client" : "server", x, y, z,
                GravityAPI.getGravityDirection(this_),
                GravityAPI.getGravityStrength(this_)
            );
            this_.sendSystemMessage(Component.literal(str));
        }
    }
}
