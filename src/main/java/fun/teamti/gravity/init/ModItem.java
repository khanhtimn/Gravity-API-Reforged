package fun.teamti.gravity.init;

import fun.teamti.gravity.GravityMod;
import fun.teamti.gravity.item.CreativeTabIconItem;
import fun.teamti.gravity.item.GravityAnchorItem;
import fun.teamti.gravity.item.GravityChangerItem;
import fun.teamti.gravity.item.GravityChangerItemAOE;
import fun.teamti.gravity.block.GravityPlatingItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItem {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GravityMod.MOD_ID);

    public static final RegistryObject<Item> CREATIVE_TAB_ICON = ITEMS.register("creative_tab_icon",
            () -> new CreativeTabIconItem(new Item.Properties().stacksTo(1).durability(0)));

    public static final RegistryObject<Item> GRAVITY_CHANGER_DOWN = ITEMS.register("gravity_changer_down",
            () -> new GravityChangerItem(new Item.Properties().stacksTo(1), Direction.DOWN));

    public static final RegistryObject<Item> GRAVITY_CHANGER_UP = ITEMS.register("gravity_changer_up",
            () -> new GravityChangerItem(new Item.Properties().stacksTo(1), Direction.UP));

    public static final RegistryObject<Item> GRAVITY_CHANGER_NORTH = ITEMS.register("gravity_changer_north",
            () -> new GravityChangerItem(new Item.Properties().stacksTo(1), Direction.NORTH));

    public static final RegistryObject<Item> GRAVITY_CHANGER_SOUTH = ITEMS.register("gravity_changer_south",
            () -> new GravityChangerItem(new Item.Properties().stacksTo(1), Direction.SOUTH));

    public static final RegistryObject<Item> GRAVITY_CHANGER_WEST = ITEMS.register("gravity_changer_west",
            () -> new GravityChangerItem(new Item.Properties().stacksTo(1), Direction.WEST));

    public static final RegistryObject<Item> GRAVITY_CHANGER_EAST = ITEMS.register("gravity_changer_east",
            () -> new GravityChangerItem(new Item.Properties().stacksTo(1), Direction.EAST));

    public static final RegistryObject<Item> GRAVITY_CHANGER_DOWN_AOE = ITEMS.register("gravity_changer_down_aoe",
            () -> new GravityChangerItemAOE(new Item.Properties().stacksTo(1), Direction.DOWN));

    public static final RegistryObject<Item> GRAVITY_CHANGER_UP_AOE = ITEMS.register("gravity_changer_up_aoe",
            () -> new GravityChangerItemAOE(new Item.Properties().stacksTo(1), Direction.UP));

    public static final RegistryObject<Item> GRAVITY_CHANGER_NORTH_AOE = ITEMS.register("gravity_changer_north_aoe",
            () -> new GravityChangerItemAOE(new Item.Properties().stacksTo(1), Direction.NORTH));

    public static final RegistryObject<Item> GRAVITY_CHANGER_SOUTH_AOE = ITEMS.register("gravity_changer_south_aoe",
            () -> new GravityChangerItemAOE(new Item.Properties().stacksTo(1), Direction.SOUTH));

    public static final RegistryObject<Item> GRAVITY_CHANGER_WEST_AOE = ITEMS.register("gravity_changer_west_aoe",
            () -> new GravityChangerItemAOE(new Item.Properties().stacksTo(1), Direction.WEST));

    public static final RegistryObject<Item> GRAVITY_CHANGER_EAST_AOE = ITEMS.register("gravity_changer_east_aoe",
            () -> new GravityChangerItemAOE(new Item.Properties().stacksTo(1), Direction.EAST));

    public static final RegistryObject<Item> GRAVITY_ANCHOR_DOWN = ITEMS.register("gravity_anchor_down",
            () -> new GravityAnchorItem(new Item.Properties().stacksTo(1), Direction.DOWN));

    public static final RegistryObject<Item> GRAVITY_ANCHOR_UP = ITEMS.register("gravity_anchor_up",
            () -> new GravityAnchorItem(new Item.Properties().stacksTo(1), Direction.UP));

    public static final RegistryObject<Item> GRAVITY_ANCHOR_NORTH = ITEMS.register("gravity_anchor_north",
            () -> new GravityAnchorItem(new Item.Properties().stacksTo(1), Direction.NORTH));

    public static final RegistryObject<Item> GRAVITY_ANCHOR_SOUTH = ITEMS.register("gravity_anchor_south",
            () -> new GravityAnchorItem(new Item.Properties().stacksTo(1), Direction.SOUTH));

    public static final RegistryObject<Item> GRAVITY_ANCHOR_WEST = ITEMS.register("gravity_anchor_west",
            () -> new GravityAnchorItem(new Item.Properties().stacksTo(1), Direction.WEST));

    public static final RegistryObject<Item> GRAVITY_ANCHOR_EAST = ITEMS.register("gravity_anchor_east",
            () -> new GravityAnchorItem(new Item.Properties().stacksTo(1), Direction.EAST));

    public static final RegistryObject<Item> PLATING_BLOCK_ITEM = ITEMS.register("plating",
            () -> new GravityPlatingItem(ModBlock.PLATING_BLOCK.get(), new Item.Properties()));

}
