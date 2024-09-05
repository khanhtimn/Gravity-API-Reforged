package fun.teamti.gravity.event.events;

import fun.teamti.gravity.GravityMod;
import fun.teamti.gravity.capability.data.GravityData;
import fun.teamti.gravity.capability.data.GravityDataProvider;
import fun.teamti.gravity.capability.dimension.DimensionGravityDataProvider;
import fun.teamti.gravity.init.ModCapability;
import fun.teamti.gravity.init.ModConfig;
import fun.teamti.gravity.init.ModTag;
import fun.teamti.gravity.network.DimensionGravitySyncPacket;
import fun.teamti.gravity.network.client.GravityDataSyncPacket;
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
        if (!event.getObject().getCapability(ModCapability.DIMENSION_GRAVITY_DATA).isPresent()) {
            event.addCapability(
                    new ResourceLocation(GravityMod.MOD_ID, "dimension_data"),
                    new DimensionGravityDataProvider(event.getObject())
            );
        }
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
                owner.getCapability(ModCapability.GRAVITY_DATA).ifPresent(ownerGravityData -> {
                    ((Entity) traceableEntity).getCapability(ModCapability.GRAVITY_DATA).ifPresent(gravityData -> {
                        gravityData.deserializeNBT(ownerGravityData.serializeNBT());
                    });
                });
            }
        }
    }

    @SubscribeEvent
    public static void onPLayerStartTracking(PlayerEvent.StartTracking event) {
        Entity entity = event.getTarget();
        if (!(ModTag.canChangeGravity(entity)) && entity.level().isClientSide()) {
            return;
        }
        entity.getCapability(ModCapability.GRAVITY_DATA).ifPresent(gravityData -> {
            GravityDataSyncPacket.sendToClientTracking(entity, gravityData.serializeNBT());
        });
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        ServerPlayer player = (ServerPlayer) event.getEntity();
        player.getCapability(ModCapability.GRAVITY_DATA).ifPresent(gravityData -> {
            GravityDataSyncPacket.sendToClientPlayer(player, gravityData.serializeNBT());
            player.level().getCapability(ModCapability.DIMENSION_GRAVITY_DATA).ifPresent(dimensionGravityData -> {
                DimensionGravitySyncPacket.sendToClient(player, dimensionGravityData);
            });
        });
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        ServerPlayer originalPlayer = (ServerPlayer) event.getOriginal();
        ServerPlayer player = (ServerPlayer) event.getEntity();
        originalPlayer.reviveCaps();
        if (ModConfig.RESET_GRAVITY_ON_RESPAWN.get()) {
            player.getCapability(ModCapability.GRAVITY_DATA).ifPresent(GravityData::reset);
        } else {
            originalPlayer.getCapability(ModCapability.GRAVITY_DATA).ifPresent(originalGravityData -> {
                player.getCapability(ModCapability.GRAVITY_DATA).ifPresent(gravityData -> {
                    gravityData.setBaseGravityDirection(originalGravityData.getCurrGravityDirection());
                });
            });
        }
        originalPlayer.invalidateCaps();
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (ModConfig.RESET_GRAVITY_ON_CHANGED_DIMENSION.get()) {
            player.getCapability(ModCapability.GRAVITY_DATA).ifPresent(GravityData::reset);
        } else {
            player.getCapability(ModCapability.GRAVITY_DATA).ifPresent(gravityData -> {
                GravityDataSyncPacket.sendToClientPlayer(player, gravityData.serializeNBT());
                player.level().getCapability(ModCapability.DIMENSION_GRAVITY_DATA).ifPresent(dimensionGravityData -> {
                    DimensionGravitySyncPacket.sendToClient(player, dimensionGravityData);
                });
            });
        }

    }
}
