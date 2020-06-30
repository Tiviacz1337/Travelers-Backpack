package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.init.ModItems;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.entity.passive.EntityVillager.PriceInfo;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.Random;

public class VillagerTrades
{
	public static VillagerRegistry.VillagerProfession priest = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation("minecraft:priest"));
	public static VillagerRegistry.VillagerProfession librarian = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation("minecraft:librarian"));
	
	public static void addTrades()
	{
		Random rand = new Random();
		priest.getCareer(priest.getRandomCareer(rand)).addTrade(1, new TradeList());
		librarian.getCareer(librarian.getRandomCareer(rand)).addTrade(1, new TradeList());
	}
	
	public static class TradeList implements ITradeList
	{
		private PriceInfo price = new PriceInfo(48, 64);
		
		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) 
		{
			ItemStack emeralds = new ItemStack(Items.EMERALD, price.getPrice(random));
			recipeList.add(new MerchantRecipe(emeralds, new ItemStack(ModItems.TRAVELERS_BACKPACK, 1, 71)));
		}
	}
}