package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class TierUpgradeItem extends Item
{
    private final Tiers.Tier tier;

    public TierUpgradeItem(Item.Properties properties, Tiers.Tier tier)
    {
        super(properties);

        this.tier = tier;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
    {
        if(this != ModItems.BLANK_UPGRADE.get())
        {
            tooltip.add(new TranslatableComponent("item.travelersbackpack.tier_upgrade_tooltip", this.tier.getName()).withStyle(ChatFormatting.BLUE));
        }
        else
        {
            tooltip.add(new TranslatableComponent("item.travelersbackpack.blank_upgrade_tooltip").withStyle(ChatFormatting.BLUE));
        }
    }

   /* @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        if(context.getHand() == Hand.MAIN_HAND && context.getItemInHand().getItem() == ModItems.BLANK_UPGRADE.get())
        {
            World world = context.getLevel();
            BlockPos pos = context.getClickedPos();
            PlayerEntity player = context.getPlayer();

            if(world.getBlockEntity(pos) instanceof TravelersBackpackTileEntity)
            {
                TravelersBackpackTileEntity tileEntity = (TravelersBackpackTileEntity)world.getBlockEntity(pos);

                if(tileEntity.getTier() != Tiers.LEATHER && player.isCrouching())
                {
                    int storageSlots = tileEntity.getTier().getStorageSlots();
                    NonNullList<ItemStack> list = NonNullList.create();

                    for(int i = 0; i < 9; i++)
                    {
                        ItemStack stack = tileEntity.getCraftingGridInventory().getStackInSlot(i);

                        if(!stack.isEmpty())
                        {
                            list.add(stack);
                            tileEntity.getCraftingGridInventory().setStackInSlot(i, ItemStack.EMPTY);
                        }
                    }

                    for(int i = storageSlots - 1; i > Tiers.LEATHER.getStorageSlots() - 7; i--)
                    {
                        ItemStack stack = tileEntity.getInventory().getStackInSlot(i);

                        if(!stack.isEmpty())
                        {
                            list.add(stack);
                            tileEntity.getInventory().setStackInSlot(i, ItemStack.EMPTY);
                        }
                    }

                    list.addAll(getUpgradesForTier(tileEntity.getTier()));

                    if(!tileEntity.getSlotManager().getUnsortableSlots().isEmpty())
                    {
                        tileEntity.getSlotManager().getUnsortableSlots().removeIf(i -> i > Tiers.LEATHER.getStorageSlots() - 7);
                    }

                    if(!tileEntity.getSlotManager().getMemorySlots().isEmpty())
                    {
                        tileEntity.getSlotManager().getMemorySlots().removeIf(p -> p.getFirst() > Tiers.LEATHER.getStorageSlots() - 7);
                    }

                    int fluidAmountLeft = tileEntity.getLeftTank().isEmpty() ? 0 : tileEntity.getLeftTank().getFluidAmount();

                    if(fluidAmountLeft > Tiers.LEATHER.getTankCapacity())
                    {
                        tileEntity.getLeftTank().drain(fluidAmountLeft - Tiers.LEATHER.getTankCapacity(), IFluidHandler.FluidAction.EXECUTE);
                    }

                    int fluidAmountRight = tileEntity.getRightTank().isEmpty() ? 0 : tileEntity.getRightTank().getFluidAmount();

                    if(fluidAmountRight > Tiers.LEATHER.getTankCapacity())
                    {
                        tileEntity.getRightTank().drain(fluidAmountRight - Tiers.LEATHER.getTankCapacity(), IFluidHandler.FluidAction.EXECUTE);
                    }

                    if(!world.isClientSide)
                    {
                        InventoryHelper.dropContents(world, pos.above(), list);
                    }

                    tileEntity.resetTier();
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return ActionResultType.PASS;
    } */

    public static NonNullList<ItemStack> getUpgradesForTier(Tiers.Tier tier)
    {
        NonNullList<ItemStack> list = NonNullList.create();

        if(tier.getOrdinal() >= 1)
        {
            list.add(ModItems.IRON_TIER_UPGRADE.get().getDefaultInstance());

            if(tier.getOrdinal() >= 2)
            {
                list.add(ModItems.GOLD_TIER_UPGRADE.get().getDefaultInstance());

                if(tier.getOrdinal() >= 3)
                {
                    list.add(ModItems.DIAMOND_TIER_UPGRADE.get().getDefaultInstance());

                    if(tier.getOrdinal() >= 4)
                    {
                        list.add(ModItems.NETHERITE_TIER_UPGRADE.get().getDefaultInstance());
                    }
                }
            }
        }
        return list;
    }
}