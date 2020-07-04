package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.api.fluids.effects.FluidEffect;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.fluids.FluidEffectRegistry;
import com.tiviacz.travelersbackpack.gui.inventory.InventoryTravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.wrappers.BlockLiquidWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemHose extends ItemBase
{
	public ItemHose(String name) 
	{
		super(name);
		setMaxStackSize(1);
		
		this.addPropertyOverride(new ResourceLocation("mode"), new IItemPropertyGetter()
        {
			@SideOnly(Side.CLIENT)
	        public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
	        {
	            NBTTagCompound nbt = stack.getTagCompound();
	            
	            float j;
	            
	            if(nbt == null)
	            {
	            	return j = 0;
	            }
	            else
	            {
	            	j = nbt.getInteger("Mode");
		            return j; 
	            }
	        }
        });
	}
	
	@Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
		if(getHoseMode(stack) == 3)
		{
			return EnumAction.DRINK;
		}
		return EnumAction.NONE;
    }
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack)
    {
        return 24;
    }
	
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand)
    {
		if(CapabilityUtils.isWearingBackpack(playerIn))
		{
			InventoryTravelersBackpack inv = CapabilityUtils.getBackpackInv(playerIn);
		
			if(getHoseMode(stack) == 1)
			{
				if(target instanceof EntityCow)
				{
					FluidTank tank = this.getSelectedFluidTank(stack, inv);

					FluidStack milk = null;

					if(FluidRegistry.isFluidRegistered("milk"))
					{
						milk = new FluidStack(FluidRegistry.getFluid("milk"), Reference.BUCKET);
					}

					if(milk == null) return false;
					
					if(tank.getFluid() == null || (tank.getFluidAmount() > 0 && tank.getFluidAmount() + Reference.BUCKET <= tank.getCapacity() && tank.getFluid().isFluidEqual(milk)))
					{
						tank.fill(milk, true);
						playerIn.world.playSound(null, playerIn.getPosition(), SoundEvents.ENTITY_COW_MILK, SoundCategory.BLOCKS, 1.0F, 1.0F);
						inv.markTankDirty();
						
						((EntityCow)target).faceEntity(playerIn, 0.1F, 0.1F);
						
						return true;
					}
				}
			}
		}
        return false;
    }
	
	@Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {	
		if(CapabilityUtils.isWearingBackpack(player))
		{
			ItemStack stack = player.getHeldItemMainhand();
			InventoryTravelersBackpack inv = CapabilityUtils.getBackpackInv(player);
			FluidTank tank = this.getSelectedFluidTank(stack, inv);
			IFluidHandler fluidHandler = FluidUtil.getFluidHandler(worldIn, pos, facing);
			
			if(fluidHandler != null)
			{
				int fluidAmount;
				
				if(getHoseMode(stack) == 1)
				{
					if(tank.canFill() && fluidHandler.getTankProperties()[0].canDrain())
					{
						FluidStack fluid = fluidHandler.getTankProperties()[0].getContents();
						fluidAmount = tank.fill(fluidHandler.drain(Reference.BUCKET, false), false);
						
						if(tank.getFluid() == null || (tank.getFluidAmount() > 0 && tank.getFluidAmount() + fluidAmount <= tank.getCapacity() && tank.getFluid().isFluidEqual(fluid)))
						{
							if(fluidAmount > 0)
							{
								tank.fill(fluidHandler.drain(Reference.BUCKET, true), true);
								worldIn.playSound(null, pos, fluid.getFluid().getFillSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
								inv.markTankDirty();
								return EnumActionResult.SUCCESS;
							}
						}
					}
				}
				
				if(getHoseMode(stack) == 2)
				{
					if(tank.canDrain() && fluidHandler.getTankProperties()[0].canFill())
					{
						FluidStack fluid = tank.getFluid();
						fluidAmount = fluidHandler.fill(tank.drain(Reference.BUCKET, false), false);
						IFluidTankProperties fluidHandlerProp = fluidHandler.getTankProperties()[0];
						
						if((fluidHandlerProp.getContents() == null) || (fluidHandlerProp.getContents().amount + fluidAmount <= fluidHandlerProp.getCapacity() && fluidHandlerProp.getContents().isFluidEqual(fluid)))
						{
							if(fluidAmount > 0)
							{
								fluidHandler.fill(tank.drain(Reference.BUCKET, true), true);
								worldIn.playSound(null, pos, fluid.getFluid().getEmptySound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
								inv.markTankDirty();
								return EnumActionResult.SUCCESS;
							}
						}
					}
				}
			} 
		}
		return EnumActionResult.FAIL;
    } 
	
	public static int getHoseMode(ItemStack stack)
	{
		if(stack.getTagCompound() != null)
		{
			return stack.getTagCompound().getInteger("Mode");
			//1 = Suck mode
			//2 = Spill mode
			//3 = Drink mode
		}
		return 0;
	}
	
	public static int getHoseTank(ItemStack stack)
	{
		if(stack.getTagCompound() != null)
		{
			return stack.getTagCompound().getInteger("Tank");
			//1 = Left tank
			//2 = Right tank
		}
		return 0;
	}
	
	public FluidTank getSelectedFluidTank(ItemStack stack, InventoryTravelersBackpack inv)
	{
		return getHoseTank(stack) == 1 ? inv.getLeftTank() : inv.getRightTank();
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
		ItemStack stack = playerIn.getHeldItem(handIn);
			
		if(CapabilityUtils.isWearingBackpack(playerIn) && handIn == EnumHand.MAIN_HAND)
		{
			if(stack.getTagCompound() == null)
			{
				this.getTagCompound(stack);
				return new ActionResult<>(EnumActionResult.SUCCESS, stack);
			}
			else
			{
				InventoryTravelersBackpack inv = CapabilityUtils.getBackpackInv(playerIn);
				RayTraceResult result = this.rayTrace(worldIn, playerIn, true);
				RayTraceResult nonFluidResult = this.rayTrace(worldIn, playerIn, false);
				FluidTank tank = this.getSelectedFluidTank(stack, inv);
				
				if(tank != null)
				{
					if(getHoseMode(stack) == 1)
					{
						if(result != null && result.typeOfHit == RayTraceResult.Type.BLOCK)
						{
							if(playerIn.canPlayerEdit(result.getBlockPos(), result.sideHit, stack))
							{
								Block blockResult = worldIn.getBlockState(result.getBlockPos()).getBlock();
								
								if(blockResult instanceof IFluidBlock)
								{
									FluidStack fluidStack = new FluidStack(((IFluidBlock)blockResult).getFluid(), Reference.BUCKET);
									
									if(tank.getFluidAmount() == 0 || tank.getFluid().isFluidEqual(fluidStack))
									{
										int amount = tank.fill(fluidStack, false);
										
										if(amount > 0 && tank.getFluidAmount() + amount <= tank.getCapacity())
										{
											worldIn.setBlockToAir(result.getBlockPos());
											tank.fill(fluidStack, true);
											inv.markTankDirty();
											worldIn.playSound(null, result.getBlockPos(), fluidStack.getFluid().getFillSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
											return new ActionResult<>(EnumActionResult.SUCCESS, stack);
										}
									}
								}
								
								else if(blockResult instanceof BlockLiquid)
								{
									BlockLiquidWrapper wrapper = new BlockLiquidWrapper((BlockLiquid)blockResult, worldIn, result.getBlockPos());
									
									FluidStack fluidStack = new FluidStack(wrapper.getTankProperties()[0].getContents(), Reference.BUCKET);
									
									if(tank.getFluidAmount() == 0 || tank.getFluid().isFluidEqual(fluidStack))
									{
										int amount = tank.fill(fluidStack, false);
										
										if(amount > 0 && tank.getFluidAmount() + amount <= tank.getCapacity())
										{
											worldIn.setBlockToAir(result.getBlockPos());
											tank.fill(fluidStack, true);
											inv.markTankDirty();
											worldIn.playSound(null, result.getBlockPos(), fluidStack.getFluid().getFillSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
											return new ActionResult<>(EnumActionResult.SUCCESS, stack);
										}
									}
								}
							}
						}
					}
					
					if(getHoseMode(stack) == 2)
					{
						if(nonFluidResult != null && nonFluidResult.typeOfHit == RayTraceResult.Type.BLOCK)
						{
							int x = nonFluidResult.getBlockPos().getX();
							int y = nonFluidResult.getBlockPos().getY();
							int z = nonFluidResult.getBlockPos().getZ();
							
							if(!worldIn.getBlockState(nonFluidResult.getBlockPos()).getBlock().isReplaceable(worldIn, nonFluidResult.getBlockPos()))
							{
								switch(nonFluidResult.sideHit)
								{
									case WEST:
										--x;
										break;
									case EAST:
										++x;
										break;
									case DOWN:
										--y;
										break;
									case NORTH:
										--z;
										break;
									case SOUTH:
										++z;
										break;
									case UP:
										++y;
										break;
									default:
										break;
								}
							}
							
							BlockPos newResult = new BlockPos(x,y,z);
							
							if(tank.getFluid() != null && tank.getFluidAmount() > 0)
							{
								FluidStack fluidStack = tank.getFluid();
								
								if(fluidStack.getFluid().canBePlacedInWorld())
								{
									Material material = worldIn.getBlockState(newResult).getMaterial();
									boolean flag = !material.isSolid();
									
									if(worldIn.provider.doesWaterVaporize() && fluidStack.getFluid() == FluidRegistry.WATER)
									{
										tank.drain(Reference.BUCKET, true);
										inv.markTankDirty();
										
                                        worldIn.playSound(null, x + 0.5D, y + 0.5D, z + 0.5D, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);
                                        
                                        for (int i = 0; i < 3; ++i)
                                        {
                                            double d0 = newResult.getX() + worldIn.rand.nextDouble();
                                            double d1 = newResult.getY() + worldIn.rand.nextDouble() * 0.5D + 0.5D;
                                            double d2 = newResult.getZ() + worldIn.rand.nextDouble();
                                            worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                                        }
                                        
                                        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
									}
									else
									{
										FluidStack drainedFluid = tank.drain(Reference.BUCKET, false);
										
										if(drainedFluid != null && drainedFluid.amount >= Reference.BUCKET)
										{
											if(!worldIn.isRemote && flag && !material.isLiquid())
											{
												worldIn.destroyBlock(newResult, true);
											}

											if(worldIn.setBlockState(newResult, fluidStack.getFluid().getBlock().getDefaultState()))
											{
												tank.drain(Reference.BUCKET, true);
												worldIn.getBlockState(newResult).neighborChanged(worldIn, newResult, fluidStack.getFluid().getBlock(), newResult);
											}
											
											worldIn.playSound(null, newResult, drainedFluid.getFluid().getEmptySound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
											inv.markTankDirty();
											return new ActionResult<>(EnumActionResult.SUCCESS, stack);
										}
									}
								}
							}
						}
					}
					
					if(getHoseMode(stack) == 3)
					{
						if(tank.getFluid() != null)
						{
							if(FluidEffectRegistry.hasFluidEffectAndCanExecute(tank.getFluid(), worldIn, playerIn))
							{
								playerIn.setActiveHand(EnumHand.MAIN_HAND);
								return new ActionResult<>(EnumActionResult.SUCCESS, stack);
							}
						}
					}
				}
			}
		}
        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        if(entityLiving instanceof EntityPlayer)
        {
        	EntityPlayer player = (EntityPlayer)entityLiving;
        	
        	if(CapabilityUtils.isWearingBackpack(player))
        	{
        		InventoryTravelersBackpack inv = CapabilityUtils.getBackpackInv(player);
        		FluidTank tank = this.getSelectedFluidTank(stack, inv);
        		
        		if(getHoseMode(stack) == 3)
                {
                	if(tank != null)
                	{
                		if(ServerActions.setFluidEffect(worldIn, player, tank))
                		{
                			FluidEffect targetEffect = FluidEffectRegistry.getFluidEffect(tank.getFluid().getFluid());

                			tank.drain(targetEffect.amountRequired, true);
							inv.markTankDirty();
                		}
                	}
                }
        	}
        }
        return stack;
    }
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
		if(entityIn instanceof EntityPlayer)
		{
			if(!CapabilityUtils.isWearingBackpack((EntityPlayer)entityIn))
			{
				if(stack.getTagCompound() != null)
				{
					stack.setTagCompound(null);
				}
			}
		}
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		if(getHoseMode(stack) == 0)
		{
			tooltip.add("Hose not assigned");
		}
		else
		{
			if(stack.getTagCompound() != null)
			{
				NBTTagCompound tag = stack.getTagCompound();
				
				if(tag.getInteger("Mode") == 1)
				{
					tooltip.add("Current mode: suck");
				}
				
				if(tag.getInteger("Mode") == 2)
				{
					tooltip.add("Current mode: spill");
				}
				
				if(tag.getInteger("Mode") == 3)
				{
					tooltip.add("Current mode: drink");
				}
				
				if(tag.getInteger("Tank") == 1) 
				{
					tooltip.add("Current tank: left");
				}
				
				if(tag.getInteger("Tank") == 2)
				{
					tooltip.add("Current tank: right");
				}
			}
		}
    }
	
	@Override
	public String getItemStackDisplayName(ItemStack stack)
    {
		int x = getHoseMode(stack);
		String mode = "";
		String localizedName = new TextComponentTranslation("item.hose.name").getFormattedText();
		String suckMode = new TextComponentTranslation("item.hose.suck").getFormattedText();
		String spillMode = new TextComponentTranslation("item.hose.spill").getFormattedText();
		String drinkMode = new TextComponentTranslation("item.hose.drink").getFormattedText();
		
		if(x == 1)
		{
			mode = " " + suckMode;
		}
		else if(x == 2)
		{
			mode = " " + spillMode;
		}
		else if(x == 3)
		{
			mode = " " + drinkMode;
		}
		
        return localizedName + mode;
    }
	
	public NBTTagCompound getTagCompound(ItemStack stack)
	{
		if(stack.getTagCompound() == null)
		{
			stack.setTagCompound(new NBTTagCompound());
		}
		
		NBTTagCompound tag = stack.getTagCompound();
		
		if(!tag.hasKey("Tank"))
		{
			tag.setInteger("Tank", 1);
		}
		
		if(!tag.hasKey("Mode"))
		{
			tag.setInteger("Mode", 1);
		}
	    	
		return tag;
	}
}