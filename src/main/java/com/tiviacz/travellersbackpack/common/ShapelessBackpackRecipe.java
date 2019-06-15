package com.tiviacz.travellersbackpack.common;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.tiviacz.travellersbackpack.items.ItemTravellersBackpack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ShapelessBackpackRecipe extends ShapelessOreRecipe 
{
	public ShapelessBackpackRecipe(@Nullable final ResourceLocation group, final NonNullList<Ingredient> input, final ItemStack result) 
	{
		super(group, input, result);
	}
	
	private ItemStack damageShears(final ItemStack stack) 
	{
		final EntityPlayer craftingPlayer = ForgeHooks.getCraftingPlayer();
		
		if(stack.attemptDamageItem(1, craftingPlayer.world.rand, craftingPlayer instanceof EntityPlayerMP ? (EntityPlayerMP) craftingPlayer : null)) 
		{
			ForgeEventFactory.onPlayerDestroyItem(craftingPlayer, stack, null);
			return ItemStack.EMPTY;
		}

		return stack;
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
	public NonNullList<ItemStack> getRemainingItems(final InventoryCrafting inventoryCrafting) 
	{
		final NonNullList<ItemStack> remainingItems = NonNullList.withSize(inventoryCrafting.getSizeInventory(), ItemStack.EMPTY);

		for(int i = 0; i < remainingItems.size(); ++i) 
		{
			final ItemStack itemstack = inventoryCrafting.getStackInSlot(i);

			if(!itemstack.isEmpty() && itemstack.getItem() instanceof ItemShears) 
			{
				remainingItems.set(i, damageShears(itemstack.copy()));
			} 
			else 
			{
				remainingItems.set(i, ForgeHooks.getContainerItem(itemstack));
			}
		}

		return remainingItems;
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
			final NonNullList<Ingredient> ingredients = parseShapeless(context, json);
			final ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

			return new ShapelessBackpackRecipe(group.isEmpty() ? null : new ResourceLocation(group), ingredients, result);
		}
		
		public static NonNullList<Ingredient> parseShapeless(final JsonContext context, final JsonObject json) 
		{
			final NonNullList<Ingredient> ingredients = NonNullList.create();
			for(final JsonElement element : JsonUtils.getJsonArray(json, "ingredients"))
				ingredients.add(CraftingHelper.getIngredient(element, context));

			if(ingredients.isEmpty())
				throw new JsonParseException("No ingredients for shapeless recipe");

			return ingredients;
		}
	}
}
