package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.compat.craftingtweaks.TravelersBackpackCraftingGridProvider;
import com.tiviacz.travelersbackpack.compat.trinkets.TrinketsCompat;
import com.tiviacz.travelersbackpack.compat.universalgraves.UniversalGravesCompat;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.handlers.EntityItemHandler;
import com.tiviacz.travelersbackpack.handlers.LootHandler;
import com.tiviacz.travelersbackpack.handlers.RightClickHandler;
import com.tiviacz.travelersbackpack.handlers.TradeOffersHandler;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.inventory.screen.slot.BackpackSlot;
import com.tiviacz.travelersbackpack.inventory.screen.slot.ToolSlot;
import com.tiviacz.travelersbackpack.util.Reference;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TravelersBackpack implements ModInitializer
{
	public static final String MODID = "travelersbackpack";
	public static final Logger LOGGER = LogManager.getLogger();
	private static boolean trinketsLoaded;
	public static boolean craftingTweaksLoaded;

	public static boolean universalGravesLoaded;

	//public static boolean dehydrationModLoaded;

	@Override
	public void onInitialize()
	{
		TravelersBackpackConfig.setup();
		ModBlocks.init();
		ModItems.init();
		ModBlockEntityTypes.init();
		ModBlockEntityTypes.initSidedStorage();
		ModScreenHandlerTypes.init();
		ModCrafting.init();
		ModNetwork.initServer();
		ModCommands.registerCommands();
		ModLootConditions.registerLootConditions();
		EntityItemHandler.registerListeners();
		LootHandler.registerListeners();
		TradeOffersHandler.init();
		RightClickHandler.registerListeners();

		ModItems.addBackpacksToList();
		ResourceUtils.createTextureLocations();
		ResourceUtils.createSleepingBagTextureLocations();

		trinketsLoaded = FabricLoader.getInstance().isModLoaded("trinkets");
		if(trinketsLoaded) TrinketsCompat.init();

		craftingTweaksLoaded = FabricLoader.getInstance().isModLoaded("craftingtweaks");
		if(craftingTweaksLoaded) new TravelersBackpackCraftingGridProvider();

		universalGravesLoaded = FabricLoader.getInstance().isModLoaded("universal-graves");
		if(universalGravesLoaded && !enableTrinkets()) UniversalGravesCompat.register();

		//dehydrationModLoaded = FabricLoader.getInstance().isModLoaded("dehydration");
		//if(dehydrationModLoaded)

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

		//Abilities
		TravelersBackpackConfig.loadItemsFromConfig(TravelersBackpackConfig.allowedAbilities, BackpackAbilities.ALLOWED_ABILITIES);
	}

	public static boolean enableTrinkets()
	{
		return trinketsLoaded && TravelersBackpackConfig.trinketsIntegration;
	}

	public static boolean isAnyGraveModInstalled()
	{
		return TravelersBackpack.universalGravesLoaded;
	}
}