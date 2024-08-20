package fun.teamti.gravity.capability.data;

import fun.teamti.gravity.init.ModCapability;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public class GravityDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

//    private GravityData gravityData = null;
//    private final LazyOptional<GravityData> gravityDataOptional = LazyOptional.of(this::createGravityData);
//
//    private GravityData createGravityData() {
//        if (this.gravityData == null) {
//            this.gravityData = new GravityData();
//        }
//        return this.gravityData;
//    }
//
//    @Override
//    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
//        return ModCapability.GRAVITY_DATA.orEmpty(cap, gravityDataOptional);
//    }
//
//    @Override
//    public CompoundTag serializeNBT() {
//        return createGravityData().serializeNBT();
//    }
//
//    @Override
//    public void deserializeNBT(CompoundTag tag) {
//        createGravityData().deserializeNBT(tag);
//    }


    private final GravityData gravityData;
    private final LazyOptional<GravityData> gravityDataOptional;

    public GravityDataProvider(Entity entity) {
        this.gravityData = new GravityData(entity);
        this.gravityDataOptional = LazyOptional.of(() -> gravityData);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        return ModCapability.GRAVITY_DATA.orEmpty(cap, gravityDataOptional);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return ModCapability.GRAVITY_DATA.orEmpty(cap, gravityDataOptional);
    }

    @Override
    public CompoundTag serializeNBT() {
        return gravityData.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        gravityData.deserializeNBT(tag);
    }

//    private final GravityData gravityData = new GravityData();
//    private final LazyOptional<GravityData> gravityDataOptional = LazyOptional.of(() -> gravityData);
//    @Override
//    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
//        return ModCapability.GRAVITY_DATA.orEmpty(cap, gravityDataOptional);
//    }
//
//
//    @Override
//    public CompoundTag serializeNBT() {
//        return this.gravityData.serializeNBT();
//    }
//
//    @Override
//    public void deserializeNBT(CompoundTag nbt) {
//        this.gravityData.deserializeNBT(nbt);
//    }
}
