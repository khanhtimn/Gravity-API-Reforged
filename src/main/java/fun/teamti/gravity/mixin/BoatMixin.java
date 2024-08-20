package fun.teamti.gravity.mixin;

import fun.teamti.gravity.api.GravityAPI;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Boat.class)
public class BoatMixin {
    @ModifyConstant(method = "floatBoat()V", constant = @Constant(doubleValue = (double) -0.04F))
    private double multiplyGravity(double constant) {
        return constant * GravityAPI.getGravityStrength(((Entity) (Object) this));
    }
}
