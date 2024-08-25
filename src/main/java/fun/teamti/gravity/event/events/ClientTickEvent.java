package fun.teamti.gravity.event.events;

import fun.teamti.gravity.GravityMod;
import fun.teamti.gravity.util.GCUtil;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GravityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientTickEvent {

    @SubscribeEvent
    public static void onPLayerStartTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            assert event.player != null;
            if (GravityMod.displayPreviewWarning) {
                GravityMod.displayPreviewWarning = false;
                event.player.sendSystemMessage(
                        Component.translatable("gravity_changer.preview").append(
                                GCUtil.getLinkText(GravityMod.ISSUE_LINK)
                        )
                );
            }
        }
    }
}
