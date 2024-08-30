package fun.teamti.gravity.init;

import fun.teamti.gravity.GravityMod;
import fun.teamti.gravity.network.GravityDataSyncPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    private static int nextID() {
        return ID++;
    }

    public static void init() {
        INSTANCE = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(GravityMod.MOD_ID, "networking"),
                () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(nextID(), GravityDataSyncPacket.class,
                GravityDataSyncPacket::toBytes,
                GravityDataSyncPacket::new,
                GravityDataSyncPacket::handle);
    }
}
