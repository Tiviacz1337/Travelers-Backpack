package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class RightClickHandler {


    public static void registerListeners() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {

            BlockPos pos = hitResult.getBlockPos();

            //Quick Unequip
            if(TravelersBackpackConfig.getConfig().backpackSettings.rightClickUnequip && !TravelersBackpack.enableIntegration()) {
                if(ComponentUtils.isWearingBackpack(player) && !level.isClientSide) {
                    if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().isEmpty()) {
                        ItemStack backpackStack = ComponentUtils.getWearingBackpack(player).copy();
                        UseOnContext context = new UseOnContext(level, player, hand, backpackStack, hitResult);
                        boolean quickPickupFlag = level.getBlockState(pos).getBlock() instanceof TravelersBackpackBlock;

                        if(!quickPickupFlag && backpackStack.getItem() instanceof TravelersBackpackItem item) {
                            if(item.place(new BlockPlaceContext(context)) == InteractionResult.SUCCESS_SERVER) {
                                player.swing(hand, true);
                                level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.05F, (1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F) * 0.7F);
                                ComponentUtils.getComponent(player).ifPresent(data -> {
                                    data.remove();
                                    data.synchronise();
                                });
                                return InteractionResult.SUCCESS;
                                //event.setCanceled(true);
                                //event.setCancellationResult(InteractionResult.SUCCESS);
                                //return;
                            }
                        }
                    }
                }
            }

            //Change Sleeping Bag
            if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().is(ModTags.SLEEPING_BAGS) && level.getBlockEntity(pos) instanceof BackpackBlockEntity blockEntity) {
                ItemStack oldSleepingBag = BackpackBlockEntity.getProperSleepingBag(blockEntity.getWrapper().getSleepingBagColor()).getBlock().asItem().getDefaultInstance();
                blockEntity.getWrapper().setSleepingBagColor(ShapedBackpackRecipe.getProperColor(player.getMainHandItem().getItem()));

                if(!level.isClientSide) {
                    Containers.dropItemStack(level, pos.getX(), pos.above().getY(), pos.getZ(), oldSleepingBag);
                    player.getMainHandItem().shrink(1);
                }
                level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);
                return InteractionResult.SUCCESS;
                //event.setCancellationResult(InteractionResult.SUCCESS);
                //event.setCanceled(true);
                //return;
            }

            //Remove custom backpack design (go back to standard)
            if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().is(Items.SHEARS) && level.getBlockEntity(pos) instanceof BackpackBlockEntity backpackBlockEntity) {
                if(!backpackBlockEntity.getWrapper().getBackpackStack().is(ModItems.STANDARD_TRAVELERS_BACKPACK)) {
                    ItemStack standardBackpack = new ItemStack(ModItems.STANDARD_TRAVELERS_BACKPACK, 1);
                    Component standardName = standardBackpack.get(DataComponents.ITEM_NAME);
                    backpackBlockEntity.toItemStack(standardBackpack);
                    standardBackpack.set(DataComponents.ITEM_NAME, standardName);

                    Direction direction = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);
                    if(!level.isClientSide && level.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())) {
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), standardBackpack);
                        backpackBlockEntity.removeSleepingBag(level, direction);
                        level.playSound(null, backpackBlockEntity.getBlockPos(), SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
                        player.gameEvent(GameEvent.SHEAR, player);
                        player.getMainHandItem().hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                    }
                    return InteractionResult.SUCCESS;
                    //event.setCancellationResult(InteractionResult.SUCCESS);
                    // event.setCanceled(true);
                    //return;
                }
            }

            if(player.isShiftKeyDown() && player.getMainHandItem().getItem() == ModItems.BLANK_UPGRADE && level.getBlockEntity(pos) instanceof BackpackBlockEntity blockEntity) {
                NonNullList<ItemStack> list = NonNullList.create();
                for(int i = 0; i < blockEntity.getWrapper().getStorage().getSlots(); i++) {
                    ItemStack stackInSlot = blockEntity.getWrapper().getStorage().getStackInSlot(i);
                    if(!stackInSlot.isEmpty()) {
                        list.add(stackInSlot);
                    }
                }
                for(int i = 0; i < blockEntity.getWrapper().getTools().getSlots(); i++) {
                    ItemStack stackInSlot = blockEntity.getWrapper().getTools().getStackInSlot(i);
                    if(!stackInSlot.isEmpty()) {
                        list.add(stackInSlot);
                    }
                }
                for(int i = 0; i < blockEntity.getWrapper().getUpgrades().getSlots(); i++) {
                    ItemStack stackInSlot = blockEntity.getWrapper().getUpgrades().getStackInSlot(i);
                    if(!stackInSlot.isEmpty()) {
                        list.add(stackInSlot);
                    }
                }
                int tier = blockEntity.getWrapper().getBackpackStack().getOrDefault(ModDataComponents.TIER, 0);
                if(tier != 0) {
                    list.addAll(getUpgrades(tier));
                }
                if(!blockEntity.getWrapper().getUnsortableSlots().isEmpty()) {
                    blockEntity.getWrapper().setUnsortableSlots(List.of());
                }
                if(!blockEntity.getWrapper().getMemorySlots().isEmpty()) {
                    blockEntity.getWrapper().setMemorySlots(List.of());
                }
                if(!level.isClientSide) {
                    Containers.dropContents(level, pos.above(), list);
                }
                blockEntity.getWrapper().removeRenderInfo();
                ItemStack backpackCopy = blockEntity.getWrapper().getBackpackStack().copy();
                initializeDefaultSize(backpackCopy);

                backpackCopy.set(ModDataComponents.TIER, Tiers.LEATHER.getOrdinal());
                backpackCopy.remove(ModDataComponents.BACKPACK_CONTAINER);
                backpackCopy.remove(ModDataComponents.UPGRADES);
                backpackCopy.remove(ModDataComponents.TOOLS_CONTAINER);

                blockEntity.removeWrapper();
                blockEntity.setBackpack(backpackCopy, level.registryAccess());
                blockEntity.getWrapper().saveHandler.run();

                return InteractionResult.SUCCESS;
                //event.setCanceled(true);
                //event.setCancellationResult(InteractionResult.SUCCESS);
                //return;
            }

            //Quick Equip
            if(TravelersBackpackConfig.getConfig().backpackSettings.rightClickEquip && level.getBlockEntity(pos) instanceof BackpackBlockEntity backpackBlockEntity) {
                if(player.isShiftKeyDown() && !ComponentUtils.isWearingBackpack(player) && !TravelersBackpack.enableIntegration()) {
                    //Prioritize placing block
                    if(player.getItemInHand(hand).getItem() instanceof BlockItem) {
                        return InteractionResult.PASS;
                    }
                    ItemStack backpack = new ItemStack(level.getBlockState(pos).getBlock(), 1).copy();
                    backpackBlockEntity.toItemStack(backpack);
                    Direction direction = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);

                    if(!level.isClientSide && level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())) {
                        ComponentUtils.equipBackpack(player, backpack);
                        backpackBlockEntity.removeSleepingBag(level, direction);

                        return InteractionResult.SUCCESS;
                        //event.setCanceled(true);
                        //event.setCancellationResult(InteractionResult.SUCCESS);
                        //return;
                    }
                }
            }

            //Quick Pick-Up
            if(level.getBlockEntity(pos) instanceof BackpackBlockEntity backpackBlockEntity) {
                if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().isEmpty()) {
                    ItemStack backpack = new ItemStack(level.getBlockState(pos).getBlock(), 1).copy();
                    backpackBlockEntity.toItemStack(backpack);
                    Direction direction = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);

                    if(!level.isClientSide && level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())) {
                        player.setItemInHand(InteractionHand.MAIN_HAND, backpack);
                        backpackBlockEntity.removeSleepingBag(level, direction);
                        level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);

                        return InteractionResult.SUCCESS;
                        //
                        // event.setCanceled(true);
                        //event.setCancellationResult(InteractionResult.SUCCESS);
                    }
                }
            }
            return InteractionResult.PASS;
        });
    }

    public static final List<Supplier<Item>> UPGRADES = Arrays.asList(
            () -> ModItems.IRON_TIER_UPGRADE,
            () -> ModItems.GOLD_TIER_UPGRADE,
            () -> ModItems.DIAMOND_TIER_UPGRADE,
            () -> ModItems.NETHERITE_TIER_UPGRADE);

    public static NonNullList<ItemStack> getUpgrades(int tier) {
        NonNullList<ItemStack> list = NonNullList.create();
        for(int i = 0; i < tier; i++) {
            list.add(UPGRADES.get(i).get().getDefaultInstance());
        }
        return list;
    }

    public static void initializeDefaultSize(ItemStack stack) {
        Tiers.Tier tier = Tiers.LEATHER;
        stack.set(ModDataComponents.TIER, tier.getOrdinal());
        stack.set(ModDataComponents.STORAGE_SLOTS, tier.getStorageSlots());
        stack.set(ModDataComponents.UPGRADE_SLOTS, tier.getUpgradeSlots());
        stack.set(ModDataComponents.TOOL_SLOTS, tier.getToolSlots());
    }
}