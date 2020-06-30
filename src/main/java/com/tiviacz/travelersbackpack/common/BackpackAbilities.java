package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.gui.inventory.InventoryTravelersBackpack;
import com.tiviacz.travelersbackpack.network.client.ParticlesPacket;
import com.tiviacz.travelersbackpack.tileentity.TileEntityTravelersBackpack;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import java.util.Iterator;
import java.util.List;

public class BackpackAbilities 
{
	public static BackpackAbilities backpackAbilities = new BackpackAbilities();
	public static BackpackRemovals backpackRemovals = new BackpackRemovals();
    
	public static boolean hasItemAbility(String colorName)
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
	
	public static boolean hasTileAbility(String colorName)
	{
		for(String valid : Reference.validTileBackpacks)
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
    
    public void executeItemAbility(EntityPlayer player, World world, ItemStack stack)
    {
    		String color = Reference.BACKPACK_NAMES[stack.getMetadata()];
    		
    		if(color.equals("Bat"))
    		{
    			itemBat(player, world, stack);
    		}
    		
    		if(color.equals("Cactus"))
    		{
    			itemCactus(player, world, stack);
    		}
    		
    		if(color.equals("Chicken"))
    		{
    			itemChicken(player, world, stack);
    		}
    		
    		if(color.equals("Creeper"))
    		{
    			itemCreeper(player, world, stack);
    		}
    		
    		if(color.equals("Dragon"))
    		{
    			itemDragon(player, world, stack);
    		}
    		
    		if(color.equals("Emerald"))
    		{
    			TravelersBackpack.NETWORK.sendToAll(new ParticlesPacket((byte)2, player.getEntityId()));
    		}
    		
    		if(color.equals("Pig"))
    		{
    			itemPig(player, world, stack);
    		}
    		
    		if(color.equals("Pigman"))
    		{
    			itemPigman(player, world, stack);
    		}
    		
    		if(color.equals("Rainbow"))
    		{
    			itemRainbow(player, world, stack);
    		}
    		
    		if(color.equals("Slime"))
    		{
    			itemSlime(player, world, stack);
    		}
    		
    		if(color.equals("Squid"))
    		{
    			itemSquid(player, world, stack);
    		}
    		
    		if(color.equals("Sunflower"))
    		{
    			itemSunflower(player, world, stack);
    		}
    		
    		if(color.equals("Wolf"))
    		{
    			itemWolf(player, world, stack);
    		}
    }
    
    public void executeTileAbility(EntityPlayer player, World world, TileEntityTravelersBackpack tile)
    {
    	String color = tile.getColor();
    	
    	if(color.equals("Cactus"))
    	{
    		tileCactus(player, world, tile);
    	}
    }

	public void executeRemoval(EntityPlayer player, World world, ItemStack stack)
    {
    	String color = Reference.BACKPACK_NAMES[(stack).getMetadata()];
    	
    	if(color.equals("Bat"))
    	{
    		BackpackAbilities.backpackRemovals.itemBat(player, world, stack);
    	}
    	
    	if(color.equals("Dragon"))
    	{
    		BackpackAbilities.backpackRemovals.itemDragon(player, world, stack);
    	}
    	
    	if(color.equals("Pigman"))
    	{
    		BackpackAbilities.backpackRemovals.itemPigman(player, world, stack);
    	}
    	
    	if(color.equals("Rainbow"))
    	{
    		BackpackAbilities.backpackRemovals.itemRainbow(player, world, stack);
    	}
    	
    	if(color.equals("Squid"))
    	{
    		BackpackAbilities.backpackRemovals.itemSquid(player, world, stack);
    	}
    	
    	if(color.equals("Wolf"))
    	{
    		BackpackAbilities.backpackRemovals.itemWolf(player, world, stack);
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
    	
    	else if(effect.getDuration() == 209)
    	{
    		player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 210, 0, false, false));
    	}
    }
	
