package com.tiviacz.travelersbackpack.handlers;

import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.*;

import java.util.ArrayList;
import java.util.Collection;

public class LootHandler 
{
	private static final LootCondition[] NO_CONDITIONS = new LootCondition[0];
	private static final LootFunction[] NO_FUNCTIONS = new LootFunction[0];

	public static LootEntryItem createItemEntry(ItemStack stack, int weight, int quality, String entryName)
	{
		Collection<LootFunction> functions = new ArrayList<>(3);
		
		if(stack.hasTagCompound())
		{
			functions.add(new SetNBT(NO_CONDITIONS, stack.getTagCompound()));
		}
		if(stack.getCount() > 1)
		{
			functions.add(new SetCount(NO_CONDITIONS, new RandomValueRange(stack.getCount())));
		}
		if(stack.getMetadata() != 0)
	 	{
			functions.add(stack.isItemStackDamageable()
				// SetDamage takes a percentage, not a number
				? new SetDamage(NO_CONDITIONS, new RandomValueRange((float) stack.getItemDamage() / (float) stack.getMaxDamage()))
	            : new SetMetadata(NO_CONDITIONS, new RandomValueRange(stack.getMetadata())));
		}
		return new LootEntryItem(stack.getItem(), weight, quality, functions.toArray(NO_FUNCTIONS), NO_CONDITIONS, entryName);
	}
}
