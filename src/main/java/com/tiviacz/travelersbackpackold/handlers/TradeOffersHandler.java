package com.tiviacz.travelersbackpackold.handlers;

import com.tiviacz.travelersbackpackold.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpackneo.init.ModItems;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;

public class TradeOffersHandler
{
    public static void init()
    {
        if(TravelersBackpackConfig.getConfig().world.enableVillagerTrade)
        {
            TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 3, factories -> factories.add(
                    (trader, random) -> new TradeOffer(new TradedItem(Items.EMERALD, random.nextInt(64) + 48),
                            new ItemStack(ModItems.VILLAGER_TRAVELERS_BACKPACK.asItem(), 1), 1, 50, 0.5F))); //#TODO make on neo
        }
    }
}