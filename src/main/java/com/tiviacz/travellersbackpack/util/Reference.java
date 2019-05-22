package com.tiviacz.travellersbackpack.util;

import net.minecraftforge.fluids.Fluid;

public class Reference 
{
	//Constants
	public static final int INVENTORY_SIZE = 45; 
	public static final int BUCKET = Fluid.BUCKET_VOLUME;
	public static final int BASIC_TANK_CAPACITY = BUCKET * 4;
	
	//Inventory Special Slots
    public static final int END_OF_INVENTORY = INVENTORY_SIZE - 7;  //38
    public static final int TOOL_UPPER = END_OF_INVENTORY + 1;      //39
    public static final int TOOL_LOWER = TOOL_UPPER + 1;			//40
    public static final int BUCKET_IN_LEFT = TOOL_LOWER + 1;		//41
    public static final int BUCKET_OUT_LEFT = BUCKET_IN_LEFT + 1;	//42
    public static final int BUCKET_IN_RIGHT = BUCKET_OUT_LEFT + 1;	//43
    public static final int BUCKET_OUT_RIGHT = BUCKET_IN_RIGHT + 1;	//44
    
    //GUIs
    public static final int TRAVELLERS_BACKPACK_GUI_ID = 1;
    public static final int TRAVELLERS_BACKPACK_TILE_GUI_ID = 2;
    public static final int TRAVELLERS_BACKPACK_WEARABLE_GUI_ID = 3;
    
    //Networking
    public static final int SLEEPING_BAG_PACKET_ID = 1;
    public static final int GUI_PACKET_ID = 2;
    public static final int EQUIP_BACKPACK_PACKET_ID = 3;
    public static final int UNEQUIP_BACKPACK_PACKET_ID = 4;
    public static final int SYNC_PLAYER_DATA_PACKET_ID = 5;
    public static final int CYCLE_TOOL_PACKET_ID = 6;
	public static final int UPDATE_INVENTORY_PACKET_ID = 7;
    
    //Keybindings
    public static final String CATEGORY = "keys.travellersBackpack.category";
    public static final String INVENTORY = "keys.travellersBackpack.inventory";
    public static final String TOGGLE_TANK = "keys.travellersBackpack.toggle_tank";
}