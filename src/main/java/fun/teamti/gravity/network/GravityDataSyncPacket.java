package fun.teamti.gravity.network;


import fun.teamti.gravity.init.ModCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

public class GravityDataSyncPacket {

    private final int entityId;
    private final CompoundTag nbtData;

    public GravityDataSyncPacket(Entity entity, CompoundTag nbtData) {
        this.entityId = entity.getId();
        this.nbtData = nbtData;
    }

    public GravityDataSyncPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.nbtData = buffer.readNbt();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeNbt(this.nbtData);
    }

    public static void handle(GravityDataSyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (!(ctx.get().getDirection().getReceptionSide().isClient() || ctx.get().getDirection().getOriginationSide().isServer())) {
                return;
            }
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                assert Minecraft.getInstance().level != null;
                handleClientSync(Minecraft.getInstance().level, packet.entityId, packet.nbtData);
            });
        });
        ctx.get().setPacketHandled(true);
    }

    private static void handleClientSync(@NotNull ClientLevel level, int entityId, @NotNull CompoundTag nbtData) {
        Objects.requireNonNull(level.getEntity(entityId))
                .getCapability(ModCapability.GRAVITY_DATA).ifPresent(
                        gravityData -> gravityData.deserializeNBT(nbtData)
                );
    }

    public static void sendToClient(Entity entity, CompoundTag tag, SimpleChannel channel) {
        channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new GravityDataSyncPacket(entity, tag));
    }
}
