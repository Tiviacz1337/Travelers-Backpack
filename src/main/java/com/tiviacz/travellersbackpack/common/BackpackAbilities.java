package com.tiviacz.travellersbackpack.common;

import com.tiviacz.travellersbackpack.capability.CapabilityUtils;
import com.tiviacz.travellersbackpack.gui.inventory.InventoryTravellersBackpack;
import com.tiviacz.travellersbackpack.tileentity.TileEntityTravellersBackpack;
import com.tiviacz.travellersbackpack.util.Reference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class BackpackAbilities 
{
	public static BackpackAbilities backpackAbilities = new BackpackAbilities();
	public static BackpackRemovals backpackRemovals = new BackpackRemovals();
    
	public static boolean hasAbility(String colorName)
    {
        for(String valid : Reference.validWearingBackpacks)
        {
            if(valid.equals(colorName))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean hasRemoval(String colorName)
    {
        for(String valid : Reference.validRemovalBackpacks)
        {
            if(valid.equals(colorName))
            {
                return true;
            }
        }
        return false;
    }
    
    public void executeAbility(EntityPlayer player, World world, Object object)
    {
        if(object instanceof ItemStack)
        {
            String color = Reference.BACKPACK_NAMES[((ItemStack)object).getMetadata()];
            
            try {
				this.getClass().getMethod("item" + color, EntityPlayer.class, World.class, ItemStack.class).invoke(backpackAbilities, player, world, object);
			} catch(Exception e) 
            {
				e.printStackTrace();
			}
        }

        if(object instanceof TileEntityTravellersBackpack)
        {
            String color = ((TileEntityTravellersBackpack)object).getColor();
            
            try {
                this.getClass().getMethod("tile" + color, World.class, TileEntityTravellersBackpack.class).invoke(backpackAbilities, world, object);
            } catch(Exception e)
            {
            	e.printStackTrace();
            }
        }

    }

    public void executeRemoval(EntityPlayer player, World world, ItemStack stack)
    {
        String color = Reference.BACKPACK_NAMES[stack.getMetadata()];
        
        try {
			backpackRemovals.getClass().getMethod("item" + color, EntityPlayer.class, World.class, ItemStack.class).invoke(backpackRemovals, player, world, stack);
		} catch(Exception e) 
        {
			e.printStackTrace();
		}
    }
    
    private boolean isUnderRain(EntityPlayer player)
    {
        return (player.world.canSeeSky(new BlockPos(MathHelper.floor(player.posX), MathHelper.floor(player.posY), MathHelper.floor(player.posZ)))
        || player.world.canSeeSky(new BlockPos(MathHelper.floor(player.posX), MathHelper.floor(player.posY + player.height), MathHelper.floor(player.posZ))))
        && player.world.isRaining();
    }
    
    //Actual Abilties
    
	public void itemBat(EntityPlayer player, World world, ItemStack stack)
	{
		PotionEffect effect = player.getActivePotionEffect(MobEffects.NIGHT_VISION);

    	if(effect == null)
    	{
    		player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 210, 0, false, false));
    	}
    	
    	else if(effect.getDuration() <= 210)
    	{
    		player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 210, 0, false, false));
    	}
    }
	
	public void itemCactus(EntityPlayer player, World world, ItemStack stack)
    {
        InventoryTravellersBackpack inv = CapabilityUtils.getBackpackInv(player);
        FluidTank leftTank = inv.getLeftTank();
        FluidTank rightTank = inv.getRightTank();
        
        int drops = 0;
        
        if(player.isInWater())
        {
            drops += 10;
        }
        
        if(isUnderRain(player))
        {
            drops += 1;
        }
        
        FluidStack water = new FluidStack(FluidRegistry.WATER, drops);

        if(inv.getLastTime() <= 0 && drops > 0 && (leftTank.canFillFluidType(water)) || (rightTank.canFillFluidType(water)))
        {
            inv.setLastTime(5);
            
            if(leftTank.canFillFluidType(water))
            {
            	leftTank.fill(water, true);
            }
                
            if(rightTank.canFillFluidType(water))
            {
            	rightTank.fill(water, true);
            }
            
            if(player instanceof EntityPlayerMP)
            {
            	//TravellersBackpack.NETWORK.sendTo(new SyncGuiPacket(stack), (EntityPlayerMP)player);
            }
        }
        
        else
        {
            inv.setLastTime(inv.getLastTime() - 1);
        }
        
        inv.markTankDirty();
    }
	
	public void itemSquid(EntityPlayer player, World world, ItemStack stack)
    {
        if(player.isInWater())
        {
            player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 210, 0, false, false));
            itemBat(player, world, stack);
        }
        else
        {
            backpackRemovals.itemSquid(player, world, stack);
        }
    }
	
	public void itemSunflower(EntityPlayer player, World world, ItemStack stack)
    {
        InventoryTravellersBackpack inv = new InventoryTravellersBackpack(stack, player);

        if(inv.getLastTime() <= 0)
        {
            if(player.canEat(false) && world.isDaytime() && world.canBlockSeeSky(new BlockPos(MathHelper.floor(player.posX), MathHelper.floor(player.posY + 1), MathHelper.floor(player.posZ)))) 
            {
                player.getFoodStats().addStats(2, 0.2f);
                inv.setLastTime(120 * 20);
            }
        }
        
        else if(inv.getLastTime() > 0)
        {
            inv.setLastTime(inv.getLastTime() - 1);
        }
        
        inv.markDirty();
    }
}