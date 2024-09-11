package fun.teamti.gravity.init;

import fun.teamti.gravity.GravityMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ModItemGroup {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GravityMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> GENERAL = CREATIVE_TAB.register("general",
            () -> new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 1)
                    .icon(ModItem.CREATIVE_TAB_ICON.get()::getDefaultInstance)
                    .title(Component.translatable("itemGroup." + GravityMod.MOD_ID + ".general"))
                    .displayItems((config, output) -> {
                        output.accept(ModBlock.PLATING_BLOCK.get());

                        List<ItemStack> gravityItems = ModItem.ITEMS.getEntries()
                                .stream()
                                .filter(registryObject -> registryObject.get() != ModItem.CREATIVE_TAB_ICON.get().asItem())
                                .map(reg -> new ItemStack(reg.get()))
                                .toList();

                        output.acceptAll(gravityItems);

                        List<ItemStack> gravityPotions = ModPotion.POTIONS.getEntries()
                                .stream()
                                .map(reg -> PotionUtils.setPotion(new ItemStack(Items.POTION), reg.get()))
                                .toList();

                        output.acceptAll(gravityPotions);


                    })
                    .build());
}
