package fun.teamti.gravity.effect;

import fun.teamti.gravity.GravityMod;
import fun.teamti.gravity.capability.data.GravityData;
import fun.teamti.gravity.init.ModCapability;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

public class GravityDirectionMobEffect extends MobEffect {

    private static final int COLOR = 0x98D982;

    public final Direction gravityDirection;

    public GravityDirectionMobEffect(Direction gravityDirection) {
        super(MobEffectCategory.NEUTRAL, COLOR);
        this.gravityDirection = gravityDirection;
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        pLivingEntity.getCapability(ModCapability.GRAVITY_DATA).ifPresent(gravityData ->
                apply(pLivingEntity, pAmplifier, gravityData)
        );
        super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    //TODO: Would this cause problem when stacking effects?
    //Should separate resetting of gravity direction and applying of gravity direction
    private void apply(LivingEntity entity, int pAmplifier, GravityData gravityData) {
        MobEffectInstance effectInstance = entity.getEffect(this);

        if (effectInstance != null && effectInstance.getDuration() > 1) {
            gravityData.applyGravityDirectionEffect(
                    gravityDirection,
                    null,
                    pAmplifier + 1
            );
        } else {
            gravityData.applyGravityDirectionEffect(
                    gravityData.getBaseGravityDirection(),
                    null,
                    pAmplifier + 2
            );
        }
        if (!entity.level().isClientSide()) {
            gravityData.setNeedsSync(true);
        }
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Mod.EventBusSubscriber(modid = GravityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    private static class GravityDirEffectEvent {

        @SubscribeEvent
        public static void onRemoveEffect(MobEffectEvent.Remove event) {
            if (event.getEffect() instanceof GravityDirectionMobEffect) {
                event.getEntity().getCapability(ModCapability.GRAVITY_DATA).ifPresent(gravityData -> {
                    gravityData.applyGravityDirectionEffect(
                            gravityData.getBaseGravityDirection(),
                            null,
                            10
                    );
                    if (!event.getEntity().level().isClientSide()) {
                        gravityData.setNeedsSync(true);
                    }
                });
            }
        }
    }
}
