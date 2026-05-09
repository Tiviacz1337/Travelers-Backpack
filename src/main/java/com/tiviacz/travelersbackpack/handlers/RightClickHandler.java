package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.advancements.ActionTypeTrigger;
import com.tiviacz.travelersbackpack.attachment.AttachmentUtils;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.block.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModAdvancements;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.handler.StorageAccessWrapper;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.InventoryHelper;
import net.fabricmc.fabric.api.event.player.PlayerPickItemEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class RightClickHandler {
    public static void registerListeners() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            BlockPos pos = hitResult.getBlockPos();

            //Quick Unequip
            if(TravelersBackpackConfig.SERVER.backpackSettings.rightClickUnequip.get() && !TravelersBackpack.enableIntegration()) {
                if(AttachmentUtils.isWearingBackpack(player)) {
                    if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().isEmpty()) {
                        ItemStack backpackStack = AttachmentUtils.getWearingBackpack(player).copy();
                        UseOnContext context = new UseOnContext(level, player, hand, backpackStack, hitResult);
                        boolean quickPickupFlag = level.getBlockState(pos).getBlock() instanceof TravelersBackpackBlock;

                        if(!quickPickupFlag && backpackStack.getItem() instanceof TravelersBackpackItem item) {
                            if(item.place(new BlockPlaceContext(context)) == (level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER)) {
                                player.swing(hand, true);
                                if(!level.isClientSide()) {
                                    AttachmentUtils.getAttachment(player).ifPresent(data -> {
                                        data.remove(player);
                                        data.synchronise(player);
                                    });
                                }
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                }
            }

            //Change Sleeping Bag
            if(TravelersBackpackConfig.SERVER.backpackUpgrades.enableSleepingBag.get() && player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().is(ModTags.SLEEPING_BAGS) && level.getBlockEntity(pos) instanceof BackpackBlockEntity blockEntity) {
                int sleepingBagColor = blockEntity.getWrapper().getSleepingBagColor();
                ItemStack oldSleepingBag = ItemStack.EMPTY;
                if(sleepingBagColor != -1) {
                    oldSleepingBag = BackpackBlockEntity.getProperSleepingBag(blockEntity.getWrapper().getSleepingBagColor()).getBlock().asItem().getDefaultInstance();
                }
                blockEntity.getWrapper().setSleepingBagColor(ShapedBackpackRecipe.getProperColor(player.getMainHandItem().getItem()));

                if(!level.isClientSide()) {
                    if(player instanceof ServerPlayer serverPlayer) {
                        ModAdvancements.ACTION_TRIGGER.trigger(serverPlayer, ActionTypeTrigger.CHANGE_SLEEPING_BAG);
                    }
                    Containers.dropItemStack(level, pos.getX(), pos.above().getY(), pos.getZ(), oldSleepingBag);
                    player.getMainHandItem().shrink(1);
                }
                level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, (1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F) * 0.7F);
                return InteractionResult.SUCCESS;
            }

            //Remove custom backpack design (go back to standard)
            if(player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().is(Items.SHEARS) && level.getBlockEntity(pos) instanceof BackpackBlockEntity backpackBlockEntity) {
                if(!backpackBlockEntity.getWrapper().getBackpackStack().is(ModItems.STANDARD_TRAVELERS_BACKPACK)) {
                    ItemStack standardBackpack = new ItemStack(ModItems.STANDARD_TRAVELERS_BACKPACK, 1);
                    Component standardName = standardBackpack.get(DataComponents.ITEM_NAME);
                    var itemModel = standardBackpack.get(DataComponents.ITEM_MODEL); //Store proper item model
                    backpackBlockEntity.toItemStack(standardBackpack);
                    standardBackpack.set(DataComponents.ITEM_MODEL, itemModel); //Retrieve item model
                    standardBackpack.set(DataComponents.ITEM_NAME, standardName);

                    Direction direction = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);
                    if(!level.isClientSide() && level.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())) {
                        if(player instanceof ServerPlayer serverPlayer) {
                            ModAdvancements.ACTION_TRIGGER.trigger(serverPlayer, ActionTypeTrigger.REVERT_CUSTOM_BACKPACK);
                        }
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), standardBackpack);
                        backpackBlockEntity.removeSleepingBag(level, direction);
                        level.playSound(null, backpackBlockEntity.getBlockPos(), SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
                        player.gameEvent(GameEvent.SHEAR, player);
                        player.getMainHandItem().hurtAndBreak(1, player, hand.asEquipmentSlot());
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
                int tier = blockEntity.getWrapper().getBackpackStack().getOrDefault(ModDataComponents.TIER, 0);
                if(tier != 0) {
                    list.addAll(getUpgrades(tier));
                }

                //Add backpack
                Item backpackItem = blockEntity.getWrapper().getBackpackStack().getItem();
                ItemStack backpackStack = new ItemStack(backpackItem);

                //Carry over sleeping bag info
                int sleepingBagColor = blockEntity.getWrapper().getSleepingBagColor();
                if(sleepingBagColor == -1) {
                    backpackStack.set(ModDataComponents.SLEEPING_BAG_COLOR, -1);
                }

                //Add backpack
                list.add(backpackStack);

                if(!level.isClientSide()) {
                    Containers.dropContents(level, pos.above(), list);
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
                return InteractionResult.SUCCESS;
            }

            //Quick Equip
            if(TravelersBackpackConfig.SERVER.backpackSettings.rightClickEquip.get() && level.getBlockEntity(pos) instanceof BackpackBlockEntity backpackBlockEntity) {
                if(player.isShiftKeyDown() && !AttachmentUtils.isWearingBackpack(player) && !TravelersBackpack.enableIntegration()) {
                    //Prioritize placing block
                    if(player.getItemInHand(hand).getItem() instanceof BlockItem) {
                        return InteractionResult.PASS;
                    }
                    ItemStack backpack = new ItemStack(level.getBlockState(pos).getBlock(), 1).copy();
                    backpackBlockEntity.toItemStack(backpack);
                    Direction direction = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);

                    if(!level.isClientSide() && level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())) {
                        AttachmentUtils.equipBackpack(player, backpack);
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

                    if(player.getInventory().getSelectedSlot() < 0 || player.getInventory().getSelectedSlot() > player.getInventory().getNonEquipmentItems().size()) {
                        return InteractionResult.FAIL; //Fix for Deselect
                    }

                    if(!level.isClientSide() && level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())) {
                        player.setItemInHand(InteractionHand.MAIN_HAND, backpack);
                        backpackBlockEntity.removeSleepingBag(level, direction);
                        level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, (1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F) * 0.7F);

                        return InteractionResult.SUCCESS;
                    }
                }
            }

            //Grant achievement for washing backpack
            if(level.getBlockState(pos).getBlock() instanceof LayeredCauldronBlock) {
                ItemStack stack = player.getItemInHand(hand);
                if(stack.getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK && stack.is(ItemTags.CAULDRON_CAN_REMOVE_DYE) && stack.has(DataComponents.DYED_COLOR)) {
                    if(player instanceof ServerPlayer serverPlayer) {
                        ModAdvancements.ACTION_TRIGGER.trigger(serverPlayer, ActionTypeTrigger.UNDYE_BACKPACK);
                    }
                }
            }

            return InteractionResult.PASS;
        });

        PlayerPickItemEvents.BLOCK.register((player, pos, state, requestIncludeData) -> {
            if(AttachmentUtils.isWearingBackpack(player)) {
                if(player.isWithinBlockInteractionRange(pos, 1.0)) {
                    ServerLevel level = player.level();
                    ItemStack itemStack = state.getCloneItemStack(level, pos, false);
                    if(!itemStack.isEmpty()) {
                        if(itemStack.isItemEnabled(level.enabledFeatures())) {
                            Inventory inventory = player.getInventory();
                            //If found in inventory, do vanilla pick item
                            if(inventory.findSlotMatchingItem(itemStack) != -1 || player.hasInfiniteMaterials()) {
                                return null;
                            }
                            BackpackWrapper wrapper = AttachmentUtils.getBackpackWrapper(player, AttachmentUtils.STORAGE_ONLY.get());

                            AtomicReference<ItemStack> atomicStack = new AtomicReference<>(null);
                            StorageAccessWrapper storage = wrapper.getStorageForInputOutput();

                            InventoryHelper.iterate(storage, (slot, stack) -> {
                                //Continue if found required stack
                                if(ItemStack.isSameItemSameComponents(stack, itemStack)) {
                                    inventory.setSelectedSlot(inventory.getSuitableHotbarSlot());
                                    ItemStack pickResult = inventory.getSelectedItem();
                                    inventory.setItem(inventory.getSelectedSlot(), stack.copy());

                                    storage.setStackInSlot(slot, pickResult);

                                    player.connection.send(new ClientboundSetHeldSlotPacket(inventory.getSelectedSlot()));
                                    player.inventoryMenu.broadcastChanges();
                                    atomicStack.set(stack);
                                    return true;
                                }
                                return false;
                            });
                            return atomicStack.get();
                        }
                    }
                }
            }
            return null;
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