package fun.teamti.gravity.mixin;

import fun.teamti.gravity.api.GravityAPI;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ExperienceOrb.class)
public class ExperienceOrbMixin {
    @ModifyArg(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
            ),
            index = 1
    )
    private double multiplyGravity(double x) {
        return x * GravityAPI.getGravityStrength(((Entity) (Object) this));
    }
}
