package fun.teamti.gravity.effect;

import fun.teamti.gravity.GravityMod;
import fun.teamti.gravity.init.ModCapability;
import fun.teamti.gravity.init.ModEffect;
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

    public final Direction gravityDirection;

    public GravityDirectionMobEffect(Direction gravityDirection, int color) {
        super(MobEffectCategory.NEUTRAL, color);
        this.gravityDirection = gravityDirection;
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        pLivingEntity.getCapability(ModCapability.GRAVITY_DATA).ifPresent(gravityData -> {
            gravityData.applyGravityDirectionEffect(
                    gravityDirection,
                    null,
                    pAmplifier + 1
            );
            if (!pLivingEntity.level().isClientSide()) {
                gravityData.setNeedsSync(true);
            }
        });
        super.applyEffectTick(pLivingEntity, pAmplifier);
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

        @SubscribeEvent
        public static void onExpireEffect(MobEffectEvent.Expired event) {
            for (Direction dir : Direction.values()) {
                MobEffectInstance effectInstance = event.getEntity().getEffect(ModEffect.DIRECTION_EFFECT_MAP.get(dir));
                if (effectInstance != null) {
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
}
