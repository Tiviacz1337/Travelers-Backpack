package com.tiviacz.travelersbackpackold;

import com.tiviacz.travelersbackpack.init.ModBlockEntityTypes;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpackold.compat.accessories.TravelersBackpackAccessory;
import com.tiviacz.travelersbackpackold.compat.craftingtweaks.TravelersBackpackCraftingGridProvider;
import com.tiviacz.travelersbackpackold.compat.trinkets.TravelersBackpackTrinket;
import com.tiviacz.travelersbackpackold.compat.universalgraves.UniversalGravesCompat;
import com.tiviacz.travelersbackpackold.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpackold.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpackold.handlers.*;
import com.tiviacz.travelersbackpackneo.init.*;
import com.tiviacz.travelersbackpackold.items.TravelersBackpackItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TravelersBackpack implements ModInitializer {
	public static final String MODID = "travelersbackpack";
	public static final Logger LOGGER = LogManager.getLogger();

	public static boolean accessoriesLoaded;
	public static boolean trinketsLoaded;
	public static boolean craftingTweaksLoaded;

	public static boolean toughasnailsLoaded;
	public static boolean comfortsLoaded;
	public static boolean universalGravesLoaded;

	@Override
	public void onInitialize() {
		TravelersBackpackConfig.register();
		ModItemGroups.registerItemGroup();
		ModBlocks.init();
		ModItems.init();
		ModBlockEntityTypes.init();
		ModBlockEntityTypes.initSidedStorage();
		ModDataComponents.init();
		ModScreenHandlerTypes.init();
		ModRecipeSerializers.init();
		ModNetwork.initServer();
		ModCommands.registerCommands();
		EntityItemHandler.registerListeners();
		LootHandler.registerListeners();
		TradeOffersHandler.init();
		RightClickHandler.registerListeners();
		SleepHandler.registerListener();

		ModItemGroups.addItemGroup();

		TravelersBackpackItem.registerCauldronBehavior();

		accessoriesLoaded = FabricLoader.getInstance().isModLoaded("accessories");
		trinketsLoaded = FabricLoader.getInstance().isModLoaded("trinkets");
		craftingTweaksLoaded = FabricLoader.getInstance().isModLoaded("craftingtweaks");

		if(craftingTweaksLoaded) new TravelersBackpackCraftingGridProvider();

		if(accessoriesLoaded) TravelersBackpackAccessory.init();
		if(trinketsLoaded && !accessoriesLoaded) TravelersBackpackTrinket.init();

		toughasnailsLoaded = FabricLoader.getInstance().isModLoaded("toughasnails");
		comfortsLoaded = FabricLoader.getInstance().isModLoaded("comforts");

		universalGravesLoaded = FabricLoader.getInstance().isModLoaded("universal-graves");
		if(universalGravesLoaded) UniversalGravesCompat.register();

		EffectFluidRegistry.initEffects();
	}

	public static boolean enableIntegration()
	{
		return enableTrinkets() || enableAccessories();
	}

	public static boolean enableAccessories()
	{
		return accessoriesLoaded && TravelersBackpackConfig.getConfig().backpackSettings.accessoriesIntegration;
	}

	public static boolean enableTrinkets()
	{
		return trinketsLoaded && !enableAccessories() && TravelersBackpackConfig.getConfig().backpackSettings.trinketsIntegration;
	}

	public static boolean isAnyGraveModInstalled()
	{
		return TravelersBackpack.universalGravesLoaded;
	}
}