package fun.teamti.gravity_api.event.events;

import fun.teamti.gravity_api.GravityAPI;
import fun.teamti.gravity_api.capability.ModCapability;
import fun.teamti.gravity_api.capability.data.GravityData;
import fun.teamti.gravity_api.capability.data.GravityDataProvider;
import fun.teamti.gravity_api.capability.dimension.DimensionGravityData;
import fun.teamti.gravity_api.capability.dimension.DimensionGravityDataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class AttatchCapabilityEvent {

    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(GravityData.class);
        event.register(DimensionGravityData.class);
    }

    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() != null) {
            if (!event.getObject().getCapability(ModCapability.GRAVITY_DATA).isPresent()) {
                event.addCapability(
                        new ResourceLocation(GravityAPI.MOD_ID, "gravity_data"),
                        new GravityDataProvider(event.getObject())
                );
            }
        }
    }

    public static void onAttachCapabilitiesLevelChunk(AttachCapabilitiesEvent<Level> event) {
        if (event.getObject() instanceof Level && !event.getObject().getCapability(ModCapability.DIMENSION_GRAVITY_DATA).isPresent()) {
            event.addCapability(
                    new ResourceLocation(GravityAPI.MOD_ID, "dimension_data"),
                    new DimensionGravityDataProvider(event.getObject())
            );
        }
    }
}
