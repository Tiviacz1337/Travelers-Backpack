package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.init.ModItems;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

import java.util.Random;

public class TradeOffersHandler
{
    public static void init()
    {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 5, factories -> factories.add(new BackpackVillagerTrade()));
    }

    private static class BackpackVillagerTrade implements TradeOffers.Factory
    {
        @Nullable
        @Override
        public TradeOffer create(Entity entity, Random random)
        {
            return new TradeOffer(new ItemStack(Items.EMERALD, random.nextInt(64) + 48), new ItemStack(ModItems.VILLAGER_TRAVELERS_BACKPACK, 1), 1, 5, 0.5F);
        }
    }
}
