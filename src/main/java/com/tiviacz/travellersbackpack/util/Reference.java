package com.tiviacz.travellersbackpack.util;

import net.minecraftforge.fluids.Fluid;

public class Reference 
{
	//Constants
	public static final int INVENTORY_SIZE = 54;
	public static final int CRAFTING_GRID_SIZE = 9;
	public static final int BUCKET = Fluid.BUCKET_VOLUME;
	public static final int POTION = 250;
	public static final int BASIC_TANK_CAPACITY = BUCKET * 4;
	
	//Inventory Special Slots
    public static final int END_OF_INVENTORY = INVENTORY_SIZE - 7;  //47
    public static final int TOOL_UPPER = END_OF_INVENTORY + 1;      //48
    public static final int TOOL_LOWER = TOOL_UPPER + 1;			//49
    public static final int BUCKET_IN_LEFT = TOOL_LOWER + 1;		//50
    public static final int BUCKET_OUT_LEFT = BUCKET_IN_LEFT + 1;	//51
    public static final int BUCKET_IN_RIGHT = BUCKET_OUT_LEFT + 1;	//52
    public static final int BUCKET_OUT_RIGHT = BUCKET_IN_RIGHT + 1;	//53

    public static final int TRAVELLERS_BACKPACK_ITEM_GUI_ID = 1;
    public static final int TRAVELLERS_BACKPACK_TILE_GUI_ID = 2;
    public static final int TRAVELLERS_BACKPACK_WEARABLE_GUI_ID = 3;
    
    //Networking
    public static final int SLEEPING_BAG_PACKET_ID = 1;
    public static final int GUI_PACKET_ID = 2;
    public static final int EQUIP_BACKPACK_PACKET_ID = 3;
    public static final int UNEQUIP_BACKPACK_PACKET_ID = 4;
    public static final int SYNC_BACKPACK_CAPABILITY_ID = 5;
    public static final int SYNC_BACKPACK_CAPABILITY_MP_ID = 6;
    public static final int CYCLE_TOOL_PACKET_ID = 7;
    
    //Keybindings
    public static final String CATEGORY = "keys.travellersBackpack.category";
    public static final String INVENTORY = "keys.travellersBackpack.inventory";
    public static final String TOGGLE_TANK = "keys.travellersBackpack.toggle_tank";
    
    public static String[] BACKPACK_NAMES = {
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
         /*   "Pigman",
            "Pink",
            "Pig",
            "Pumpkin",
            "Purple",
            "Quartz",
            "Rainbow",
            "Red",
            "Sandstone",
            "Sheep",
            "Silverfish",
            "Squid",
            "Sunflower",
            "Creeper",
            "Skeleton",
            "WitherSkeleton",
            "Slime",
            "Snow",
            "Spider",
            "Sponge",
            "Villager",
            "White",
            "Wolf",
            "Yellow",
            "Zombie" */
    };
}