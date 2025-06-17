package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.handlers.NeoForgeEventHandler;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.BackpackContainer;
import com.tiviacz.travelersbackpack.inventory.BackpackSettingsContainer;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu;
import com.tiviacz.travelersbackpack.inventory.sorter.ContainerSorter;
import com.tiviacz.travelersbackpack.inventory.upgrades.IEnable;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.ClientboundSyncItemStackPacket;
import com.tiviacz.travelersbackpack.util.InventoryHelper;
import com.tiviacz.travelersbackpack.util.ItemStackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Optional;

public class ServerActions {
    public static void swapTool(Player player, double scrollDelta) {
        if(AttachmentUtils.isWearingBackpack(player)) {
            BackpackWrapper wrapper = AttachmentUtils.getBackpackWrapper(player, AttachmentUtils.TOOLS_ONLY);
            ItemStackHandler inv = wrapper.getTools();
            if(InventoryHelper.isEmpty(inv)) return;

            int toolSlots = inv.getSlots();
            int lastSlot = toolSlots - 1;
            int j = 0;

            for(int i = 0; i <= lastSlot; i++) {
                if(!inv.getStackInSlot(i).isEmpty()) {
                    j++;
                }
            }

            ItemStack[] tools = new ItemStack[j];
            int slot = 0;

            for(int i = 0; i <= j - 1; i++) {
                tools[slot] = inv.getStackInSlot(i).copy();
                slot++;
            }

            swapTool(scrollDelta, tools, player);
            slot = 0;

            for(int i = 0; i <= j - 1; i++) {
                inv.setStackInSlot(i, tools[slot]);
                slot++;
            }

            wrapper.sendDataToClients(ModDataComponents.TOOLS_CONTAINER.get());
        }
    }

    public static void swapTool(double delta, ItemStack[] tools, Player player) {
        if(delta > 0) {
            ItemStack tempStack = tools[0];

            for(int i = 0; i <= tools.length - 1; i++) {
                if(i + 1 > tools.length - 1) {
                    tools[tools.length - 1] = player.getMainHandItem();
                    player.setItemInHand(InteractionHand.MAIN_HAND, tempStack);
                } else {
                    tools[i] = tools[i + 1];
                }
            }
        }
        if(delta < 0) {
            ItemStack tempStack = tools[tools.length - 1];

            for(int i = tools.length - 1; i >= 0; i--) {
                if(i - 1 < 0) {
                    tools[0] = player.getMainHandItem();
                    player.setItemInHand(InteractionHand.MAIN_HAND, tempStack);
                } else {
                    tools[i] = tools[i - 1];
                }
            }
        }
    }

    public static void equipBackpack(Player player, boolean equip) {
        if(equip) {
            handleEquipBackpack(player);
        } else {
            handleUnequipBackpack(player);
        }
    }

    public static boolean swapBackpack(Player player) {
        Level level = player.level();

        if(level.isClientSide || !AttachmentUtils.isWearingBackpack(player)) {
            return false;
        }

        if(player.containerMenu instanceof BackpackItemMenu) {
            player.closeContainer();
        }

        ItemStack equippedBackpack = AttachmentUtils.getWearingBackpack(player).copy();
        ItemStack newBackpack = player.getMainHandItem().copy();

        AttachmentUtils.getAttachment(player).ifPresent(attachment -> {
            attachment.equipBackpack(newBackpack);
            attachment.synchronise();
        });

        NeoForgeEventHandler.runAbilitiesRemoval(player);

        player.getMainHandItem().shrink(1);
        player.getInventory().add(equippedBackpack);

        return true;
    }

    public static boolean equipBackpack(Player player) {
        Level level = player.level();

        if(level.isClientSide) {
            return false;
        }

        if(AttachmentUtils.isWearingBackpack(player)) {
            return swapBackpack(player);
        }

        if(player.containerMenu instanceof BackpackItemMenu) {
            player.closeContainer();
        }

        ItemStack stack = player.getMainHandItem().copy();

        AttachmentUtils.getAttachment(player).ifPresent(attachment -> {
            attachment.equipBackpack(stack);
            attachment.synchronise();
        });

        player.getMainHandItem().shrink(1);
        return true;
    }

    public static void handleEquipBackpack(Player player) {
        if(!equipBackpack(player))
            return;

        playEquippingSound(player);
    }

