package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

public class TradeOffersHandler {
    public static void init() {
        if(TravelersBackpackConfig.COMMON.enableVillagerTrade.get()) {
            TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 3, factories -> factories.add(
                    (trader, entity, randomSource) -> new MerchantOffer(new ItemCost(Items.EMERALD, randomSource.nextInt(64) + 48),
                            new ItemStack(ModItems.VILLAGER_TRAVELERS_BACKPACK, 1), 1, 50, 0.5F)));
        }
    }
}