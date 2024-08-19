package fun.teamti.gravity;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(GravityAPI.MOD_ID)
public class GravityAPI
{
    public static final String MOD_ID = "gravity_api";
    private static final Logger LOGGER = LogUtils.getLogger();

    public GravityAPI()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);
    }

}