	public void itemCactus(EntityPlayer player, World world, ItemStack stack)
    {
        InventoryTravelersBackpack inv = CapabilityUtils.getBackpackInv(player);
        FluidTank leftTank = inv.getLeftTank();
        FluidTank rightTank = inv.getRightTank();
        
        int drops = 0;
        
        if(player.isInWater())
        {
            drops += 2;
        }
        
        if(isUnderRain(player))
        {
            drops += 1;
        }
        
        FluidStack water = new FluidStack(FluidRegistry.WATER, drops);

        if(inv.getLastTime() <= 0 && drops > 0)
        {
            inv.setLastTime(5);
            
            if(leftTank.canFillFluidType(water))
            {
            	if(!world.isRemote)
            	{
            		leftTank.fill(water, true);
            	}
            }
                
            if(rightTank.canFillFluidType(water))
            {
            	if(!world.isRemote)
            	{
            		rightTank.fill(water, true);
            	}
            }
            
            inv.markDirty();
        }
        else
        {
            inv.setLastTime(inv.getLastTime() - 1);
            inv.markTimeDirty();
        }
    }
	
	public void itemChicken(EntityPlayer player, World world, ItemStack stack)
    {
        InventoryTravelersBackpack inv = CapabilityUtils.getBackpackInv(player);
        
        int eggTime = inv.getLastTime() - 1;
        
        if(eggTime <= 0)
        {
        	world.playSound(null, player.getPosition(), SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.AMBIENT, 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3F + 1.0F);
            
            if(!world.isRemote) 
            {
            	player.dropItem(Items.EGG, 1);
            }
            
            eggTime = (200 + 10 * world.rand.nextInt(10)) * 20;
        }
        
        inv.setLastTime(eggTime);
        inv.markTimeDirty();
    }
	
	public void itemCreeper(EntityPlayer player, World world, ItemStack stack)
	{
        InventoryTravelersBackpack inv = CapabilityUtils.getBackpackInv(player);
        
        int pssstTime = inv.getLastTime();
        
        if(pssstTime > 0)
        {
        	pssstTime = inv.getLastTime() - 1;
        }

        if(pssstTime <= 0)
        {
            if(player.isSneaking())
            {
                List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(player, 
                		new AxisAlignedBB(player.posX, player.posY, player.posZ, player.posX + 1.0D, player.posY + 1.0D, player.posZ + 1.0D)
                		.expand(5.0D, 1.0D, 5.0D));
                
                if(entities.isEmpty())
                {
                    pssstTime -= 1;
                    return;
                }

                for(Entity entity : entities)
                {
                    if(entity instanceof EntityPlayer)
                    {
                        if(player.getDistance(entity) <= 3)
                        {
                            world.playSound(null, player.getPosition(), SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.AMBIENT, 1.2F, 0.5F);
                            pssstTime = 120*20;
                        }
                    }
                }
            }
        }
        inv.setLastTime(pssstTime);
        inv.markTimeDirty();
    }
	
