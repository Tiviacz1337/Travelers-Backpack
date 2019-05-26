package com.tiviacz.travellersbackpack.common;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.tiviacz.travellersbackpack.items.ItemTravellersBackpack;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ShapedBackpackRecipe extends ShapedOreRecipe 
{
	public ShapedBackpackRecipe(@Nullable final ResourceLocation group, final ItemStack result, final CraftingHelper.ShapedPrimer primer) 
	{
		super(group, result, primer);
	}

	@Override
	public ItemStack getCraftingResult(final InventoryCrafting inv) 
	{
		final ItemStack output = super.getCraftingResult(inv);

		if(!output.isEmpty()) 
		{
			for(int i = 0; i < inv.getSizeInventory(); i++) 
			{
				final ItemStack ingredient = inv.getStackInSlot(i);

				if(!ingredient.isEmpty() && ingredient.getItem() instanceof ItemTravellersBackpack) 
				{
					NBTTagCompound tag = ingredient.getTagCompound();
					output.setTagCompound(tag);
					break;
				}
			}
		}
		return output;
	}

	@Override
	public String getGroup() 
	{
		return group == null ? "" : group.toString();
	}

	public static class Factory implements IRecipeFactory 
	{
		@Override
		public IRecipe parse(final JsonContext context, final JsonObject json) 
		{
			final String group = JsonUtils.getString(json, "group", "");
			final CraftingHelper.ShapedPrimer primer = parseShaped(context, json);
			final ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

			return new ShapedBackpackRecipe(group.isEmpty() ? null : new ResourceLocation(group), result, primer);
		}
	}
	
	public static CraftingHelper.ShapedPrimer parseShaped(final JsonContext context, final JsonObject json) 
	{
		final Map<Character, Ingredient> ingredientMap = Maps.newHashMap();
		
		for(final Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "key").entrySet()) 
		{
			if(entry.getKey().length() != 1)
				throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
			if(" ".equals(entry.getKey()))
				throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");

			ingredientMap.put(entry.getKey().toCharArray()[0], CraftingHelper.getIngredient(entry.getValue(), context));
		}

		ingredientMap.put(' ', Ingredient.EMPTY);

		final JsonArray patternJ = JsonUtils.getJsonArray(json, "pattern");

		if(patternJ.size() == 0)
			throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");

		final String[] pattern = new String[patternJ.size()];
		
		for(int x = 0; x < pattern.length; ++x) 
		{
			final String line = JsonUtils.getString(patternJ.get(x), "pattern[" + x + "]");
			
			if(x > 0 && pattern[0].length() != line.length())
				throw new JsonSyntaxException("Invalid pattern: each row must  be the same width");
			
			pattern[x] = line;
		}

		final CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
		primer.width = pattern[0].length();
		primer.height = pattern.length;
		primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
		primer.input = NonNullList.withSize(primer.width * primer.height, Ingredient.EMPTY);

		final Set<Character> keys = Sets.newHashSet(ingredientMap.keySet());
		keys.remove(' ');

		int index = 0;
		
		for(final String line : pattern) 
		{
			for(final char chr : line.toCharArray()) 
			{
				final Ingredient ing = ingredientMap.get(chr);
				
				if(ing == null)
					throw new JsonSyntaxException("Pattern references symbol '" + chr + "' but it's not defined in the key");
			
				primer.input.set(index++, ing);
				keys.remove(chr);
			}
		}

		if(!keys.isEmpty())
			throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + keys);

		return primer;
	}
}