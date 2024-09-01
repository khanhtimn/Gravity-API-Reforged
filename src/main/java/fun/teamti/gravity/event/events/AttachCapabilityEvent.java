package fun.teamti.gravity.event.events;

import fun.teamti.gravity.GravityMod;
import fun.teamti.gravity.capability.data.GravityData;
import fun.teamti.gravity.capability.data.GravityDataProvider;
import fun.teamti.gravity.capability.dimension.DimensionGravityDataProvider;
import fun.teamti.gravity.init.ModCapability;
import fun.teamti.gravity.init.ModConfig;
import fun.teamti.gravity.init.ModNetwork;
import fun.teamti.gravity.init.ModTag;
import fun.teamti.gravity.network.GravityDataSyncPacket;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GravityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttachCapabilityEvent {

    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity != null && ModTag.canChangeGravity(entity)
                && !entity.getCapability(ModCapability.GRAVITY_DATA).isPresent()
        ) {
            event.addCapability(
                    new ResourceLocation(GravityMod.MOD_ID, "gravity_data"),
                    new GravityDataProvider(entity)
            );
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesLevelChunk(AttachCapabilitiesEvent<Level> event) {
        if (event.getObject() instanceof Level
                && !event.getObject().getCapability(ModCapability.DIMENSION_GRAVITY_DATA).isPresent()
        ) {
            event.addCapability(
                    new ResourceLocation(GravityMod.MOD_ID, "dimension_data"),
                    new DimensionGravityDataProvider(event.getObject())
            );
        }
    }

    @SubscribeEvent
    public static void onPLayerStartTracking(PlayerEvent.StartTracking event) {
        Entity entity = event.getTarget();
        if (!(ModTag.canChangeGravity(entity)) && entity.level().isClientSide()) {
            return;
        }
        entity.getCapability(ModCapability.GRAVITY_DATA).ifPresent(gravityData -> {
            GravityDataSyncPacket.sendToClient(entity, gravityData.serializeNBT(), ModNetwork.INSTANCE);
        });
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        ServerPlayer player = (ServerPlayer) event.getEntity();
        player.getCapability(ModCapability.GRAVITY_DATA).ifPresent(gravityData -> {
            GravityDataSyncPacket.sendToClient(player, gravityData.serializeNBT(), ModNetwork.INSTANCE);
        });
    }

    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinLevelEvent event) {
        Entity newEntity = event.getEntity();
        if (!(ModTag.canChangeGravity(newEntity))
                && newEntity.level().isClientSide()
                && ModConfig.SPAWNED_ENTITY_INHERIT_OWNER_GRAVITY.get()
        ) {
            return;
        }
        if (newEntity instanceof TraceableEntity traceableEntity) {
            Entity owner = traceableEntity.getOwner();
            if (owner != null) {
                owner.getCapability(ModCapability.GRAVITY_DATA).ifPresent(originalGravityData -> {
                    ((Entity) traceableEntity).getCapability(ModCapability.GRAVITY_DATA).ifPresent(gravityData -> {
                        gravityData.deserializeNBT(originalGravityData.serializeNBT());
                    });
                });
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        event.getOriginal().reviveCaps();
        if (ModConfig.RESET_GRAVITY_ON_RESPAWN.get()) {
            event.getEntity().getCapability(ModCapability.GRAVITY_DATA).ifPresent(GravityData::reset);
        } else {
            event.getOriginal().getCapability(ModCapability.GRAVITY_DATA).ifPresent(originalGravityData -> {
                Direction gravityEffectDir = originalGravityData.getCurrGravityDirection();
                event.getEntity().getCapability(ModCapability.GRAVITY_DATA).ifPresent(gravityData -> {
                    gravityData.applyGravityDirectionEffect(
                            gravityEffectDir, null, 1000
                    );
                    gravityData.setNeedsSync(true);
                });
            });
        }
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        if (ModConfig.RESET_GRAVITY_ON_CHANGED_DIMENSION.get()) {
            event.getEntity().getCapability(ModCapability.GRAVITY_DATA).ifPresent(GravityData::reset);
        } else {
            event.getEntity().getCapability(ModCapability.GRAVITY_DATA).ifPresent(gravityData -> {
                GravityDataSyncPacket.sendToClient(event.getEntity(), gravityData.serializeNBT(), ModNetwork.INSTANCE);
            });
        }

    }
}
