package fun.teamti.gravity.network;


import fun.teamti.gravity.capability.dimension.DimensionGravityData;
import fun.teamti.gravity.init.ModCapability;
import fun.teamti.gravity.init.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Optional;
import java.util.function.Supplier;

public class DimensionGravitySyncPacket {
    private final CompoundTag nbtData;
    private final ResourceKey<Level> dimensionKey;

    public DimensionGravitySyncPacket(CompoundTag nbtData, ResourceKey<Level> dimensionKey) {
        this.nbtData = nbtData;
        this.dimensionKey = dimensionKey;
    }

    public DimensionGravitySyncPacket(FriendlyByteBuf buffer) {
        this.nbtData = buffer.readNbt();
        this.dimensionKey = buffer.readResourceKey(Registries.DIMENSION);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(this.nbtData);
        buf.writeResourceKey(this.dimensionKey);
    }


    public static void handle(DimensionGravitySyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                handleOnClient(packet);
            } else {
                handleOnServer(packet, ctx.get().getSender());
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private static void handleOnClient(DimensionGravitySyncPacket packet) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Optional<ClientLevel> clientLevel = Optional.ofNullable(Minecraft.getInstance().level);
            clientLevel.ifPresent(level -> {
                if (level.dimension().equals(packet.dimensionKey)) {
                    level.getCapability(ModCapability.DIMENSION_GRAVITY_DATA).ifPresent(
                            dimensionGravityData -> dimensionGravityData.deserializeNBT(packet.nbtData)
                    );
                }
            });
        });
    }

    private static void handleOnServer(DimensionGravitySyncPacket packet, ServerPlayer sender) {
        if (sender != null) {
            Optional<ServerLevel> serverLevel = Optional.of(sender.serverLevel());
            serverLevel.ifPresent(level -> {
                if (level.dimension().equals(packet.dimensionKey)) {
                    level.getCapability(ModCapability.DIMENSION_GRAVITY_DATA).ifPresent(dimensionGravityData -> {
                        dimensionGravityData.deserializeNBT(packet.nbtData);
                        sendToDimension(level, dimensionGravityData);
                    });
                }
            });
        }
    }

    public static void sendToClient(ServerPlayer player, DimensionGravityData dimensionGravityData) {
        DimensionGravitySyncPacket packet = new DimensionGravitySyncPacket(
                dimensionGravityData.serializeNBT(),
                player.level().dimension()
        );
        ModNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), packet);
    }

    public static void sendToDimension(ServerLevel level, DimensionGravityData dimensionGravityData) {
        DimensionGravitySyncPacket packet = new DimensionGravitySyncPacket(
                dimensionGravityData.serializeNBT(),
                level.dimension()
        );
        ModNetwork.INSTANCE.send(
                PacketDistributor.DIMENSION.with(level::dimension),
                packet
        );
    }
}