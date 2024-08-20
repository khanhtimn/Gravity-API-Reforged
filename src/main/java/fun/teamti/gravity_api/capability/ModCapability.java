package fun.teamti.gravity_api.capability;

import fun.teamti.gravity_api.capability.data.GravityData;
import fun.teamti.gravity_api.capability.dimension.DimensionGravityData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ModCapability {

    public static Capability<GravityData> GRAVITY_DATA = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static Capability<DimensionGravityData> DIMENSION_GRAVITY_DATA = CapabilityManager.get(new CapabilityToken<>() {
    });
}
