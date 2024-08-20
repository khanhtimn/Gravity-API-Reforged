package fun.teamti.gravity_api;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(GravityAPI.MOD_ID)
public class GravityAPI
{
    public static final String MOD_ID = "gravity_api";
    public static final Logger LOGGER = LoggerFactory.getLogger("GravityAPI");

    public GravityAPI()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);
    }

}
