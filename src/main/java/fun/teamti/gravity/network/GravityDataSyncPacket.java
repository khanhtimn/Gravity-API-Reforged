package fun.teamti.gravity.network;



import fun.teamti.gravity.capability.data.GravityData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;


import java.util.function.Supplier;

public class GravityDataSyncPacket {
    private final int entityId;
    private final Direction gravityDirection;
    private final double gravityStrength;

    public GravityDataSyncPacket(Entity entity, GravityData gravityData) {
        this.entityId = entity.getId();
        this.gravityDirection = gravityData.getCurrGravityDirection();
        this.gravityStrength = gravityData.getCurrGravityStrength();
    }

    public GravityDataSyncPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.gravityDirection = Direction.byName(buf.readUtf(16));
        this.gravityStrength = buf.readDouble();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeUtf(gravityDirection.getName());
        buf.writeDouble(gravityStrength);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            assert Minecraft.getInstance().level != null;
            Entity entity = Minecraft.getInstance().level.getEntity(entityId);
            if (entity != null) {
            }
        });
        context.get().setPacketHandled(true);
    }

    public static void sendToClient(Entity entity, GravityData gravityData, SimpleChannel channel) {
        if (!entity.level().isClientSide) {
            channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new GravityDataSyncPacket(entity, gravityData));
        }
    }
}
