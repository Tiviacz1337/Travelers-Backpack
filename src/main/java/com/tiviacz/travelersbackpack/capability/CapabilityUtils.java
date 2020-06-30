package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.gui.inventory.InventoryTravelersBackpack;
import com.tiviacz.travelersbackpack.handlers.ConfigHandler;
import com.tiviacz.travelersbackpack.items.ItemTravelersBackpack;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class CapabilityUtils 
{
	public static ITravelersBackpack getCapability(EntityPlayer player)
	{
		return player.getCapability(TravelersBackpackCapability.TRAVELERS_BACKPACK_CAPABILITY, TravelersBackpackCapability.DEFAULT_FACING);
	}
	
    public static boolean isWearingBackpack(EntityPlayer player)
    {
    	ITravelersBackpack cap = getCapability(player);
    	ItemStack backpack = cap.getWearable();
    	
        return cap.hasWearable() && backpack.getItem() instanceof ItemTravelersBackpack;
    }

    public static ItemStack getWearingBackpack(EntityPlayer player)
    {
    	ITravelersBackpack cap = getCapability(player);
    	ItemStack backpack = cap.getWearable();
    	
        return isWearingBackpack(player) ? backpack : ItemStack.EMPTY;
    }

	public static void synchronise(EntityPlayer player)
	{
		getCapability(player).synchronise();
	}

	public static void synchroniseToOthers(EntityPlayer player)
	{
		getCapability(player).synchroniseToOthers(player);
	}
    
    public static void equipBackpack(EntityPlayer player, ItemStack stack)
    {
    	ITravelersBackpack cap = getCapability(player);
    	
    	if(!player.world.isRemote)
    	{
    		if(!cap.hasWearable())
    		{
    			cap.setWearable(stack);
    			player.world.playSound(null, player.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.2F) * 0.7F);
    		
    			//Sync
    			synchronise(player);
    			synchroniseToOthers(player);
    		}
    	}
    }
    
    public static void onEquippedUpdate(World world, EntityPlayer player, ItemStack stack)
	{
		if(!ConfigHandler.server.enableBackpackAbilities)
		{
			return;
		}
		
		if(world == null || player == null || stack == null)
		{
			return;
		}
		
		if(BackpackAbilities.hasItemAbility(Reference.BACKPACK_NAMES[stack.getMetadata()]))
		{
			BackpackAbilities.backpackAbilities.executeItemAbility(player, world, stack);
		}
	}
    
    public static void onUnequipped(World world, EntityPlayer player, ItemStack stack)
    {
    	if(BackpackAbilities.hasRemoval(Reference.BACKPACK_NAMES[stack.getMetadata()]))
    	{
    		BackpackAbilities.backpackAbilities.executeRemoval(player, world, stack);
    	}
    }

    public static InventoryTravelersBackpack getBackpackInv(EntityPlayer player)
    {
        ItemStack wearable = CapabilityUtils.getWearingBackpack(player);
        
        if(wearable.getItem() instanceof ItemTravelersBackpack)
        {
        	return new InventoryTravelersBackpack(wearable, player);
        }
        return null;
    }
}