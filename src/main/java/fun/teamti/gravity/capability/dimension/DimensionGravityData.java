package fun.teamti.gravity.capability.dimension;

import fun.teamti.gravity.init.ModNetwork;
import fun.teamti.gravity.network.DimensionGravitySyncPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

public class DimensionGravityData implements INBTSerializable<CompoundTag> {

    double dimensionGravityStrength = 1;
    
    private final Level currentWorld;
    
    public DimensionGravityData(Level level) {
        this.currentWorld = level;
    }

    public double getDimensionGravityStrength() {
        return dimensionGravityStrength;
    }

    public void setDimensionGravityStrength(double strength) {
        if (!currentWorld.isClientSide) {
            dimensionGravityStrength = strength;
        }
        //TODO: Sync state of client and server
        DimensionGravitySyncPacket.sendToServer(currentWorld, serializeNBT(), ModNetwork.INSTANCE);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("DimensionGravityStrength", dimensionGravityStrength);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        dimensionGravityStrength = tag.getDouble("DimensionGravityStrength");
    }
}
