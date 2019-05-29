package com.tiviacz.travellersbackpack.handlers;

import com.tiviacz.travellersbackpack.TravellersBackpack;

import net.minecraftforge.common.config.Config;

@Config(modid = TravellersBackpack.MODID)
public class ConfigHandler 
{
	public static boolean backpackDeathPlace = true;
	
	public static boolean enableToolCycling = true;
	
	public static boolean toolSlotsAcceptSwords = false;
	
	public static boolean renderTools = true;
	
	public static boolean enableOverlay = true;
	
	public static boolean oldGuiTankRender = false;
	
	public static boolean enableBackpackBlockWearable = true;
	
//	public static boolean autoItemPickUp = true;
	
//	public static boolean canInteractWithOtherPlayersBackpack = true;
}