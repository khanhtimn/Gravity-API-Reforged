package fun.teamti.gravity.mixin;

import fun.teamti.gravity.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;
import java.util.Map;

@Mixin(AreaEffectCloud.class)
public abstract class AreaEffectCloudEntityMixin extends Entity {

    @Shadow public abstract boolean isWaiting();

    @Shadow public abstract float getRadius();

    public AreaEffectCloudEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @ModifyArgs(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addAlwaysVisibleParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
            )
    )
    private void modify_move_multiply_0(Args args) {
        boolean bl = this.isWaiting();
        float f = this.getRadius();

        float g;
        if (bl) {
            g = 0.2F;
        } else {
            g = f;
        }

        float h = this.random.nextFloat() * 6.2831855F;
        float k = Mth.sqrt(this.random.nextFloat()) * g;

        double d = this.getX() ;
        double e = this.getY();
        double l = this.getZ() ;
        Vec3 modify = RotationUtil.vecWorldToPlayer(d,e,l, GravityChangerAPI.getGravityDirection(this));
        d = modify.x+ (double)(Mth.cos(h) * k);
        e = modify.y;
        l = modify.z+ (double)(Mth.sin(h) * k);
        modify = RotationUtil.vecPlayerToWorld(d,e,l, GravityChangerAPI.getGravityDirection(this));

        args.set(1,modify.x);
        args.set(2,modify.y);
        args.set(3,modify.z);
    }


}
