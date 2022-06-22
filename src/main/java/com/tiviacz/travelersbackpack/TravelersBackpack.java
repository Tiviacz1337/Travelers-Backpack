package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.handlers.EntityItemHandler;
import com.tiviacz.travelersbackpack.handlers.LootHandler;
import com.tiviacz.travelersbackpack.handlers.TradeOffersHandler;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.network.ModNetwork;
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

		//Fluid Effects
		EffectFluidRegistry.initEffects();
	}
}
