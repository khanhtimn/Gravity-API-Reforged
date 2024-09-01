package fun.teamti.gravity.network.client;


import fun.teamti.gravity.capability.data.GravityData;
import fun.teamti.gravity.init.ModCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
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
            if (ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT) {
                return;
            }
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Optional<ClientLevel> level = Optional.ofNullable(Minecraft.getInstance().level);
                level.ifPresent(x -> handleClientSync(x, packet.entityId, packet.nbtData));
            });
        });
        ctx.get().setPacketHandled(true);
    }

    private static void handleClientSync(@NotNull ClientLevel level, int entityId, @NotNull CompoundTag nbtData) {
        Optional<Entity> levelEntity = Optional.ofNullable(level.getEntity(entityId));
        levelEntity.ifPresent(entity -> entity.getCapability(ModCapability.GRAVITY_DATA).ifPresent(
                gravityData -> gravityData.deserializeNBT(nbtData)
        ));
    }
}