    /*public static void equipBackpack(Player player) {
        Level level = player.level();

        if(!level.isClientSide) {
            if(!CapabilityUtils.isWearingBackpack(player)) {
                if(player.containerMenu instanceof BackpackItemMenu) player.closeContainer();

                ItemStack stack = player.getMainHandItem().copy();

                CapabilityUtils.getCapability(player).ifPresent(attachment -> {
                    attachment.equipBackpack(stack);
                    attachment.synchronise();
                });

                player.getMainHandItem().shrink(1);
                playEquippingSound(player);

            } else {
                player.closeContainer();
                player.sendSystemMessage(Component.translatable(Reference.OTHER_BACKPACK));
            }
        }
    }*/

    public static boolean unequipBackpack(Player player) {
        Level level = player.level();

        if(level.isClientSide || !AttachmentUtils.isWearingBackpack(player)) {
            return false;
        }

        if(player.containerMenu instanceof BackpackItemMenu) {
            player.closeContainer();
        }

        ItemStack backpack = AttachmentUtils.getWearingBackpack(player).copy();

        if(!player.getInventory().add(backpack)) {
            if(player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable(Reference.NO_SPACE));
            }
            return false;
        }

        AttachmentUtils.getAttachment(player).ifPresent(attachment -> {
            attachment.equipBackpack(new ItemStack(Items.AIR, 0));
            attachment.synchronise();
        });

