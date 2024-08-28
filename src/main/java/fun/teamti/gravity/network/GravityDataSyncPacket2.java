package fun.teamti.gravity.network;


import fun.teamti.gravity.init.ModCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class GravityDataSyncPacket2 {


    private final int entityId;
    private final CompoundTag data;

    public GravityDataSyncPacket2(Entity entity, CompoundTag data) {
        this.entityId = entity.getId();
        this.data = data;
    }

    public GravityDataSyncPacket2(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.data = buffer.readNbt();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeNbt(this.data);
    }

    public static void handle(GravityDataSyncPacket2 packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            assert level != null;
            Entity entity = level.getEntity(packet.entityId);
            if (entity != null) {
                entity.getCapability(ModCapability.GRAVITY_DATA).ifPresent(gravityData -> {
                    gravityData.updateFromPacket(
                            Direction.byName(packet.data.getString("currentGravityDirection")),
                            packet.data.getDouble("currentGravityStrength")
                    );
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void sendToClient(Entity entity, CompoundTag tag, SimpleChannel channel) {
        channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new GravityDataSyncPacket2(entity, tag));
    }

//    public static void sendToClient(Entity entity, ServerPlayer player, SimpleChannel channel) {
//        channel.send(
//                PacketDistributor.PLAYER.with(() -> player),
//                new GravityDataSyncPacket2(entity)
//        );
//    }
}
