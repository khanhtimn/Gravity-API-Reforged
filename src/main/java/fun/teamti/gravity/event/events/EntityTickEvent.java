package fun.teamti.gravity.event.events;

import fun.teamti.gravity.GravityMod;
import fun.teamti.gravity.capability.data.GravityData;
import fun.teamti.gravity.init.ModCapability;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GravityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityTickEvent {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingEntityTickEvent(LivingEvent.LivingTickEvent event) {
        event.getEntity().getCapability(ModCapability.GRAVITY_DATA).ifPresent(GravityData::tick);
    }
}
