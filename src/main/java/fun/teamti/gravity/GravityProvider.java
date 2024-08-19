package fun.teamti.gravity;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public class GravityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<GravityComponent> GRAVITY_COMP_KEY = CapabilityManager.get(new CapabilityToken<GravityComponent>() {});

    private GravityComponent gravity = null;
    private final LazyOptional<GravityComponent> gravityOptional = LazyOptional.of(this::createGravityComponent);

    private GravityComponent createGravityComponent() {
        if (this.gravity == null) {
            this.gravity = new GravityComponent();
        }
        return this.gravity;
    }


    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        return cap == GRAVITY_COMP_KEY ? gravityOptional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        createGravityComponent().writeToNbt(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        createGravityComponent().readFromNbt(tag);
    }
}
