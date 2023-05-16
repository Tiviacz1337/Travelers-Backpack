package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
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

    public TierUpgradeItem(Properties pProperties, Tiers.Tier tier)
    {
        super(pProperties);

        this.tier = tier;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
    {
        if(TravelersBackpackConfig.enableTierUpgrades)
        {
            if(this != ModItems.BLANK_UPGRADE.get())
            {
                tooltip.add(Component.translatable("item.travelersbackpack.tier_upgrade_tooltip", this.tier.getName()).withStyle(ChatFormatting.BLUE));
            }
            else
            {
                tooltip.add(Component.translatable("item.travelersbackpack.blank_upgrade_tooltip").withStyle(ChatFormatting.BLUE));
            }
        }
        else
        {
            tooltip.add(Component.translatable("item.travelersbackpack.tier_upgrade_disabled"));
        }
    }

  /*  @Override
    public InteractionResult useOn(UseOnContext context)
    {
        if(context.getHand() == InteractionHand.MAIN_HAND && context.getItemInHand().getItem() == ModItems.BLANK_UPGRADE.get())
        {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            Player player = context.getPlayer();

            if(level.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity)
            {
                if(blockEntity.getTier() != Tiers.LEATHER && player.isCrouching())
                {
                    int storageSlots = blockEntity.getTier().getStorageSlots();
                    NonNullList<ItemStack> list = NonNullList.create();

                    for(int i = 0; i < 9; i++)
                    {
                        ItemStack stack = blockEntity.getCraftingGridHandler().getStackInSlot(i);

                        if(!stack.isEmpty())
                        {
                            list.add(stack);
                            blockEntity.getCraftingGridHandler().setStackInSlot(i, ItemStack.EMPTY);
                        }
                    }

                    for(int i = storageSlots - 1; i > Tiers.LEATHER.getStorageSlots() - 7; i--)
                    {
                        ItemStack stack = blockEntity.getHandler().getStackInSlot(i);

                        if(!stack.isEmpty())
                        {
                            list.add(stack);
                            blockEntity.getHandler().setStackInSlot(i, ItemStack.EMPTY);
                        }
                    }

                    list.addAll(getUpgradesForTier(blockEntity.getTier()));

                    if(!blockEntity.getSlotManager().getUnsortableSlots().isEmpty())
                    {
                        blockEntity.getSlotManager().getUnsortableSlots().removeIf(i -> i > Tiers.LEATHER.getStorageSlots() - 7);
                    }

                    if(!blockEntity.getSlotManager().getMemorySlots().isEmpty())
                    {
                        blockEntity.getSlotManager().getMemorySlots().removeIf(p -> p.getFirst() > Tiers.LEATHER.getStorageSlots() - 7);
                    }

                    int fluidAmountLeft = blockEntity.getLeftTank().isEmpty() ? 0 : blockEntity.getLeftTank().getFluidAmount();

                    if(fluidAmountLeft > Tiers.LEATHER.getTankCapacity())
                    {
                        blockEntity.getLeftTank().drain(fluidAmountLeft - Tiers.LEATHER.getTankCapacity(), IFluidHandler.FluidAction.EXECUTE);
                    }

                    int fluidAmountRight = blockEntity.getRightTank().isEmpty() ? 0 : blockEntity.getRightTank().getFluidAmount();

                    if(fluidAmountRight > Tiers.LEATHER.getTankCapacity())
                    {
                        blockEntity.getRightTank().drain(fluidAmountRight - Tiers.LEATHER.getTankCapacity(), IFluidHandler.FluidAction.EXECUTE);
                    }

                    if(!level.isClientSide)
                    {
                        Containers.dropContents(level, pos.above(), list);
                    }

                    blockEntity.resetTier();
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
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