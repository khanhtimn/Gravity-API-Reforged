package fun.teamti.gravity.util;

import fun.teamti.gravity.GravityMod;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
public class ClientUtil {

    public static MutableComponent getLinkText(String link) {
        return Component.literal(link).withStyle(
                style -> style.withClickEvent(new ClickEvent(
                        ClickEvent.Action.OPEN_URL, link
                )).withUnderlined(true)
        );
    }

    public static MutableComponent getDirectionText(Direction gravityDirection) {
        return Component.translatable("direction." + gravityDirection.getName());
    }

    public static boolean isClientPlayer(Entity entity) {
        if (entity.level().isClientSide()) {
            return entity instanceof LocalPlayer;
        }
        return false;
    }

    public static boolean isRemotePlayer(Entity entity) {
        if (entity.level().isClientSide()) {
            return entity instanceof RemotePlayer;
        }
        return false;
    }

    public static void showWarningOnJoin(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (GravityMod.displayPreviewWarning) {
                GravityMod.displayPreviewWarning = false;
                event.player.sendSystemMessage(
                        Component.translatable("gravity_api.preview").append(
                                getLinkText(GravityMod.ISSUE_LINK)
                        )
                );
            }
        }
    }

}
