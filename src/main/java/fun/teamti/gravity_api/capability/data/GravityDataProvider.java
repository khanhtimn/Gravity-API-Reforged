package fun.teamti.gravity_api.capability.data;

import fun.teamti.gravity_api.capability.ModCapability;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GravityDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

//    private GravityData gravity = null;
//    private final LazyOptional<GravityData> gravityOptional = LazyOptional.of(this::createGravityComponent);
//
//    private GravityData createGravityComponent() {
//        if (this.gravity == null) {
//            this.gravity = new GravityData();
//        }
//        return this.gravity;
//    }
//
//    @Override
//    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
//        return cap == GRAVITY_COMP_KEY ? gravityOptional.cast() : LazyOptional.empty();
//    }
//
//    @Override
//    public CompoundTag serializeNBT() {
//        CompoundTag tag = new CompoundTag();
//        createGravityComponent().writeToNbt(tag);
//        return tag;
//    }
//
//    @Override
//    public void deserializeNBT(CompoundTag tag) {
//        createGravityComponent().readFromNbt(tag);
//    }


//    private final GravityData gravityData;
//    private final LazyOptional<IGravityData> gravityDataOptional;
//
//    public GravityDataProvider(Entity entity) {
//        this.gravityData = new GravityData(entity);
//        this.gravityDataOptional = LazyOptional.of(() -> gravityData);
//    }
//
//    @Override
//    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
//        return cap == ModCapability.GRAVITY_DATA_CAPABILITY ? gravityDataOptional.cast() : LazyOptional.empty();
//    }
//
//    @Override
//    public CompoundTag serializeNBT() {
//        CompoundTag tag = new CompoundTag();
//        gravityData.writeToNbt(tag);
//        return tag;
//    }
//
//    @Override
//    public void deserializeNBT(CompoundTag nbt) {
//        gravityData.readFromNbt(nbt);
//    }

    private final IGravityData gravityData = new GravityData();
    private final LazyOptional<IGravityData> gravityDataOptional = LazyOptional.of(() -> gravityData);
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ModCapability.GRAVITY_DATA_CAPABILITY.orEmpty(cap, gravityDataOptional);
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.gravityData.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.gravityData.deserializeNBT(nbt);
    }
}
