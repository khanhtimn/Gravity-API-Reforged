package fun.teamti.gravity.effect;

import fun.teamti.gravity.init.ModCapability;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class GravityStrengthMobEffect extends MobEffect {

    private final double base;
    private final int signum;

    public GravityStrengthMobEffect(int color, double base, int signum) {
        super(MobEffectCategory.NEUTRAL, color);
        this.base = base;
        this.signum = signum;
    }

    private double getGravityStrengthMultiplier(int amplifier) {
        return Math.pow(base, amplifier) * signum;
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        pLivingEntity.getCapability(ModCapability.GRAVITY_DATA).ifPresent(gravityData ->
                gravityData.applyGravityStrengthEffect(getGravityStrengthMultiplier(pAmplifier + 1)));
        super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }
}
