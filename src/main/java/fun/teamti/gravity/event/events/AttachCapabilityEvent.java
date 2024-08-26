package fun.teamti.gravity.event.events;

import fun.teamti.gravity.GravityMod;
import fun.teamti.gravity.capability.data.GravityDataProvider;
import fun.teamti.gravity.capability.dimension.DimensionGravityDataProvider;
import fun.teamti.gravity.init.ModCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GravityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttachCapabilityEvent {

    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if(entity instanceof LivingEntity || entity instanceof Player) {
            if (!entity.getCapability(ModCapability.GRAVITY_DATA).isPresent()) {
                GravityDataProvider provider = new GravityDataProvider(entity);
                GravityMod.LOGGER.info("Attaching gravity data capability to entity: {}", entity.getClass().getSimpleName());
                event.addCapability(
                        new ResourceLocation(GravityMod.MOD_ID, "gravity_data"),
                        provider
                );
                //event.addListener(provider::invalidate);
            }
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesLevelChunk(AttachCapabilitiesEvent<Level> event) {
        if (event.getObject() instanceof Level && !event.getObject().getCapability(ModCapability.DIMENSION_GRAVITY_DATA).isPresent()) {
            GravityMod.LOGGER.info("Attaching dimension gravity data capability to level: {}", event.getObject().dimension());
            event.addCapability(
                    new ResourceLocation(GravityMod.MOD_ID, "dimension_data"),
                    new DimensionGravityDataProvider(event.getObject())
            );
        }
    }
}
