package fun.teamti.gravity.network;


import fun.teamti.gravity.init.ModCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;


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

    // Sending the packet to clients tracking the entity
    public static void sendToClient(Entity entity, CompoundTag tag, SimpleChannel channel) {
        channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new GravityDataSyncPacket(entity, tag));
    }

    // Handling the packet on the client side
    public static void handle(GravityDataSyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert Minecraft.getInstance().level != null;
            Entity entity = Minecraft.getInstance().level.getEntity(packet.entityId);
            if (entity != null) {
                entity.getCapability(ModCapability.GRAVITY_DATA).ifPresent(gravityData -> {
                    gravityData.deserializeNBT(packet.nbtData);
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
