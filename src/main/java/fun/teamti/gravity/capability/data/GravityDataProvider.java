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

    private final GravityData gravityData;
    private final LazyOptional<GravityData> gravityDataOptional;

    public GravityDataProvider(Entity entity) {
        //Is this needed?
        //entity.setBoundingBox(entity.getDimensions(entity.getPose()).makeBoundingBox(entity.position()));
        this.gravityData = new GravityData(entity);
        this.gravityDataOptional = LazyOptional.of(() -> gravityData);
    }

    public void invalidate() {
        gravityDataOptional.invalidate();
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
}
