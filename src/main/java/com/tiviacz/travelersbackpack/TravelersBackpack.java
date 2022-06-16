package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.compat.trinkets.TrinketsCompat;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.EntityItemHandler;
import com.tiviacz.travelersbackpack.handlers.LootHandler;
import com.tiviacz.travelersbackpack.handlers.TradeOffersHandler;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.network.ModNetwork;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class TravelersBackpack implements ModInitializer
{
	public static final String MODID = "travelersbackpack";
	private static boolean trinketsLoaded;

	@Override
	public void onInitialize()
	{
		ModBlocks.init();
		ModItems.init();
		ModBlockEntityTypes.init();
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

		//if(enableTrinkets())
		//{
			TrinketsCompat.init();
		//}//
	}

	public static boolean enableTrinkets()
	{
		return trinketsLoaded && TravelersBackpackConfig.trinketsIntegration;
	}
}
