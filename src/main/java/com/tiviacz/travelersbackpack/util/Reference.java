package com.tiviacz.travelersbackpack.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;

public class Reference
{
    //ItemGroup Instance
    //public static final ItemGroup TRAVELERS_BACKPACK_TAB = TravelersBackpackItemGroup.instance;

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
    public static final byte TRAVELERS_BACKPACK_ITEM_SCREEN_ID = 1;
    public static final byte TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID = 2;
    public static final byte TRAVELERS_BACKPACK_TILE_SCREEN_ID = 3;

    //Networking
    public static final byte FROM_KEYBIND = 0;
    public static final byte BACKPACK_GUI = 1;

    public static final int CYCLE_TOOL_ACTION = 0;
    public static final int SWITCH_HOSE_ACTION = 1;
    public static final int TOGGLE_HOSE_TANK = 2;
    public static final int EMPTY_TANK = 3;

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

            "Hay",
            "Melon",
            "Pumpkin",

            "Blaze",

            "Bat",
            "Wolf",
            "Fox",
            "Ocelot",
            "Cow",
            "Pig",
            "Chicken",
            "Villager",
    };
 /*   public static String[] BACKPACK_NAMES = {
            "Standard",
            "Coal",
            "Diamond",
            "Emerald",
            "Gold",
            "Iron",
            "Lapis",
            "Netherite",
            "Quartz",
            "Redstone",
    }; */
  /*  public static String[] BACKPACK_NAMES = {
            "Standard",		//0
    //        "Cow",			//1
    //        "Bat",			//2
    //        "Black",		//3
    //        "Blaze",		//4
    //        "Carrot",		//5
            "Coal",			//6
            "Netherite",
            "Diamond",		//7
            "Emerald",		//8
            "Gold",			//9
            "Iron",			//10
    //        "IronGolem",  	//11
            "Lapis",		//12
            "Redstone",		//13
    //        "Blue",			//14
    //        "Bookshelf",	//15
    //        "Brown",		//16
    //        "Cactus",		//17
    //        "Cake",			//18
    //        "Chest",		//19
    //        "Cookie",		//20
    //        "Cyan",			//21
    //        "Dragon",		//22
    //        "Egg",			//23
    //        "Electric",		//24
    //        "Deluxe",		//25
    //        "Enderman",		//26
    //        "End",			//27
    //        "Chicken",		//28
    //        "Ocelot",		//29
    //        "Ghast",		//30
    //        "Gray",			//31
    //        "Green",		//32
    //        "Haybale",		//33
    //        "Horse",		//34
    //        "Leather",		//35
    //        "LightBlue",	//36
    //        "Glowstone",	//37
    //        "LightGray",	//38
    //        "Lime",			//39
    //        "Magenta",		//40
    //        "MagmaCube",	//41
    //        "Melon",		//42
    //        "BrownMushroom",//43
    //         "RedMushroom",	//44
    //        "Mooshroom",	//45
    //        "Nether",		//46
    //        "Wither",		//47
            "Crying_Obsidian",
    //        "Obsidian",		//48
    //        "Orange",		//49
    //        "Overworld",	//50
    //        "Pigman",		//51
    //        "Pink",			//52
    //        "Pig",			//53
    //        "Pumpkin",		//54
    //        "Purple",		//55
              "Quartz",		//56
    //        "Rainbow",		//57
    //        "Red",			//58
    //        "Sandstone",	//59
    //        "Sheep",		//60
    //        "Silverfish",	//61
    //        "Squid",		//62
    //        "Sunflower",	//63
    //        "Creeper",		//64
    //        "Skeleton",		//65
    //        "WitherSkeleton", //66
    //        "Slime",		//67
    //        "Snow",			//68
    //        "Spider",		//69
    //         "Sponge",		//70
    //        "Villager",		//71
    //        "White",		//72
            "Wolf",			//73
            "Fox",
            "Ocelot"
    //        "Yellow",		//74
    //        "Zombie",		//75
    //        "ModdedNetwork" //76
    };
 /*   public static String[] BACKPACK_NAMES = {
            "Standard",
            "Diamond",
            "Gold",
            "Bat"
    }; */

  /*  public static String[] BACKPACK_NAMES = {
            "Standard",		//0
            "Cow",			//1
            "Bat",			//2
            "Black",		//3
            "Blaze",		//4
            "Carrot",		//5
            "Coal",			//6
            "Diamond",		//7
            "Emerald",		//8
            "Gold",			//9
            "Iron",			//10
            "IronGolem",  	//11
            "Lapis",		//12
            "Redstone",		//13
            "Blue",			//14
            "Bookshelf",	//15
            "Brown",		//16
            "Cactus",		//17
            "Cake",			//18
            "Chest",		//19
            "Cookie",		//20
            "Cyan",			//21
            "Dragon",		//22
            "Egg",			//23
            "Electric",		//24
            "Deluxe",		//25
            "Enderman",		//26
            "End",			//27
            "Chicken",		//28
            "Ocelot",		//29
            "Ghast",		//30
            "Gray",			//31
            "Green",		//32
            "Haybale",		//33
            "Horse",		//34
            "Leather",		//35
            "LightBlue",	//36
            "Glowstone",	//37
            "LightGray",	//38
            "Lime",			//39
            "Magenta",		//40
            "MagmaCube",	//41
            "Melon",		//42
            "BrownMushroom",//43
            "RedMushroom",	//44
            "Mooshroom",	//45
            "Nether",		//46
            "Wither",		//47
            "Obsidian",		//48
            "Orange",		//49
            "Overworld",	//50
            "Pigman",		//51
            "Pink",			//52
            "Pig",			//53
            "Pumpkin",		//54
            "Purple",		//55
            "Quartz",		//56
            "Rainbow",		//57
            "Red",			//58
            "Sandstone",	//59
            "Sheep",		//60
            "Silverfish",	//61
            "Squid",		//62
            "Sunflower",	//63
            "Creeper",		//64
            "Skeleton",		//65
            "WitherSkeleton", //66
            "Slime",		//67
            "Snow",			//68
            "Spider",		//69
            "Sponge",		//70
            "Villager",		//71
            "White",		//72
            "Wolf",			//73
            "Yellow",		//74
            "Zombie",		//75
            "ModdedNetwork" //76
    }; */

    public static String[] validWearingBackpacks =
            {
                    "Bat",  	//Done
                    //"Blaze",
                    "Cactus",
                    "Chicken",
                    "Cow",
                    "Creeper",
                    "Dragon",
                    "Melon",
                    "Mooshroom",
                    "Ocelot",
                    "Pig",
                    "Pigman",
                    "Rainbow",
                    "Slime",
                    "Squid",     //Done
                    "Sunflower", //Done
                    "Wolf"
            };

    public static String[] validRemovalBackpacks =
            {
                    "Bat",
                    "Dragon",
                    "Rainbow",
                    "Squid",
            };
}