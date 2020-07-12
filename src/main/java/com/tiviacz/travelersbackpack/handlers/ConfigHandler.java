package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

@Config(modid = TravelersBackpack.MODID)
public class ConfigHandler 
{
	public static final Server server = new Server();
	
	public static class Server
	{
		@Comment("Enables warning about modid change")
		@Name("Enable warning")
		public boolean enableWarning = true;

		@Name("Backpack's Abilities")
		public boolean enableBackpackAbilities = true;
		
		@Comment("Places backpack at place where player died")
		@Name("Place Backpack on Death")
		public boolean backpackDeathPlace = true;
		
		@Name("Tool Slots Accept Swords")
		public boolean toolSlotsAcceptSwords = false;
		
		@Comment("Enables wearing backpack directly from ground")
		@Name("Backpack Block Wearable")
		public boolean enableBackpackBlockWearable = true;
		
		@Comment("Enables backpacks spawning in loot chests")
		@Name("Loot")
		public boolean enableLoot = true;
		
		@Comment("Enables button in backpack gui, which allows to empty tank")
		@Name("Empty Tank Button")
		public boolean enableEmptyTankButton = true;
		
		@Name("Sleeping Bag Spawn Point")
		public boolean enableSleepingBagSpawnPoint = false;
		
		@Name("Disable Crafting in Backpack inventory")
		public boolean disableCrafting = false;
		
		@RequiresMcRestart
		@Name("Configures Backpack Tanks Capacity")
		@RangeInt(min = 250)
		public int tanksCapacity = 4000;
	}
	
	public static final Client client = new Client();
	
	public static class Client
	{
		@Comment("Enables auto message with backpack coords after player dies")
		@Name("Backpack Coords Message")
		public boolean enableBackpackCoordsMessage = true;

		@Comment("Enables tool cycling via shift + scroll combination, while backpack is worn")
		@Name("Tool Cycling")
		public boolean enableToolCycling = true;
		
		@Comment("Enables tip, how to obtain a backpack, if there's no crafting recipe for it")
		@Name("Obtain Tips")
		public boolean obtainTips = true;
		
		@Comment("Disabling this option may improve performance")
		@Name("Backpack Item Fluid Renderer")
		public boolean enableBackpackItemFluidRenderer = true;
		
		@Comment("Render tools in tool slots on the backpack, while worn")
		@Name("Render Tools")
		public boolean renderTools = true;
		
		@Comment("Render backpack while player wears elytra")
		@Name("Render Backpack with Elytra")
		public boolean renderBackpackWithElytra = true;
		
		@Name("Old Gui Tank Render")
		public boolean oldGuiTankRender = false;
		
		@Name("Overlay Position")
		public final Overlay overlay = new Overlay();
		
		public static class Overlay
		{
			@Comment("Enables tanks and tool slots overlay, while backpack is worn")
			@Name("Overlay")
			public boolean enableOverlay = true;
			
			@Comment("Offsets to left side")
			@Name("Offset X")
			public int offsetX = 20;
			
			@Comment("Offsets to up")
			@Name("Offset Y")
			public int offsetY = 30;
		}
	}
}