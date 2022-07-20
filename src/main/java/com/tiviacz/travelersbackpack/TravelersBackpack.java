package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.compat.trinkets.TrinketsCompat;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.handlers.EntityItemHandler;
import com.tiviacz.travelersbackpack.handlers.LootHandler;
import com.tiviacz.travelersbackpack.handlers.TradeOffersHandler;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.init.ModNetwork;
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

	@Override
	public void onInitialize()
	{
		ModBlocks.init();
		ModItems.init();
		ModBlockEntityTypes.init();
		ModBlockEntityTypes.initSidedFluidStorage();
		ModScreenHandlerTypes.init();
		ModCrafting.init();
		ModNetwork.initServer();
		TravelersBackpackConfig.setup();
		EntityItemHandler.registerListeners();
		LootHandler.registerListeners();
		TradeOffersHandler.init();

		ModItems.addBackpacksToList();
		ResourceUtils.createTextureLocations();

		trinketsLoaded = FabricLoader.getInstance().isModLoaded("trinkets");

		if(trinketsLoaded)	TrinketsCompat.init();

		EffectFluidRegistry.initEffects();
	}

	public static boolean enableTrinkets()
	{
		return trinketsLoaded && TravelersBackpackConfig.trinketsIntegration;
	}
}
