package fun.teamti.gravity;

import fun.teamti.gravity.command.GravityCommand;
import fun.teamti.gravity.init.*;
import fun.teamti.gravity.util.ClientUtil;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(GravityMod.MOD_ID)
public class GravityMod
{
    public static final String MOD_ID = "gravity_api";
    public static final Logger LOGGER = LoggerFactory.getLogger("GravityAPI");
    public static final String ISSUE_LINK = "https://github.com/khanhtimn/Gravity-API-Reforged/issues";
    public static boolean displayPreviewWarning = true;

    public GravityMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.CLIENT, ModConfig.SPEC, "gravity_api.toml");
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(ClientUtil::showWarningOnJoin);
        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> GravityCommand.register(event.getDispatcher()));

        ModArgumentType.ARGUMENT_TYPES.register(modEventBus);
        ModItem.ITEMS.register(modEventBus);
        ModBlock.BLOCKS.register(modEventBus);
        ModBlock.BLOCK_ENTITIES.register(modEventBus);

        //GravityDirectionMobEffect.init();
        //GravityInvertMobEffect.init();
        //GravityStrengthMobEffect.init();
        //GravityPotion.init();

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        ModNetwork.init();
    }

}
