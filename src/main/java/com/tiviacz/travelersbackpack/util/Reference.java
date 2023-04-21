package com.tiviacz.travelersbackpack.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reference
{
    //Constants
    public static final int INVENTORY_SIZE = 45;
    public static final int CRAFTING_GRID_SIZE = 9;
    public static final int COMBINED_INVENTORY_SIZE = INVENTORY_SIZE + CRAFTING_GRID_SIZE;
    public static final long BUCKET = FluidConstants.BUCKET;
    public static final int POTION = 250;
    public static final long BASIC_TANK_CAPACITY = BUCKET * 4;

    //Inventory Special Slots
    public static final int END_OF_INVENTORY = INVENTORY_SIZE - 7;  //47
    public static final int TOOL_UPPER = END_OF_INVENTORY + 1;      //48
    public static final int TOOL_LOWER = TOOL_UPPER + 1;			//49
    public static final int BUCKET_IN_LEFT = TOOL_LOWER + 1;		//50
    public static final int BUCKET_OUT_LEFT = BUCKET_IN_LEFT + 1;	//51
    public static final int BUCKET_IN_RIGHT = BUCKET_OUT_LEFT + 1;	//52
    public static final int BUCKET_OUT_RIGHT = BUCKET_IN_RIGHT + 1;	//53

    //Screen IDs
    public static final byte NO_SCREEN_ID = 0;
    public static final byte ITEM_SCREEN_ID = 1;
    public static final byte WEARABLE_SCREEN_ID = 2;
    public static final byte BLOCK_ENTITY_SCREEN_ID = 3;

    public static final byte SWAP_TOOL = 0;
    public static final byte SWITCH_HOSE_MODE = 1;
    public static final byte TOGGLE_HOSE_TANK = 2;
    public static final byte EMPTY_TANK = 3;
    public static final byte OPEN_SCREEN = 4;

    //Keybindings
    public static final String CATEGORY = "key.travelersbackpack.category";
    public static final String INVENTORY = "key.travelersbackpack.inventory";
    public static final String TOGGLE_TANK = "key.travelersbackpack.toggle_tank";
    public static final String CYCLE_TOOL = "key.travelersbackpack.cycle_tool";

    //Translation Keys
    public static final String NO_BACKPACK = "action.travelersbackpack.unequip_nobackpack";
    public static final String NO_SPACE = "action.travelersbackpack.unequip_nospace";
    public static final String OTHER_BACKPACK = "action.travelersbackpack.equip_otherbackpack";
    public static final String FAIL = "action.travelersbackpack.equip_fail";
    public static final String DEPLOY = "action.travelersbackpack.deploy_sleeping_bag";

    public static String[] BACKPACK_NAMES = {
            "Standard",
            "Netherite",
            "Diamond",
            "Gold",
            "Emerald",
            "Iron",
            "Lapis",
            "Redstone",
            "Coal",

            "Quartz",
            "Bookshelf",
            "End",
            "Nether",
            "Sandstone",
            "Snow",
            "Sponge",

            "Cake",

            "Cactus",
            "Hay",
            "Melon",
            "Pumpkin",

            "Creeper",
            "Dragon",
            "Enderman",
            "Blaze",
            "Ghast",
            "Magma_Cube",
            "Skeleton",
            "Spider",
            "Wither",

            "Bat",
            "Bee",
            "Wolf",
            "Fox",
            "Ocelot",
            "Horse",
            "Cow",
            "Pig",
            "Sheep",
            "Chicken",
            "Squid",
            "Villager",
            "Iron_Golem"
    };

    public static final List<EntityType> COMPATIBLE_TYPE_ENTRIES = Arrays.asList(
            EntityType.ENDERMAN,
            EntityType.PIGLIN,
            EntityType.SKELETON,
            EntityType.WITHER_SKELETON,
            EntityType.ZOMBIE
    );

    public static final List<EntityType> ALLOWED_TYPE_ENTRIES = new ArrayList<>();
}