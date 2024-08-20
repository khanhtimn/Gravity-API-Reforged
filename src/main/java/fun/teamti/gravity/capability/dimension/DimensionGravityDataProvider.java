package fun.teamti.gravity.capability.dimension;

import fun.teamti.gravity.init.ModCapability;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DimensionGravityDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

//    private DimensionGravityData dimensionGravityData = null;
//    private final LazyOptional<DimensionGravityData> dimensionGravityDataOptional = LazyOptional.of(this::createDimensionGravityData);
//
//    private DimensionGravityData createDimensionGravityData() {
//        if(this.dimensionGravityData == null) {
//            dimensionGravityData = new DimensionGravityData();
//        }
//        return dimensionGravityData;
//    }
//

    private final DimensionGravityData dimensionGravityData;
    private final LazyOptional<DimensionGravityData> dimensionGravityDataOptional;

    public DimensionGravityDataProvider(Level level) {
        this.dimensionGravityData = new DimensionGravityData(level);
        this.dimensionGravityDataOptional = LazyOptional.of(() -> dimensionGravityData);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ModCapability.DIMENSION_GRAVITY_DATA.orEmpty(cap, dimensionGravityDataOptional);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return ModCapability.DIMENSION_GRAVITY_DATA.orEmpty(cap, dimensionGravityDataOptional);
    }

    @Override
    public CompoundTag serializeNBT() {
        return dimensionGravityData.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        dimensionGravityData.deserializeNBT(nbt);
    }
}
