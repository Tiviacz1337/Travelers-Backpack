package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.compat.craftingtweaks.TravelersBackpackCraftingGridProvider;
import com.tiviacz.travelersbackpack.compat.trinkets.TrinketsCompat;
import com.tiviacz.travelersbackpack.compat.universalgraves.UniversalGravesCompat;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.handlers.*;
import com.tiviacz.travelersbackpack.init.*;
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

	public static boolean comfortsLoaded;
	public static boolean universalGravesLoaded;

	@Override
	public void onInitialize()
	{
		TravelersBackpackConfig.register();
		ModItemGroups.registerItemGroup();
		ModBlocks.init();
		ModItems.init();
		ModBlockEntityTypes.init();
		ModBlockEntityTypes.initSidedStorage();
		ModScreenHandlerTypes.init();
		ModRecipeSerializers.init();
		ModNetwork.initServer();
		ModCommands.registerCommands();
		ModLootConditions.registerLootConditions();
		EntityItemHandler.registerListeners();
		LootHandler.registerListeners();
		TradeOffersHandler.init();
		RightClickHandler.registerListeners();
		SleepHandler.registerListener();

		ModItems.addBackpacksToList();
		ResourceUtils.createTextureLocations();
		ResourceUtils.createSleepingBagTextureLocations();
		ModItemGroups.addItemGroup();

		trinketsLoaded = FabricLoader.getInstance().isModLoaded("trinkets");
		craftingTweaksLoaded = FabricLoader.getInstance().isModLoaded("craftingtweaks");

		if(craftingTweaksLoaded) new TravelersBackpackCraftingGridProvider();

		if(trinketsLoaded) TrinketsCompat.init();

		comfortsLoaded = FabricLoader.getInstance().isModLoaded("comforts");

		universalGravesLoaded = FabricLoader.getInstance().isModLoaded("universal-graves");
		if(universalGravesLoaded && !enableTrinkets()) UniversalGravesCompat.register();

		EffectFluidRegistry.initEffects();
	}

	public static boolean enableTrinkets()
	{
		return trinketsLoaded && TravelersBackpackConfig.getConfig().backpackSettings.trinketsIntegration;
	}

	public static boolean isAnyGraveModInstalled()
	{
		return TravelersBackpack.universalGravesLoaded;
	}
}