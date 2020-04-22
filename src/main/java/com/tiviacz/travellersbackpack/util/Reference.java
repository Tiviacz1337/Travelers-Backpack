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
    public static final int SYNC_GUI_PACKET_ID = 8;
    public static final int PARTICLE_PACKET_ID = 9;
    
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
    };
    
    public static String[] validWearingBackpacks = {
    		"Bat",  	//Done
    		//"Blaze",
    		"Cactus",  	//Done 
    		"Chicken",  //Done
    	//	"Cow",
    		"Creeper",  //Done
    		"Dragon",	//Done
    		"Emerald",  //Done
    	//	"Melon",
    	//	"Mooshroom",
    	//	"Ocelot",
    		"Pig",		//Done
    		"Pigman",	//Done
    		"Rainbow",	//Done
    		"Slime",	//Done
    		"Squid",     //Done
    		"Sunflower", //Done
    		"Wolf"		//Done
	};
    
    public static String[] validTileBackpacks = {
    		"Cactus"
    };

	public static String[] validRemovalBackpacks = {
			"Bat",
			"Dragon",
			"Pigman",
			"Rainbow",
			"Squid",
			"Wolf"
 	};
}