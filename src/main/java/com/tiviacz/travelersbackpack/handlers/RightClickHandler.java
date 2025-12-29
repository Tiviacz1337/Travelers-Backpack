package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.advancements.ActionTypeTrigger;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
                if(ComponentUtils.isWearingBackpack(player)) {
                    if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().isEmpty()) {
                        ItemStack backpackStack = ComponentUtils.getWearingBackpack(player).copy();
                        UseOnContext context = new UseOnContext(level, player, hand, backpackStack, hitResult);
                        boolean quickPickupFlag = level.getBlockState(pos).getBlock() instanceof TravelersBackpackBlock;

                        if(!quickPickupFlag && backpackStack.getItem() instanceof TravelersBackpackItem item) {
                            if(item.place(new BlockPlaceContext(context)) == InteractionResult.sidedSuccess(level.isClientSide)) {
                                player.swing(hand, true);
                                //level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.05F, (1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F) * 0.7F);
                                if(!level.isClientSide()) {
                                    ComponentUtils.getComponentOptional(player).ifPresent(data -> {
                                        data.remove();
                                        data.synchronise();
                                    });
                                }
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                }
            }

            //Change Sleeping Bag
            if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().is(ModTags.SLEEPING_BAGS) && level.getBlockEntity(pos) instanceof BackpackBlockEntity blockEntity) {
                ItemStack oldSleepingBag = blockEntity.getProperSleepingBag(blockEntity.getWrapper().getSleepingBagColor()).getBlock().asItem().getDefaultInstance();
                blockEntity.getWrapper().setSleepingBagColor(ShapedBackpackRecipe.getProperColor(player.getMainHandItem().getItem()));

                if(!level.isClientSide) {
                    if(player instanceof ServerPlayer serverPlayer) {
                        ActionTypeTrigger.INSTANCE.trigger(serverPlayer, ActionTypeTrigger.CHANGE_SLEEPING_BAG);
                    }
                    Containers.dropItemStack(level, pos.getX(), pos.above().getY(), pos.getZ(), oldSleepingBag);
                    player.getMainHandItem().shrink(1);
                }
                level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);
                return InteractionResult.SUCCESS;
            }

            //Remove custom backpack design (go back to standard)
            if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().is(Items.SHEARS) && level.getBlockEntity(pos) instanceof BackpackBlockEntity backpackBlockEntity) {
                if(!backpackBlockEntity.getWrapper().getBackpackStack().is(ModItems.STANDARD_TRAVELERS_BACKPACK)) {
                    ItemStack standardBackpack = new ItemStack(ModItems.STANDARD_TRAVELERS_BACKPACK, 1);
                    backpackBlockEntity.toItemStack(standardBackpack);
                    Direction direction = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);
                    if(!level.isClientSide && level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())) {
                        if(player instanceof ServerPlayer serverPlayer) {
                            ActionTypeTrigger.INSTANCE.trigger(serverPlayer, ActionTypeTrigger.REVERT_CUSTOM_BACKPACK);
                        }
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), standardBackpack);
                        backpackBlockEntity.removeSleepingBag(level, direction);
                        level.playSound(null, backpackBlockEntity.getBlockPos(), SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
                        player.gameEvent(GameEvent.SHEAR, player);
                        player.getMainHandItem().hurtAndBreak(1, player, consumer -> consumer.broadcastBreakEvent(hand));
                    }
                    return InteractionResult.SUCCESS;
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
                int tier = NbtHelper.getOrDefault(blockEntity.getWrapper().getBackpackStack(), ModDataHelper.TIER, 0);
                if(tier != 0) {
                    list.addAll(getUpgrades(tier));
                }

                //Add backpack
                Item backpackItem = blockEntity.getWrapper().getBackpackStack().getItem();
                list.add(backpackItem.getDefaultInstance());

                if(!level.isClientSide) {
                    Containers.dropContents(level, pos.above(), list);
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }

                return InteractionResult.SUCCESS;
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
                    }
                }
            }

            //Quick Pick-Up
            if(level.getBlockEntity(pos) instanceof BackpackBlockEntity backpackBlockEntity) {
                if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().isEmpty()) {
                    ItemStack backpack = new ItemStack(level.getBlockState(pos).getBlock(), 1).copy();
                    backpackBlockEntity.toItemStack(backpack);
                    Direction direction = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);

                    if(player.getInventory().selected < 0 || player.getInventory().selected > player.getInventory().items.size()) {
                        return InteractionResult.FAIL; //Fix for Deselect
                    }

                    if(!level.isClientSide && level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())) {
                        player.setItemInHand(InteractionHand.MAIN_HAND, backpack);
                        backpackBlockEntity.removeSleepingBag(level, direction);
                        level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);
                        return InteractionResult.SUCCESS;
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
}