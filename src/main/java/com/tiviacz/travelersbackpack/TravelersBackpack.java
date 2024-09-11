package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.compat.accessories.TravelersBackpackAccessory;
import com.tiviacz.travelersbackpack.compat.craftingtweaks.TravelersBackpackCraftingGridProvider;
import com.tiviacz.travelersbackpack.compat.trinkets.TravelersBackpackTrinket;
import com.tiviacz.travelersbackpack.compat.universalgraves.UniversalGravesCompat;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.handlers.*;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
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
		ModComponentTypes.init();
		ModScreenHandlerTypes.init();
		ModRecipeSerializers.init();
		ModNetwork.initServer();
		ModCommands.registerCommands();
		EntityItemHandler.registerListeners();
		LootHandler.registerListeners();
		TradeOffersHandler.init();
		RightClickHandler.registerListeners();
		SleepHandler.registerListener();

		ResourceUtils.createSleepingBagTextureLocations();
		ModItemGroups.addItemGroup();

		TravelersBackpackItem.registerCauldronBehavior();

		accessoriesLoaded = FabricLoader.getInstance().isModLoaded("accessories");
		trinketsLoaded = FabricLoader.getInstance().isModLoaded("trinkets");
		craftingTweaksLoaded = FabricLoader.getInstance().isModLoaded("craftingtweaks");

		if(craftingTweaksLoaded) new TravelersBackpackCraftingGridProvider();

		if(accessoriesLoaded) TravelersBackpackAccessory.init();
		if(trinketsLoaded && !accessoriesLoaded) TravelersBackpackTrinket.init();

		comfortsLoaded = FabricLoader.getInstance().isModLoaded("comforts");

		universalGravesLoaded = FabricLoader.getInstance().isModLoaded("universal-graves");
		if(universalGravesLoaded && !enableIntegration()) UniversalGravesCompat.register();

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