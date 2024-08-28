package fun.teamti.gravity.item;

import fun.teamti.gravity.api.GravityAPI;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GravityChangerItem extends Item {

    public final Direction gravityDirection;

    public GravityChangerItem(Properties properties, Direction pDirection) {
        super(properties);
        gravityDirection = pDirection;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, @NotNull Player user, @NotNull InteractionHand hand) {
        if (!world.isClientSide()) {
            GravityAPI.setBaseGravityDirection(user, gravityDirection);
        }
        return InteractionResultHolder.success(user.getItemInHand(hand));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        tooltip.add(
                Component.translatable("gravity_api.gravity_changer.tooltip.0")
                        .withStyle(ChatFormatting.GRAY)
        );
        tooltip.add(
                Component.translatable("gravity_api.gravity_changer.tooltip.1")
                        .withStyle(ChatFormatting.GRAY)
        );
    }
}
