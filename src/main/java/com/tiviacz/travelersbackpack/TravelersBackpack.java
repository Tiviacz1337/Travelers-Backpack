package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.datagen.ModRecipesProvider;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.handlers.*;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.inventory.screen.slot.BackpackSlot;
import com.tiviacz.travelersbackpack.inventory.screen.slot.ToolSlot;
import com.tiviacz.travelersbackpack.util.Reference;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TravelersBackpack implements ModInitializer
{
	public static final String MODID = "travelersbackpack";
	public static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitialize()
	{
		TravelersBackpackConfig.setup();
		ModBlocks.init();
		ModItems.init();
		ModBlockEntityTypes.init();
		ModBlockEntityTypes.initSidedStorages();
		ModScreenHandlerTypes.init();
		ModCrafting.init();
		ModNetwork.initServer();
		ModCommands.registerCommands();
		ModLootConditions.registerLootConditions();
		EntityItemHandler.registerListeners();
		LootHandler.registerListeners();
		TradeOffersHandler.init();
		RightClickHandler.registerListeners();
		SlownessHandler.registerListener();

		ModItems.addBackpacksToList();
		ResourceUtils.createTextureLocations();
		ResourceUtils.createSleepingBagTextureLocations();

		//Fluid Effects
		EffectFluidRegistry.initEffects();

		//Finish

		//Slots
		TravelersBackpackConfig.loadItemsFromConfig(TravelersBackpackConfig.toolSlotsAcceptableItems, ToolSlot.TOOL_SLOTS_ACCEPTABLE_ITEMS);
		TravelersBackpackConfig.loadItemsFromConfig(TravelersBackpackConfig.blacklistedItems, BackpackSlot.BLACKLISTED_ITEMS);

		//Backpack spawn
		TravelersBackpackConfig.loadEntityTypesFromConfig(TravelersBackpackConfig.possibleOverworldEntityTypes, Reference.ALLOWED_TYPE_ENTRIES);
		TravelersBackpackConfig.loadEntityTypesFromConfig(TravelersBackpackConfig.possibleNetherEntityTypes, Reference.ALLOWED_TYPE_ENTRIES);
		TravelersBackpackConfig.loadItemsFromConfig(TravelersBackpackConfig.overworldBackpacks, ModItems.COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES);
		TravelersBackpackConfig.loadItemsFromConfig(TravelersBackpackConfig.netherBackpacks, ModItems.COMPATIBLE_NETHER_BACKPACK_ENTRIES);

		ModRecipesProvider.generate();
	}
}