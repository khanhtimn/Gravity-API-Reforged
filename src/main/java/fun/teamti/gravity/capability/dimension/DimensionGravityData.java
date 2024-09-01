package fun.teamti.gravity.capability.dimension;

import fun.teamti.gravity.network.DimensionGravitySyncPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

public class DimensionGravityData implements INBTSerializable<CompoundTag> {
    private double dimensionGravityStrength = 1;
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
            syncToClients();
        }
    }

    private void syncToClients() {
        currentWorld.players().forEach(player -> {
            if (player instanceof ServerPlayer serverPlayer) {
                DimensionGravitySyncPacket.sendToClient(serverPlayer, this);
            }
        });
    }

    private void syncToServer() {
        if (currentWorld instanceof ServerLevel currentServerLevel) {
            DimensionGravitySyncPacket.sendToDimension(currentServerLevel, this);
        }
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