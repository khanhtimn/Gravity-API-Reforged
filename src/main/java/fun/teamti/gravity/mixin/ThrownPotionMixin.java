package fun.teamti.gravity.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import fun.teamti.gravity.api.GravityAPI;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrownPotion.class)
public class ThrownPotionMixin {
    @ModifyReturnValue(method = "getGravity", at = @At("RETURN"))
    private float multiplyGravity(float original) {
        return original * (float) GravityAPI.getGravityStrength(((Entity) (Object) this));
    }
}
