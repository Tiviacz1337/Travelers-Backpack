package com.tiviacz.travelersbackpack.item;

import com.google.common.collect.Multimap;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.client.screens.tooltip.BackpackTooltipComponent;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.entity.BackpackItemEntity;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.BackpackContainer;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.menu.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.util.InventoryHelper;
import com.tiviacz.travelersbackpack.util.KeyHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import com.tiviacz.travelersbackpack.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class TravelersBackpackItem extends BlockItem {
    public TravelersBackpackItem(Properties properties, Block block) {
        super(block, properties.useBlockDescriptionPrefix().stacksTo(1)
                .component(ModDataComponents.TIER, 0)
                .component(ModDataComponents.SLEEPING_BAG_COLOR, DyeColor.RED.getId())
                .component(ModDataComponents.IS_VISIBLE, true));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if(hand == InteractionHand.OFF_HAND || player.isCrouching()) {
            return InteractionResult.FAIL;
        }

        if(!TravelersBackpackConfig.getConfig().backpackSettings.allowOnlyEquippedBackpack) {
            if(!level.isClientSide()) {
                BackpackContainer.openBackpack((ServerPlayer)player, player.getInventory().getSelectedItem(), Reference.ITEM_SCREEN_ID, player.getInventory().getSelectedSlot());
            }
        } else {
            if(!ComponentUtils.isWearingBackpack(player) && !TravelersBackpack.enableIntegration()) {
                ServerActions.equipBackpack(player);
                player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult interactionResult = this.place(new BlockPlaceContext(context));
        return !interactionResult.consumesAction() ? this.use(context.getLevel(), context.getPlayer(), context.getHand()) : interactionResult;
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        if(!context.canPlace() || (context.getHand() == InteractionHand.MAIN_HAND && context.getPlayer() != null && !context.getPlayer().isCrouching())) {
            return InteractionResult.FAIL;
        } else {
            BlockPlaceContext blockitemusecontext = this.updatePlacementContext(context);

            if(blockitemusecontext == null) {
                return InteractionResult.FAIL;
            } else {
                BlockState blockstate = this.getPlacementState(blockitemusecontext);

                if(blockstate == null) {
                    return InteractionResult.FAIL;
                } else if(!this.placeBlock(blockitemusecontext, blockstate)) {
                    return InteractionResult.FAIL;
                } else {
                    BlockPos blockpos = blockitemusecontext.getClickedPos();
                    Level level = blockitemusecontext.getLevel();
                    Player player = blockitemusecontext.getPlayer();
                    ItemStack itemstack = blockitemusecontext.getItemInHand();
                    BlockState blockstate1 = level.getBlockState(blockpos);

                    if(blockstate1.is(blockstate.getBlock())) {
                        this.updateCustomBlockEntityTag(blockpos, level, player, itemstack, blockstate1);
                        blockstate1.getBlock().setPlacedBy(level, blockpos, blockstate1, player, itemstack);

                        if(level.getBlockEntity(blockpos) instanceof BackpackBlockEntity blockEntity) {
                            blockEntity.setBackpack(itemstack, level.registryAccess());
                        }

                        if(player instanceof ServerPlayer serverPlayer) {
                            CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, blockpos, itemstack);
                        }
                    }

                    level.gameEvent(player, GameEvent.BLOCK_PLACE, blockpos);
                    SoundType soundtype = blockstate1.getSoundType();
                    level.playSound(player, blockpos, this.getPlaceSound(blockstate1), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

                    if(player == null || !player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }

                    return InteractionResult.SUCCESS_SERVER;
                }
            }
        }
    }

    public static boolean isCreative(Player player) {
        return player.level().isClientSide() && player.containerMenu instanceof CreativeModeInventoryScreen.ItemPickerMenu;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if(isCreative(player) || stack.getCount() > 1 || !slot.mayPickup(player) || action != ClickAction.SECONDARY) {
            return super.overrideStackedOnOther(stack, slot, action, player);
        }
        ItemStack itemstack = slot.getItem();
        if(BackpackSlotItemHandler.isItemValid(itemstack)) {
            int count = add(player, stack, itemstack, true);
            if(count <= 0) {
                return false;
            }
            int j = add(player, stack, slot.safeTake(count, count, player), false);
            if(j > 0) {
                this.playInsertSound(player);
            }
            return true;
        }
        return super.overrideStackedOnOther(stack, slot, action, player);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if(isCreative(player) || stack.getCount() > 1 || !slot.mayPlace(stack) || action != ClickAction.SECONDARY) {
            return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
        }
        if(slot.allowModification(player)) {
            int i = add(player, stack, other, false);
            if(i > 0) {
                this.playInsertSound(player);
                other.shrink(i);
            }
            return true;
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
    }

    private static int add(Player player, ItemStack backpackStack, ItemStack insertedStack, boolean simulate) {
        int k = insertedStack.getCount();
        if(!insertedStack.isEmpty() && BackpackSlotItemHandler.isItemValid(insertedStack)) {
            BackpackWrapper wrapper = BackpackWrapper.fromStack(backpackStack);
            ItemStack result = InventoryHelper.addItemStackToHandler(wrapper.getStorageForInputOutput(), insertedStack, simulate);
            return k - result.getCount();
        } else {
            return 0;
        }
    }

    private void playInsertSound(Entity pEntity) {
        pEntity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> componentConsumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipDisplay, componentConsumer, tooltipFlag);

        if(stack.has(ModDataComponents.TIER)) {
            componentConsumer.accept(Component.translatable("tier.travelersbackpack.backpack").append(Tiers.of(stack.get(ModDataComponents.TIER)).getLocalizedName()));
        }

        if(stack.has(ModDataComponents.BACKPACK_CONTAINER) && !KeyHelper.isCtrlPressed()) {
            componentConsumer.accept(Component.translatable("item.travelersbackpack.inventory_tooltip").withStyle(ChatFormatting.BLUE));
        }

        if(TravelersBackpackConfig.getConfig().client.obtainTips) {
            if(stack.getItem() == ModItems.BAT_TRAVELERS_BACKPACK) {
                componentConsumer.accept(Component.translatable("obtain.travelersbackpack.bat").withStyle(ChatFormatting.BLUE));
            }
            if(stack.getItem() == ModItems.VILLAGER_TRAVELERS_BACKPACK) {
                componentConsumer.accept(Component.translatable("obtain.travelersbackpack.villager").withStyle(ChatFormatting.BLUE));
            }
            if(stack.getItem() == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK) {
                componentConsumer.accept(Component.translatable("obtain.travelersbackpack.iron_golem").withStyle(ChatFormatting.BLUE));
            }
        }
        //Check if specific ability is enabled && Check if Abilities are enabled overall
        if(BackpackAbilities.ALLOWED_ABILITIES.contains(stack.getItem()) && TravelersBackpackConfig.getConfig().backpackAbilities.enableBackpackAbilities) {
            if(KeyHelper.isShiftPressed()) {
                //Custom Descriptions
                if(BackpackAbilities.CUSTOM_DESCRIPTIONS.contains(stack.getItem())) {
                    componentConsumer.accept(Component.translatable("ability.travelersbackpack." + this.getDescriptionId().replaceAll("block.travelersbackpack.", "")).withStyle(ChatFormatting.BLUE));
                }
                boolean whenEquippedPresent = false;
                //Add descriptions based on BackpackEffects (Can be added)
                if(BackpackAbilities.getBackpackEffects().containsKey(stack.getItem())) {
                    componentConsumer.accept(Component.translatable("ability.travelersbackpack.when_equipped").withStyle(ChatFormatting.DARK_PURPLE));
                    whenEquippedPresent = true;
                    BackpackAbilities.getBackpackEffects().entries().stream().filter(entry -> entry.getKey() == stack.getItem()).forEach(entry -> {
                        MutableComponent mutablecomponent = Component.literal("- ");
                        mutablecomponent.append(Component.translatable(entry.getValue().effect().value().getDescriptionId()));
                        if(entry.getValue().amplifier() > 0) {
                            mutablecomponent = Component.translatable("potion.withAmplifier", mutablecomponent, Component.translatable("potion.potency." + entry.getValue().amplifier()));
                        }
                        if(BackpackAbilities.getCooldowns().containsKey(stack.getItem())) {
                            mutablecomponent.append(" " + TextUtils.getConvertedTime(entry.getValue().minDuration()));
                        }
                        componentConsumer.accept(mutablecomponent.withStyle(entry.getValue().effect().value().getCategory().getTooltipFormatting()));
                    });
                }

                //Add attribute modifiers
                addAttributeModifierTooltip(stack, componentConsumer, whenEquippedPresent);

                //Tooltip to show if ability is available for equipped backpack, block, or both
                if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, stack) && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, stack)) {
                    componentConsumer.accept(Component.translatable("ability.travelersbackpack.item_and_block"));
                } else if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, stack) && !BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, stack)) {
                    componentConsumer.accept(Component.translatable("ability.travelersbackpack.block"));
                } else if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, stack) && !BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, stack)) {
                    componentConsumer.accept(Component.translatable("ability.travelersbackpack.item"));
                }
            } else {
                componentConsumer.accept(Component.translatable("ability.travelersbackpack.hold_shift").withStyle(ChatFormatting.BLUE));
            }
        }
    }

    private void addAttributeModifierTooltip(ItemStack stack, Consumer<Component> componentConsumer, boolean whenEquippedPresent) {
        Multimap<Holder<Attribute>, AttributeModifier> multimap = BackpackAbilities.ABILITIES.getAttributeAbilityMultimap(stack);
        if(!multimap.isEmpty()) {
            if(!whenEquippedPresent) {
                componentConsumer.accept(Component.translatable("ability.travelersbackpack.when_equipped").withStyle(ChatFormatting.DARK_PURPLE));
            }
            for(Map.Entry<Holder<Attribute>, AttributeModifier> entry : multimap.entries()) {
                Holder<Attribute> attribute = entry.getKey();
                AttributeModifier modifier = entry.getValue();
                double d = modifier.amount();
                double e;
                if(modifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_BASE && modifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                    if(attribute.is(Attributes.KNOCKBACK_RESISTANCE)) {
                        e = d * (double)10.0F;
                    } else {
                        e = d;
                    }
                } else {
                    e = d * (double)100.0F;
                }

                if(d > (double)0.0F) {
                    componentConsumer.accept(Component.translatable("attribute.modifier.plus." + modifier.operation().id(), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(e), Component.translatable(attribute.value().getDescriptionId())).withStyle(attribute.value().getStyle(true)));
                } else if(d < (double)0.0F) {
                    componentConsumer.accept(Component.translatable("attribute.modifier.take." + modifier.operation().id(), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(-e), Component.translatable(attribute.value().getDescriptionId())).withStyle(attribute.value().getStyle(false)));
                }
            }
        }
    }

    @Nullable
    public Entity createEntity(Level level, Entity entity, ItemStack itemstack) {
        if(!(entity instanceof ItemEntity itemEntity)) {
            return null;
        }
        if(!hasCustomData(itemEntity.getItem())) {
            return null;
        }

        return createBackpackEntity(level, itemEntity, itemstack);
    }

    public boolean hasCustomData(ItemStack stack) {
        if(stack.getOrDefault(ModDataComponents.BACKPACK_CONTAINER, BackpackContainerContents.fromItems(0, List.of())).getItems().stream().anyMatch(itemStack -> !itemStack.isEmpty())) {
            return true;
        }
        NonNullList<ItemStack> upgrades = stack.getOrDefault(ModDataComponents.UPGRADES, BackpackContainerContents.fromItems(0, List.of())).getItems();
        if(upgrades.stream().anyMatch(itemStack -> !itemStack.isEmpty() && !itemStack.is(ModItems.TANKS_UPGRADE)) && upgrades.stream().anyMatch(itemStack -> itemStack.is(ModItems.TANKS_UPGRADE))) {
            return true;
        }
        if(stack.getOrDefault(ModDataComponents.TOOLS_CONTAINER, BackpackContainerContents.fromItems(0, List.of())).getItems().stream().anyMatch(itemStack -> !itemStack.isEmpty())) {
            return true;
        }
        return stack.getOrDefault(ModDataComponents.TIER, 0) >= Tiers.DIAMOND.getOrdinal();
    }

    @Nullable
    private BackpackItemEntity createBackpackEntity(Level level, ItemEntity itemEntity, ItemStack itemstack) {
        BackpackItemEntity backpackItemEntity = ModItems.BACKPACK_ITEM_ENTITY.create(level, EntitySpawnReason.TRIGGERED);
        if(backpackItemEntity != null) {
            backpackItemEntity.setPos(itemEntity.getX(), itemEntity.getY(), itemEntity.getZ());
            backpackItemEntity.setItem(itemstack.copy());
            backpackItemEntity.setPickUpDelay(itemEntity.pickupDelay);
            if(itemEntity.getOwner() != null) {
                backpackItemEntity.setThrower(itemEntity.getOwner());
            }
            backpackItemEntity.setDeltaMovement(itemEntity.getDeltaMovement());
        }
        return backpackItemEntity;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack pStack) {
        return Optional.of(new BackpackTooltipComponent(pStack));
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    public static void registerCauldronInteraction() {
        CauldronInteraction.WATER.map().put(ModItems.STANDARD_TRAVELERS_BACKPACK, TravelersBackpackItem::dyedItemIteration);
    }

    private static InteractionResult dyedItemIteration(
            BlockState p_364488_, Level p_363832_, BlockPos p_363503_, Player p_362213_, InteractionHand p_360757_, ItemStack p_360363_
    ) {
        if(!p_360363_.is(ItemTags.DYEABLE)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        } else if(!p_360363_.has(DataComponents.DYED_COLOR)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        } else {
            if(!p_363832_.isClientSide()) {
                p_360363_.remove(DataComponents.DYED_COLOR);
                LayeredCauldronBlock.lowerFillLevel(p_364488_, p_363832_, p_363503_);
            }

            return InteractionResult.SUCCESS;
        }
    }
}