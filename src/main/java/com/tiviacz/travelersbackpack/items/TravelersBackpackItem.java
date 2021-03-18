package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackItemStackRenderer;
import com.tiviacz.travelersbackpack.compat.curios.TravelersBackpackCurios;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.tileentity.TravelersBackpackTileEntity;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TravelersBackpackItem extends BlockItem
{
    public TravelersBackpackItem(Block block)
    {
        super(block, new Item.Properties().group(Reference.TRAVELERS_BACKPACK_TAB).maxStackSize(1).setISTER(() -> TravelersBackpackItemStackRenderer::new));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        if(!worldIn.isRemote)
        {
            if(handIn == Hand.MAIN_HAND)
            {
                if(itemstack.getItem() == this && !playerIn.isSneaking())
                {
                    TravelersBackpackInventory.openGUI((ServerPlayerEntity)playerIn, playerIn.inventory.getCurrentItem(), Reference.TRAVELERS_BACKPACK_ITEM_SCREEN_ID);
                }
            }
        }
        return ActionResult.resultPass(itemstack);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        ActionResultType actionresulttype = this.tryPlace(new BlockItemUseContext(context));
        return !actionresulttype.isSuccessOrConsume() ? this.onItemRightClick(context.getWorld(), context.getPlayer(), context.getHand()).getType() : actionresulttype;

   /*     BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();

        if(context.getHand() == Hand.MAIN_HAND && !player.isSneaking())
        {
            return ActionResultType.FAIL;
        }

        BlockState blockState = context.getWorld().getBlockState(context.getPos());

        if(!blockState.isReplaceable(new BlockItemUseContext(context)))
        {
            pos = pos.offset(context.getFace());
        }

        ItemStack stack = player.getHeldItem(context.getHand());

        if(!stack.isEmpty() && player.canPlayerEdit(pos, context.getPlacementHorizontalFacing(), stack))
        {
            BlockState blockState1 = block.getStateForPlacement(new BlockItemUseContext(context));

            if(placeBlockAt(stack, player, world, pos, blockState1))
            {
                if(stack.getTag() != null)
                {
                    if(world.getTileEntity(pos) instanceof TravelersBackpackTileEntity)
                    {
                        ((TravelersBackpackTileEntity)world.getTileEntity(pos)).loadAllData(stack.getTag());
                    }
                    //((TravelersBackpackTileEntity)world.getTileEntity(pos)).loadAllData(stack.getTag());
                }
                blockState1 = world.getBlockState(pos);
                SoundType soundtype = blockState1.getBlock().getSoundType(blockState1, world, pos, player);
                world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                stack.shrink(1);
            }
            return ActionResultType.SUCCESS;
        }
        else
        {
            return ActionResultType.FAIL;
        } */
    }

    public ActionResultType tryPlace(BlockItemUseContext context)
    {
        if(!context.canPlace() || (context.getHand() == Hand.MAIN_HAND && !context.getPlayer().isSneaking()))
        {
            return ActionResultType.FAIL;
        }
        else
        {
            BlockItemUseContext blockitemusecontext = this.getBlockItemUseContext(context);

            if(blockitemusecontext == null)
            {
                return ActionResultType.FAIL;
            }
            else
            {
                BlockState blockstate = this.getStateForPlacement(blockitemusecontext);

                if(blockstate == null)
                {
                    return ActionResultType.FAIL;
                }

                else if(!this.placeBlock(blockitemusecontext, blockstate))
                {
                    return ActionResultType.FAIL;
                }
                else
                {
                    BlockPos blockpos = blockitemusecontext.getPos();
                    World world = blockitemusecontext.getWorld();
                    PlayerEntity player = blockitemusecontext.getPlayer();
                    ItemStack itemstack = blockitemusecontext.getItem();
                    BlockState blockstate1 = world.getBlockState(blockpos);
                    Block block = blockstate1.getBlock();

                    if(block == blockstate.getBlock())
                    {
                        this.onBlockPlaced(blockpos, world, player, itemstack, blockstate1);
                        block.onBlockPlacedBy(world, blockpos, blockstate1, player, itemstack);

                        if(itemstack.getTag() != null && world.getTileEntity(blockpos) instanceof TravelersBackpackTileEntity)
                        {
                            ((TravelersBackpackTileEntity)world.getTileEntity(blockpos)).loadAllData(itemstack.getTag());
                        }

                        if(player instanceof ServerPlayerEntity)
                        {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)player, blockpos, itemstack);
                        }
                    }

                    SoundType soundtype = blockstate1.getSoundType(world, blockpos, context.getPlayer());
                    world.playSound(player, blockpos, this.getPlaceSound(blockstate1, world, blockpos, player), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

                    if(player == null || !player.abilities.isCreativeMode)
                    {
                        itemstack.shrink(1);
                    }

                    return ActionResultType.func_233537_a_(world.isRemote);
                }
            }
        }
    }

  /*  public boolean placeBlockAt(ItemStack stack, PlayerEntity player, World world, BlockPos pos, BlockState newState)
    {
        if(!world.setBlockState(pos, newState, 11))
        {
            return false;
        }

        BlockState state = world.getBlockState(pos);

        if(state.getBlock() == block)
        {
            setTileEntityNBT(world, player, pos, stack);
            block.onBlockPlacedBy(world, pos, state, player, stack);

            if(player instanceof ServerPlayerEntity)
            {
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)player, pos, stack);
            }
        }
        return true;
    } */

    @Override
    @OnlyIn(Dist.CLIENT)
    public ITextComponent getDisplayName(ItemStack stack)
    {
        return new TranslationTextComponent("block.travelersbackpack.travelers_backpack");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(new TranslationTextComponent(this.getTranslationKey()).mergeStyle(TextFormatting.BLUE));

        if(TravelersBackpackConfig.CLIENT.obtainTips.get())
        {
            if(stack.getItem() == ModItems.BAT_TRAVELERS_BACKPACK.get())
            {
                tooltip.add(new TranslationTextComponent("obtain.travelersbackpack.bat").mergeStyle(TextFormatting.BLUE));
            }

            if(stack.getItem() == ModItems.VILLAGER_TRAVELERS_BACKPACK.get())
            {
                tooltip.add(new TranslationTextComponent("obtain.travelersbackpack.villager").mergeStyle(TextFormatting.BLUE));
            }
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt)
    {
        if(TravelersBackpack.enableCurios())
        {
            return new ICapabilityProvider()
            {
                final LazyOptional<ICurio> curio = LazyOptional.of(TravelersBackpackCurios::createBackpackProvider);

                @Nonnull
                @Override
                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
                {
                    return CuriosCapability.ITEM.orEmpty(cap, curio);
                }
            };
        }
        return null;
    }

    //Special

/*    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        Item item = stack.getItem();
        boolean flag = item == ModItems.NETHERITE_TRAVELERS_BACKPACK.get() ||
                item == ModItems.DIAMOND_TRAVELERS_BACKPACK.get() ||
                item == ModItems.GOLD_TRAVELERS_BACKPACK.get() ||
                item == ModItems.IRON_TRAVELERS_BACKPACK.get();

        if(flag)
        {
            return enchantment == Enchantments.INFINITY;
        }
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack)
    {
        Item item = stack.getItem();
        boolean flag = item == ModItems.NETHERITE_TRAVELERS_BACKPACK.get() ||
                item == ModItems.DIAMOND_TRAVELERS_BACKPACK.get() ||
                item == ModItems.GOLD_TRAVELERS_BACKPACK.get() ||
                item == ModItems.IRON_TRAVELERS_BACKPACK.get();

        return flag;
    }

    @Override
    public int getItemEnchantability() {
        return 30;
    } */
}
