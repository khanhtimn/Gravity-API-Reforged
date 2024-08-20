package fun.teamti.gravity_api.capability.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface IGravityData extends INBTSerializable<CompoundTag> {
}
