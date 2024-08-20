package fun.teamti.gravity.mixin;

import fun.teamti.gravity.api.GravityAPI;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @ModifyConstant(method = "tick()V", constant = @Constant(doubleValue = -0.04))
    private double multiplyGravity(double constant) {
        return constant * GravityAPI.getGravityStrength(this);
    }
}
