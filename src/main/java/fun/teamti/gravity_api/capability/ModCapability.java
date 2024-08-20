package fun.teamti.gravity_api.capability;

import fun.teamti.gravity_api.capability.data.GravityData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ModCapability {

    public static Capability<GravityData> GRAVITY_DATA_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

}