        return true;
    }

    public static void handleUnequipBackpack(Player player) {
        if(!unequipBackpack(player))
            return;

        playEquippingSound(player);
    }

    private static void playEquippingSound(Player player) {
        player.level().playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, (1.0F + (player.level().getRandom().nextFloat() - player.level().getRandom().nextFloat()) * 0.2F) * 0.7F);
    }

  /*  public static void equipBackpack(Player player, boolean equip) {
        if(equip) {
            equipBackpack(player);
        } else {
            unequipBackpack(player);
        }
    }

    public static void equipBackpack(Player player) {
        Level level = player.level();

        if(!level.isClientSide) {
            if(!AttachmentUtils.isWearingBackpack(player)) {
                if(player.containerMenu instanceof BackpackItemMenu) player.closeContainer();

                ItemStack stack = player.getMainHandItem().copy();

                AttachmentUtils.getAttachment(player).ifPresent(attachment -> {
                    attachment.equipBackpack(stack);
                    attachment.synchronise();
                });

                player.getMainHandItem().shrink(1);
                level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, (1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F) * 0.7F);

            } else {
                player.closeContainer();
                player.sendSystemMessage(Component.translatable(Reference.OTHER_BACKPACK));
            }
        }
    }

    public static void unequipBackpack(Player player) {
        Level level = player.level();

        if(!level.isClientSide) {
            if(AttachmentUtils.isWearingBackpack(player)) {
                if(player.containerMenu instanceof BackpackItemMenu) player.closeContainer();

                ItemStack backpack = AttachmentUtils.getWearingBackpack(player).copy();

                if(!player.getInventory().add(backpack)) {
                    player.sendSystemMessage(Component.translatable(Reference.NO_SPACE));
                    return;
                }

                AttachmentUtils.getAttachment(player).ifPresent(attachment -> {
                    attachment.equipBackpack(new ItemStack(Items.AIR, 0));
                    attachment.synchronise();
                });
                level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.05F, (1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F) * 0.7F);
            }
        }
    }*/

    public static void openBackpackFromSlot(ServerPlayer player, int index, boolean fromSlot) {
        if(index >= 0 && index < player.getInventory().items.size()) {
            ItemStack backpackStack = player.getInventory().items.get(index);
            if(backpackStack.getItem() instanceof TravelersBackpackItem) {
                if(!TravelersBackpackConfig.SERVER.backpackSettings.allowOnlyEquippedBackpack.get()) {
                    if(!fromSlot || TravelersBackpackConfig.SERVER.backpackSettings.allowOpeningFromSlot.get()) {
                        BackpackContainer.openBackpack(player, backpackStack, Reference.ITEM_SCREEN_ID, index);
                    }
                }
            }
        }
    }

    public static void openBackpackSettings(ServerPlayer player, int entityId, boolean open) {
        if(player.getId() == entityId) {
            if(player.containerMenu instanceof BackpackBaseMenu menu) {
                if(open) {
                    if(menu.getWrapper().getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) {
                        if(player.level().getBlockEntity(menu.getWrapper().getBackpackPos()) instanceof BackpackBlockEntity backpackBlockEntity) {
                            backpackBlockEntity.openSettings(player, backpackBlockEntity, menu.getWrapper().getBackpackPos());
                        }
                    } else {
                        BackpackSettingsContainer.openSettings(player, menu.getWrapper().getBackpackStack(), menu.getWrapper().getScreenID(), menu.getWrapper().getBackpackSlotIndex());
                    }
                }
            } else if(player.containerMenu instanceof BackpackSettingsMenu menu) {
                if(!open) {
                    if(menu.getWrapper().getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) {
                        if(player.level().getBlockEntity(menu.getWrapper().getBackpackPos()) instanceof BackpackBlockEntity backpackBlockEntity) {
                            backpackBlockEntity.openBackpack(player, backpackBlockEntity, menu.getWrapper().getBackpackPos());
                        }
                    } else {
                        BackpackContainer.openBackpack(player, menu.getWrapper().getBackpackStack(), menu.getWrapper().getScreenID(), menu.getWrapper().getBackpackSlotIndex());
                    }
                }
            }
        }
    }

    public static final int TAB_OPEN = 0;
    public static final int UPGRADE_ENABLED = 1;
    public static final int SHIFT_CLICK_TO_BACKPACK = 2;
    public static final int PLAY_RECORD = 3;

    public static void modifyUpgradeTab(ServerPlayer player, int slot, boolean open, int packetType) {
        if(player.containerMenu instanceof BackpackBaseMenu menu) {
            ItemStack upgradeStack = menu.getWrapper().getUpgrades().getStackInSlot(slot);
            if(!upgradeStack.isEmpty()) {
                ItemStack updateStack = upgradeStack.copy();
                updateStack.set(getPacketType(packetType), open);
                menu.getWrapper().getUpgrades().setStackInSlot(slot, updateStack);

                if(packetType == UPGRADE_ENABLED) {
                    if(menu.getWrapper().getUpgradeManager().hasUpgradeInSlot(slot)) {
                        menu.getWrapper().getUpgradeManager().mappedUpgrades.get(slot).ifPresent(upgradeBase -> {
                            if(upgradeBase instanceof IEnable upg) {
                                upg.setEnabled(open);
                            }
                        });
                    }
                }
            }
        }
    }

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> getPacketType(int type) {
        return switch(type) {
            case 0 -> ModDataComponents.TAB_OPEN;
            case 1 -> ModDataComponents.UPGRADE_ENABLED;
            case 2 -> ModDataComponents.SHIFT_CLICK_TO_BACKPACK;
            case 3 -> ModDataComponents.IS_PLAYING;
            default -> ModDataComponents.TAB_OPEN;
        };
    }

    public static void removeBackpackUpgrade(ServerPlayer player, int slot) {
        if(player.containerMenu instanceof BackpackBaseMenu menu) {
            BackpackWrapper wrapper = menu.getWrapper();
            if(!wrapper.getUpgrades().getStackInSlot(slot).isEmpty()) {
                Optional<UpgradeBase<?>> upgrade = wrapper.getUpgradeManager().mappedUpgrades.get(slot);

                ItemStack upgradeStack = wrapper.getUpgrades().getStackInSlot(slot).copy();
                upgradeStack.set(ModDataComponents.TAB_OPEN, false);
                wrapper.getUpgrades().setStackInSlot(slot, ItemStack.EMPTY);

                upgrade.ifPresent(upgradeBase -> upgradeBase.onUpgradeRemoved(upgradeStack));

                if(!player.getInventory().add(upgradeStack)) {
                    player.drop(upgradeStack, true);
                }
                wrapper.saveHandler.run();
            }
        }
    }

    public static void switchAbilitySlider(ServerPlayer player, boolean sliderValue) {
        BackpackWrapper wrapper = AttachmentUtils.getBackpackWrapperArtificial(player);

        //If ability slider is being switched in the backpack screen, then reassign the wrapper
        if(player.containerMenu instanceof BackpackBaseMenu menu) {
            wrapper = menu.getWrapper();
        }

        wrapper.setDataAndSync(ModDataComponents.ABILITY_ENABLED.get(), sliderValue);

        //Run for equipped backpack
        if(wrapper.getBackpackOwner() != null) {
            if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_REMOVAL_LIST, wrapper.getBackpackStack()) && !sliderValue) {
                BackpackAbilities.ABILITIES.abilityRemoval(wrapper.getBackpackStack(), wrapper.getBackpackOwner());
            }

            if(wrapper.getBackpackStack().getItem() == ModItems.CHICKEN_TRAVELERS_BACKPACK.get() && wrapper.getCooldown() <= 0) {
                BackpackAbilities.ABILITIES.chickenAbility(wrapper.getBackpackStack(), wrapper.getBackpackOwner(), true);
            }
        }
    }

    public static void showToolSlots(ServerPlayer player, boolean show) {
        if(player.containerMenu instanceof BackpackBaseMenu menu) {
            menu.getWrapper().setDataAndSync(ModDataComponents.SHOW_TOOL_SLOTS.get(), show);
        }
    }

    public static void sortBackpack(ServerPlayer player, int button, boolean shiftPressed) {
        if(player.containerMenu instanceof BackpackBaseMenu menu) {
            ContainerSorter.selectSort(menu.getWrapper(), player, button, shiftPressed);
        }
    }

    public static void toggleVisibility(Player player) {
        if(player.containerMenu instanceof BackpackSettingsMenu menu) {
            boolean visibility = menu.getWrapper().getBackpackStack().getOrDefault(ModDataComponents.IS_VISIBLE, true);
            menu.getWrapper().setDataAndSync(ModDataComponents.IS_VISIBLE.get(), !visibility);
        }
    }

    public static void toggleButtonsVisibility(Player player) {
        if(player.containerMenu instanceof BackpackBaseMenu menu) {
            boolean current = menu.getWrapper().showMoreButtons();
            menu.getWrapper().setDataAndSync(ModDataComponents.SHOW_MORE_BUTTONS.get(), !current);
        }
    }

    public static void toggleSleepingBag(Player player, BlockPos pos, boolean isEquipped) {
        Level level = player.level();
        if(isEquipped) {
            BlockPos sleepingBagPos1 = pos.relative(player.getDirection());
            BlockPos sleepingBagPos2 = sleepingBagPos1.relative(player.getDirection());
            boolean canPlace = placeAndUseSleepingBag(player, sleepingBagPos1, sleepingBagPos2, pos, level, player.getDirection());
            if(!canPlace) {
                player.sendSystemMessage(Component.translatable(Reference.DEPLOY));
                player.closeContainer();
                return;
            }

            if(!level.isClientSide) {
                if(player instanceof ServerPlayer) {
                    player.startSleepInBed(pos.relative(player.getDirection()).relative(player.getDirection())).ifLeft(bedSleepingProblem -> {
                        if(bedSleepingProblem.getMessage() != null) {
                            player.displayClientMessage(bedSleepingProblem.getMessage(), true);
                            if(level.getBlockState(sleepingBagPos1).getBlock() instanceof SleepingBagBlock) {
                                level.setBlockAndUpdate(sleepingBagPos1, Blocks.AIR.defaultBlockState());
                            }
                            if(level.getBlockState(sleepingBagPos2).getBlock() instanceof SleepingBagBlock) {
                                level.setBlockAndUpdate(sleepingBagPos2, Blocks.AIR.defaultBlockState());
                            }
                        }
                    });
                    player.closeContainer();
                }
            }
        } else {
            if(level.getBlockEntity(pos) instanceof BackpackBlockEntity blockEntity) {
                if(!blockEntity.isSleepingBagDeployed()) {
                    if(!blockEntity.deploySleepingBag(level, pos)) {
                        player.sendSystemMessage(Component.translatable(Reference.DEPLOY));
                    }
                } else {
                    blockEntity.removeSleepingBag(level, blockEntity.getBlockDirection());
                }
                if(!level.isClientSide) {
                    player.closeContainer();
                }
            }
        }
    }

    public static boolean placeAndUseSleepingBag(Player player, BlockPos sleepingBagPos1, BlockPos sleepingBagPos2, BlockPos pos, Level level, Direction direction) {
        if(!player.onGround() || level.getBlockState(sleepingBagPos1.below()).isAir() || level.getBlockState(sleepingBagPos1.below()).getBlock() instanceof LiquidBlock || !BedBlock.canSetSpawn(level)) {
            return false;
        }
        ItemStack backpack = AttachmentUtils.getWearingBackpack(player);
        if(BackpackBlockEntity.canPlaceSleepingBag(sleepingBagPos2, level) && BackpackBlockEntity.canPlaceSleepingBag(sleepingBagPos1, level)) {
            level.playSound(null, sleepingBagPos2, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.5F, 1.0F);

            if(!level.isClientSide) {
                BlockState sleepingBagState = BackpackBlockEntity.getProperSleepingBag(backpack.getOrDefault(ModDataComponents.SLEEPING_BAG_COLOR.get(), DyeColor.RED.getId()));
                level.setBlock(sleepingBagPos1, sleepingBagState.setValue(SleepingBagBlock.FACING, direction).setValue(SleepingBagBlock.PART, BedPart.FOOT).setValue(SleepingBagBlock.CAN_DROP, false), 3);
                level.setBlock(sleepingBagPos2, sleepingBagState.setValue(SleepingBagBlock.FACING, direction).setValue(SleepingBagBlock.PART, BedPart.HEAD).setValue(SleepingBagBlock.CAN_DROP, false), 3);

                level.updateNeighborsAt(pos, sleepingBagState.getBlock());
                level.updateNeighborsAt(sleepingBagPos2, sleepingBagState.getBlock());
            }
            return true;
        }
        return false;
    }

    public static int throwPotion(Level level, Player player, ItemStack potionStack, boolean isSplash) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(), isSplash ? SoundEvents.SPLASH_POTION_THROW : SoundEvents.LINGERING_POTION_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

        if(!level.isClientSide) {
            ThrownPotion thrownpotion = new ThrownPotion(level, player);
            thrownpotion.setItem(potionStack);
            thrownpotion.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.5F, 1.0F);
            level.addFreshEntity(thrownpotion);
        }

        if(!player.getAbilities().instabuild) {
            return Reference.POTION;
        }
        return 0;
    }

    public static boolean setFluidEffect(Level level, Player player, FluidTank tank) {
        FluidStack fluidStack = tank.getFluid();
        boolean done = false;
        if(EffectFluidRegistry.hasExecutableEffects(fluidStack, level, player)) {
            done = EffectFluidRegistry.executeEffects(fluidStack, player, level);
        }
        return done;
    }

    public static void switchHoseMode(Player player, double scrollDelta) {
        ItemStack hose = player.getMainHandItem();
        if(hose.getItem() instanceof HoseItem) {
            List<Integer> settings = hose.getOrDefault(ModDataComponents.HOSE_MODES, List.of(1, 1));
            if(scrollDelta > 0) {
                int nextMode = settings.get(0) + 1;
                hose.set(ModDataComponents.HOSE_MODES, List.of(nextMode == 4 ? 1 : nextMode, settings.get(1)));
            } else if(scrollDelta < 0) {
                int nextMode = settings.get(0) - 1;
                hose.set(ModDataComponents.HOSE_MODES, List.of(nextMode == 0 ? 3 : nextMode, settings.get(1)));
            }
        }

        if(!player.level().isClientSide) {
            PacketDistributor.sendToPlayer((ServerPlayer)player, new ClientboundSyncItemStackPacket(player.getId(), player.getInventory().selected, hose, ItemStackUtils.createDataComponentMap(hose, ModDataComponents.HOSE_MODES.get())));
        }
    }

    public static void toggleHoseTank(Player player) {
        ItemStack hose = player.getMainHandItem();
        if(hose.getItem() instanceof HoseItem) {
            List<Integer> settings = hose.getOrDefault(ModDataComponents.HOSE_MODES, List.of(1, 1));
            if(settings.get(1) == 1) {
                hose.set(ModDataComponents.HOSE_MODES, List.of(settings.get(0), 2));
            } else {
                hose.set(ModDataComponents.HOSE_MODES, List.of(settings.get(0), 1));
            }
        }

        if(!player.level().isClientSide) {
            PacketDistributor.sendToPlayer((ServerPlayer)player, new ClientboundSyncItemStackPacket(player.getId(), player.getInventory().selected, hose, ItemStackUtils.createDataComponentMap(hose, ModDataComponents.HOSE_MODES.get())));
        }
    }
}