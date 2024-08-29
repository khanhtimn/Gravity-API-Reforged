package fun.teamti.gravity.block;

import fun.teamti.gravity.init.ModBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GravityPlatingItem extends BlockItem {
    
    public GravityPlatingItem(Block block, Properties properties) {
        super(block, properties);
    }
    
    public static @Nullable GravityPlatingBlockEntity.SideData getSideData(@Nullable CompoundTag tag) {
        if (tag == null) {
            return null;
        }
        
        if (tag.contains("sideData")) {
            CompoundTag t = tag.getCompound("sideData");
            return GravityPlatingBlockEntity.SideData.fromTag(t);
        }
        return null;
    }
    
    public static void setSideData(CompoundTag tag, @Nullable GravityPlatingBlockEntity.SideData sideData) {
        if (sideData != null) {
            tag.put("sideData", sideData.toTag());
        }
        else {
            tag.remove("sideData");
        }
    }
    
    public static ItemStack createStack(@Nullable GravityPlatingBlockEntity.SideData sideData) {
        ItemStack itemStack = new ItemStack(ModBlock.PLATING_BLOCK.get().asItem());
        setSideData(itemStack.getOrCreateTag(), sideData);
        return itemStack;
    }
    
    @Override
    public @NotNull Component getName(ItemStack stack) {
        GravityPlatingBlockEntity.SideData sideData = getSideData(stack.getTag());
        if (sideData != null) {
            return Component.translatable(
                "gravity_api.plating.item_name",
                sideData.level, GravityPlatingBlockEntity.getForceText(sideData.isAttracting)
            );
        }
        
        return super.getName(stack);
    }
    
    @Override
    public @NotNull InteractionResult place(@NotNull BlockPlaceContext context) {
        InteractionResult result = super.place(context);
        
        Level level = context.getLevel();
        ItemStack itemStack = context.getItemInHand();
        BlockPos clickedPos = context.getClickedPos();
        
        if (level.isClientSide()) {
            return result;
        }
        
        GravityPlatingBlockEntity.SideData sideData = getSideData(itemStack.getOrCreateTag());
        
        if (sideData != null) {
            BlockEntity blockEntity = level.getBlockEntity(clickedPos);
            if (blockEntity instanceof GravityPlatingBlockEntity be) {
                be.onPlacing(context.getClickedFace().getOpposite(), sideData);
            }
        }
        
        return result;
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack itemStack, Level world, List<Component> tooltip, @NotNull TooltipFlag tooltipContext) {
        tooltip.add(Component.translatable("gravity_api.plating.tooltip.0").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("gravity_api.plating.tooltip.1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("gravity_api.plating.tooltip.2").withStyle(ChatFormatting.GRAY));
    }
}