package fun.teamti.gravity.item;

import fun.teamti.gravity.init.ModCapability;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Based on code from AmethystGravity (by CyborgCabbage)
 */
public class GravityAnchorItem extends Item {

    private final Direction gravityDirection;

    public GravityAnchorItem(Properties properties, Direction pDirection) {
        super(properties);
        gravityDirection = pDirection;
    }

    @Mod.EventBusSubscriber
    private static class GravityAnchorChange {
        @SubscribeEvent
        public static void onHoldingAnchorItem(LivingEvent.LivingTickEvent event) {
            Entity entity = event.getEntity();
            if (entity instanceof Player player) {
                player.getCapability(ModCapability.GRAVITY_DATA).ifPresent(gravityData -> {
                    for (ItemStack handSlot : player.getHandSlots()) {
                        Item item = handSlot.getItem();
                        if (item instanceof GravityAnchorItem anchorItem) {
                            gravityData.applyGravityDirectionEffect(
                                    anchorItem.gravityDirection,
                                    null, 1000000
                            );
                        }
                    }
                });
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, Level world, List<Component> tooltip, @NotNull TooltipFlag tooltipContext) {
        tooltip.add(
                Component.translatable("gravity_api.gravity_anchor.tooltip.0")
                        .withStyle(ChatFormatting.GRAY)
        );

        tooltip.add(
                Component.translatable("gravity_api.gravity_anchor.tooltip.1")
                        .withStyle(ChatFormatting.GRAY)
        );
    }
}
