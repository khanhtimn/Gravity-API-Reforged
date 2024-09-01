package fun.teamti.gravity.network;


import fun.teamti.gravity.init.ModCapability;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class DimensionGravitySyncPacket {

    private final ResourceKey<Level> currentLevel;
    private final CompoundTag nbtData;

    public DimensionGravitySyncPacket(Level level, CompoundTag nbtData) {
        this.currentLevel = level.dimension();
        this.nbtData = nbtData;
    }

    public DimensionGravitySyncPacket(FriendlyByteBuf buffer) {
        this.currentLevel = buffer.readResourceKey(Registries.DIMENSION);
        this.nbtData = buffer.readNbt();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeResourceKey(this.currentLevel);
        buffer.writeNbt(this.nbtData);
    }

    public static void handle(DimensionGravitySyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Optional<ServerLevel> level = Optional.ofNullable(Objects.requireNonNull(Objects.requireNonNull(ctx.get().getSender()).getServer()).getLevel(packet.currentLevel));
            level.ifPresent(x -> {
                x.getCapability(ModCapability.DIMENSION_GRAVITY_DATA).ifPresent(dimensionGravityData -> {
                    dimensionGravityData.deserializeNBT(packet.nbtData);
                });
            });
        });
        ctx.get().setPacketHandled(true);
    }

    public static void sendToServer(Level level, CompoundTag tag, SimpleChannel channel) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            channel.sendToServer(new DimensionGravitySyncPacket(level, tag));
        });
    }
}
