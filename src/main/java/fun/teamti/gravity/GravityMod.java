package fun.teamti.gravity;

import fun.teamti.gravity.command.DirectionArgumentType;
import fun.teamti.gravity.command.GravityCommand;
import fun.teamti.gravity.command.LocalDirectionArgumentType;
import fun.teamti.gravity.config.GravityAPIConfig;
import fun.teamti.gravity.item.GravityAnchorItem;
import fun.teamti.gravity.item.GravityChangerItem;
import fun.teamti.gravity.item.GravityChangerItemAOE;
import fun.teamti.gravity.mob.effect.GravityDirectionMobEffect;
import fun.teamti.gravity.mob.effect.GravityInvertMobEffect;
import fun.teamti.gravity.mob.effect.GravityPotion;
import fun.teamti.gravity.mob.effect.GravityStrengthMobEffect;
import fun.teamti.gravity.plating.GravityPlatingBlock;
import fun.teamti.gravity.plating.GravityPlatingBlockEntity;
import fun.teamti.gravity.plating.GravityPlatingItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(GravityMod.MOD_ID)
public class GravityMod
{
    public static final String MOD_ID = "gravity_api";
    public static final Logger LOGGER = LoggerFactory.getLogger("GravityAPI");

    public GravityMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, GravityAPIConfig.SPEC, "gravity_api.toml");
        //GravityChangerItem.init();
        //GravityChangerItemAOE.init();
        //GravityAnchorItem.init();

        //GravityDirectionMobEffect.init();
        //GravityInvertMobEffect.init();
        //GravityStrengthMobEffect.init();
        //GravityPotion.init();

        //GravityPlatingBlock.init();
        //GravityPlatingItem.init();
        //GravityPlatingBlockEntity.init();

        DirectionArgumentType.init();
        LocalDirectionArgumentType.init();

        MinecraftForge.EVENT_BUS.register(this);
    }

}
