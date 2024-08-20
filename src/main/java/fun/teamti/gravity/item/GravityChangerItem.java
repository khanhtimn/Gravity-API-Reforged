package fun.teamti.gravity.item;

import fun.teamti.gravity.GravityMod;
import fun.teamti.gravity.api.GravityAPI;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
    public static final Item GRAVITY_CHANGER_DOWN = new GravityChangerItem(new Properties().stacksTo(1), Direction.DOWN);
    public static final Item GRAVITY_CHANGER_UP = new GravityChangerItem(new Properties().stacksTo(1), Direction.UP);
    public static final Item GRAVITY_CHANGER_NORTH = new GravityChangerItem(new Properties().stacksTo(1), Direction.NORTH);
    public static final Item GRAVITY_CHANGER_SOUTH = new GravityChangerItem(new Properties().stacksTo(1), Direction.SOUTH);
    public static final Item GRAVITY_CHANGER_WEST = new GravityChangerItem(new Properties().stacksTo(1), Direction.WEST);
    public static final Item GRAVITY_CHANGER_EAST = new GravityChangerItem(new Properties().stacksTo(1), Direction.EAST);
    
    public final Direction gravityDirection;
    
    public GravityChangerItem(Properties settings, Direction _gravityDirection) {
        super(settings);
        gravityDirection = _gravityDirection;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if (!world.isClientSide())
            GravityAPI.setBaseGravityDirection(user, gravityDirection);
        return InteractionResultHolder.success(user.getItemInHand(hand));
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        tooltip.add(
            Component.translatable("gravity_changer.gravity_changer.tooltip.0")
                .withStyle(ChatFormatting.GRAY)
        );
        tooltip.add(
            Component.translatable("gravity_changer.gravity_changer.tooltip.1")
                .withStyle(ChatFormatting.GRAY)
        );
    }
    
    public static void init() {
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(GravityMod.MOD_ID, "gravity_changer_down"), GRAVITY_CHANGER_DOWN);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(GravityMod.MOD_ID, "gravity_changer_up"), GRAVITY_CHANGER_UP);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(GravityMod.MOD_ID, "gravity_changer_north"), GRAVITY_CHANGER_NORTH);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(GravityMod.MOD_ID, "gravity_changer_south"), GRAVITY_CHANGER_SOUTH);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(GravityMod.MOD_ID, "gravity_changer_west"), GRAVITY_CHANGER_WEST);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(GravityMod.MOD_ID, "gravity_changer_east"), GRAVITY_CHANGER_EAST);
    }
    
}