	public void itemDragon(EntityPlayer player, World world, ItemStack stack)
	{
		itemPigman(player, world, stack);
        itemSquid(player, world, stack);
        
        PotionEffect effectRegeneration = player.getActivePotionEffect(MobEffects.REGENERATION);
        PotionEffect effectStrenght = player.getActivePotionEffect(MobEffects.STRENGTH);

    	if(effectRegeneration == null)
    	{
    		player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 420, 0, false, false));
    	} 
    	
    	else if(effectRegeneration.getDuration() <= 210)
    	{
    		player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 420, 0, false, false));
    	}
    	
    	if(effectStrenght == null)
    	{
    		player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 210, 0, false, false));
    	}
    	
    	else if(effectStrenght.getDuration() <= 210)
    	{
    		player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 210, 0, false, false));
    	}
	}
	
	public void itemPig(EntityPlayer player, World world, ItemStack stack)
    {
        InventoryTravelersBackpack inv = CapabilityUtils.getBackpackInv(player);
        
        int oinkTime = inv.getLastTime() - 1;
        
        if(oinkTime <= 0)
        {
            world.playSound(null, player.getPosition(), SoundEvents.ENTITY_PIG_AMBIENT, SoundCategory.AMBIENT, 0.8F, 1.0F);
            oinkTime = world.rand.nextInt(61) * 20;
        }
        
        inv.setLastTime(oinkTime);
        inv.markTimeDirty();
    }
	
	public void itemPigman(EntityPlayer player, World world, ItemStack stack)
    {
		PotionEffect effectFireResistance = player.getActivePotionEffect(MobEffects.FIRE_RESISTANCE);

		if(effectFireResistance == null)
    	{
    		player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 210, 0, false, false));
    	}
    	
    	else if(effectFireResistance.getDuration() <= 210)
    	{
    		player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 210, 0, false, false));
    	}
    }
	
	public void itemRainbow(EntityPlayer player, World world, ItemStack stack)
	{
		InventoryTravelersBackpack inv = CapabilityUtils.getBackpackInv(player);
        
		if(player.isSprinting())
		{
			TravelersBackpack.NETWORK.sendToAll(new ParticlesPacket((byte)0, player.getEntityId()));
		}
		
		player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 210, 1, false, false));
		player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 210, 1, false, false));

        inv.markTimeDirty();
	}
	
	public void itemSlime(EntityPlayer player, World world, ItemStack stack)
    {
        if(player.onGround)
        {
            if((player.moveForward == 0 && player.moveStrafing == 0) && (Math.abs(player.moveForward) < 3 && Math.abs(player.moveStrafing) < 3))
            {
                player.addVelocity(player.motionX *= 0.828, 0, player.motionZ *= 0.828);
            }
            
            if(player.isSprinting())
            {
                InventoryTravelersBackpack inv = CapabilityUtils.getBackpackInv(player);
                
                int slimeTime = inv.getLastTime() > 0 ? inv.getLastTime() - 1 : 5;
                
                if(slimeTime <= 0)
                {
                    if(!world.isRemote)
                    {
                        TravelersBackpack.NETWORK.sendToAll(new ParticlesPacket((byte)1, player.getEntityId()));
                    }
                    
                    world.playSound(null, player.getPosition(), SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.AMBIENT, 0.6F, 1.0F);
                    slimeTime = 5;
                }
                inv.setLastTime(slimeTime);
                inv.markTimeDirty();
            }
        }
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
        InventoryTravelersBackpack inv = new InventoryTravelersBackpack(stack, player);

        if(inv.getLastTime() <= 0)
        {
            if(world.isDaytime() && world.canBlockSeeSky(new BlockPos(MathHelper.floor(player.posX), MathHelper.floor(player.posY + 1), MathHelper.floor(player.posZ)))) 
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
	
	public void itemWolf(EntityPlayer player, World world, ItemStack stack)
    {
        InventoryTravelersBackpack inv = CapabilityUtils.getBackpackInv(player);
        
        if(!world.isDaytime() && world.getCurrentMoonPhaseFactorBody() == 1.0F)
        {
            player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 21, 2, false, false));
        }
        
        int lastCheckTime = inv.getLastTime() - 1;

        if(lastCheckTime <= 0)
        {
            lastCheckTime = 20;
            List<EntityWolf> wolves = world.getEntitiesWithinAABB(EntityWolf.class, new AxisAlignedBB(player.posX - 7.0D, player.posY - 7.0D, player.posZ - 7.0D, player.posX + 7.0D, player.posY + 7.0D, player.posZ + 7.0D));//.expand(16.0D, 4.0D, 16.0D));
            
            if(wolves.isEmpty())
            {
            	return;
            }

            for(EntityWolf wolf : wolves)
            {
                if(wolf.isAngry() && wolf.getAttackTarget() == player)
                {
                    wolf.setAngry(false);
                    wolf.setAttackTarget(null);
                    wolf.setRevengeTarget(null);
                    
                    Iterator<?> i2 = wolf.targetTasks.taskEntries.iterator();
                    
                    while(i2.hasNext())
                    {
                        ((EntityAITasks.EntityAITaskEntry)i2.next()).action.resetTask();
                        world.playSound(null, player.getPosition(), SoundEvents.ENTITY_WOLF_HOWL, SoundCategory.HOSTILE, 0.7F, 0.5F);
                    }
                }
            }
        }
        inv.setLastTime(lastCheckTime);
        inv.markTimeDirty();
    }
	
	//Tile Abilities
	
	public void tileCactus(EntityPlayer player, World world, TileEntityTravelersBackpack tile)
	{
		if(world.isRaining() && world.canSeeSky(tile.getPos()))
        {
            int dropTime = tile.getLastTime() - 1;
           
            if(dropTime <= 0)
            {
            	if(tile.getLeftTank().canFillFluidType(new FluidStack(FluidRegistry.WATER, 2)))
            	{
            		tile.getLeftTank().fill(new FluidStack(FluidRegistry.WATER, 2), true);
            	}
            	
            	if(tile.getRightTank().canFillFluidType(new FluidStack(FluidRegistry.WATER, 2)))
            	{
            		tile.getRightTank().fill(new FluidStack(FluidRegistry.WATER, 2), true);
            	}
            	
                dropTime = 5;
            }
            tile.setLastTime(dropTime);
        }

	}
}