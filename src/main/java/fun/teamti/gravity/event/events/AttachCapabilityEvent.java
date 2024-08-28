package fun.teamti.gravity.event.events;

import fun.teamti.gravity.GravityMod;
import fun.teamti.gravity.capability.data.GravityData;
import fun.teamti.gravity.capability.data.GravityDataProvider;
import fun.teamti.gravity.capability.dimension.DimensionGravityDataProvider;
import fun.teamti.gravity.event.GravityUpdateEvent;
import fun.teamti.gravity.init.ModCapability;
import fun.teamti.gravity.init.ModNetwork;
import fun.teamti.gravity.network.GravityDataSyncPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GravityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttachCapabilityEvent {

    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity != null && !entity.getCapability(ModCapability.GRAVITY_DATA).isPresent()) {
            String isClientSide = entity.level().isClientSide() ? "from clientside" : "";
            GravityMod.LOGGER.info("Attaching gravity data capability to entity: {} {}", entity.getClass().getSimpleName(), isClientSide);
            event.addCapability(
                    new ResourceLocation(GravityMod.MOD_ID, "gravity_data"),
                    new GravityDataProvider(entity)
            );
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

    @SubscribeEvent
    public static void onGravityUpdateEvent(GravityUpdateEvent event) {
        event.getEntity().getCapability(ModCapability.GRAVITY_DATA).ifPresent(GravityData::tick);
    }

    @SubscribeEvent
    public static void onPLayerStartTracking(PlayerEvent.StartTracking event) {
        Entity entity = event.getTarget();
        event.getEntity().getCapability(ModCapability.GRAVITY_DATA).ifPresent(gravityData -> {
            GravityDataSyncPacket.sendToClient(entity, gravityData.serializeNBT(), ModNetwork.INSTANCE);
        });
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        event.getEntity().getCapability(ModCapability.GRAVITY_DATA).ifPresent(gravityData -> {
            GravityDataSyncPacket.sendToClient(player, gravityData.serializeNBT(), ModNetwork.INSTANCE);
        });
    }
}
