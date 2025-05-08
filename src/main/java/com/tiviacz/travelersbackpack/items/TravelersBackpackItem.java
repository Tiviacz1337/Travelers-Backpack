package com.tiviacz.travelersbackpack.items;

import com.google.common.collect.Multimap;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.client.screens.tooltip.BackpackTooltipComponent;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.entity.BackpackItemEntity;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.BackpackContainer;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.util.KeyHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import com.tiviacz.travelersbackpack.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class TravelersBackpackItem extends BlockItem {
    @Deprecated(forRemoval = true)
    public final ResourceLocation texture;

    public TravelersBackpackItem(Block block) {
        this(block, "");
    }

    @Deprecated(forRemoval = true)
    public TravelersBackpackItem(Block block, String name) {
        this(block, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/" + name.toLowerCase(Locale.ENGLISH) + ".png"));
    }

    @Deprecated(forRemoval = true)
    public TravelersBackpackItem(Block block, ResourceLocation texture) {
        super(block, new Properties().stacksTo(1)
                .component(ModDataComponents.TIER, 0)
                .component(ModDataComponents.SLEEPING_BAG_COLOR, DyeColor.RED.getId())
                .component(ModDataComponents.IS_VISIBLE, true));

        //Texture location
        this.texture = texture;
    }

    @Deprecated(forRemoval = true)
    public ResourceLocation getBackpackTexture() {
        return this.texture;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if(hand == InteractionHand.OFF_HAND || player.isCrouching()) {
            return InteractionResultHolder.fail(itemstack);
        }

        if(!TravelersBackpackConfig.SERVER.backpackSettings.allowOnlyEquippedBackpack.get()) {
            if(!level.isClientSide) {
                BackpackContainer.openBackpack((ServerPlayer)player, player.getInventory().getSelected(), Reference.ITEM_SCREEN_ID, player.getInventory().selected);
            }
        } else {
            if(!AttachmentUtils.isWearingBackpack(player) && !TravelersBackpack.enableIntegration()) {
                ServerActions.equipBackpack(player);
                player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }
        }
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult interactionResult = this.place(new BlockPlaceContext(context));
        return !interactionResult.consumesAction() ? this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult() : interactionResult;
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
                    SoundType soundtype = blockstate1.getSoundType(level, blockpos, context.getPlayer());
                    level.playSound(player, blockpos, this.getPlaceSound(blockstate1, level, blockpos, player), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

                    if(player == null || !player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }

                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
        return updateCustomBlockEntityTag(pLevel, pPlayer, pPos, pStack);
    }

    public static boolean updateCustomBlockEntityTag(Level pLevel, @Nullable Player pPlayer, BlockPos pPos, ItemStack pStack) {
        MinecraftServer minecraftserver = pLevel.getServer();
        if(minecraftserver == null) {
            return false;
        } else {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if(blockEntity != null) {
                if(pLevel.isClientSide || !blockEntity.onlyOpCanSetNbt() || pPlayer != null && pPlayer.canUseGameMasterBlocks()) {
                    blockEntity.applyComponentsFromItemStack(pStack.copy());
                    return true;
                }
            }
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        if(stack.has(ModDataComponents.TIER)) {
            tooltipComponents.add(Component.translatable("tier.travelersbackpack." + Tiers.of(stack.get(ModDataComponents.TIER)).getName()));
        }

        if(stack.has(ModDataComponents.BACKPACK_CONTAINER) && !KeyHelper.isCtrlPressed()) {
            tooltipComponents.add(Component.translatable("item.travelersbackpack.inventory_tooltip").withStyle(ChatFormatting.BLUE));
        }

        if(TravelersBackpackConfig.CLIENT.obtainTips.get()) {
            if(stack.getItem() == ModItems.BAT_TRAVELERS_BACKPACK.get()) {
                tooltipComponents.add(Component.translatable("obtain.travelersbackpack.bat").withStyle(ChatFormatting.BLUE));
            }
            if(stack.getItem() == ModItems.VILLAGER_TRAVELERS_BACKPACK.get()) {
                tooltipComponents.add(Component.translatable("obtain.travelersbackpack.villager").withStyle(ChatFormatting.BLUE));
            }
            if(stack.getItem() == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get()) {
                tooltipComponents.add(Component.translatable("obtain.travelersbackpack.iron_golem").withStyle(ChatFormatting.BLUE));
            }
        }
        //Check if specific ability is enabled && Check if Abilities are enabled overall
        if(BackpackAbilities.ALLOWED_ABILITIES.contains(stack.getItem()) && TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get()) {
            if(KeyHelper.isShiftPressed()) {
                //Custom Descriptions
                if(BackpackAbilities.CUSTOM_DESCRIPTIONS.contains(stack.getItem())) {
                    tooltipComponents.add(Component.translatable("ability.travelersbackpack." + this.getDescriptionId(stack).replaceAll("block.travelersbackpack.", "")).withStyle(ChatFormatting.BLUE));
                }
                boolean whenEquippedPresent = false;
                //Add descriptions based on BackpackEffects (Can be added)
                if(BackpackAbilities.getBackpackEffects().containsKey(stack.getItem())) {
                    tooltipComponents.add(Component.translatable("ability.travelersbackpack.when_equipped").withStyle(ChatFormatting.DARK_PURPLE));
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
                        tooltipComponents.add(mutablecomponent.withStyle(entry.getValue().effect().value().getCategory().getTooltipFormatting()));
                    });
                }

                //Add attribute modifiers
                addAttributeModifierTooltip(stack, tooltipComponents, whenEquippedPresent);

                //Tooltip to show if ability is available for equipped backpack, block, or both
                if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, stack) && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, stack)) {
                    tooltipComponents.add(Component.translatable("ability.travelersbackpack.item_and_block"));
                } else if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, stack) && !BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, stack)) {
                    tooltipComponents.add(Component.translatable("ability.travelersbackpack.block"));
                } else if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, stack) && !BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, stack)) {
                    tooltipComponents.add(Component.translatable("ability.travelersbackpack.item"));
                }
            } else {
                tooltipComponents.add(Component.translatable("ability.travelersbackpack.hold_shift").withStyle(ChatFormatting.BLUE));
            }
        }
    }

    private void addAttributeModifierTooltip(ItemStack stack, List<Component> components, boolean whenEquippedPresent) {
        Multimap<Holder<Attribute>, AttributeModifier> multimap = BackpackAbilities.ABILITIES.getAttributeAbilityMultimap(stack);
        if(!multimap.isEmpty()) {
            if(!whenEquippedPresent) {
                components.add(Component.translatable("ability.travelersbackpack.when_equipped").withStyle(ChatFormatting.DARK_PURPLE));
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
                    components.add(Component.translatable("attribute.modifier.plus." + modifier.operation().id(), new Object[]{ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(e), Component.translatable(((Attribute)attribute.value()).getDescriptionId())}).withStyle(((Attribute)attribute.value()).getStyle(true)));
                } else if(d < (double)0.0F) {
                    components.add(Component.translatable("attribute.modifier.take." + modifier.operation().id(), new Object[]{ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(-e), Component.translatable(((Attribute)attribute.value()).getDescriptionId())}).withStyle(((Attribute)attribute.value()).getStyle(false)));
                }
            }
        }
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return hasCustomData(stack);
    }

    @Nullable
    @Override
    public Entity createEntity(Level level, Entity entity, ItemStack itemstack) {
        if(!(entity instanceof ItemEntity itemEntity)) {
            return null;
        }
        return createBackpackEntity(level, itemEntity, itemstack);
    }

    public boolean hasCustomData(ItemStack stack) {
        if(stack.getOrDefault(ModDataComponents.BACKPACK_CONTAINER, BackpackContainerContents.fromItems(0, List.of())).getItems().stream().anyMatch(itemStack -> !itemStack.isEmpty())) {
            return true;
        }
        NonNullList<ItemStack> upgrades = stack.getOrDefault(ModDataComponents.UPGRADES.get(), BackpackContainerContents.fromItems(0, List.of())).getItems();
        if(upgrades.stream().anyMatch(itemStack -> !itemStack.isEmpty() && !itemStack.is(ModItems.TANKS_UPGRADE.get())) && upgrades.stream().anyMatch(itemStack -> itemStack.is(ModItems.TANKS_UPGRADE.get()))) {
            return true;
        }
        if(stack.getOrDefault(ModDataComponents.TOOLS_CONTAINER, BackpackContainerContents.fromItems(0, List.of())).getItems().stream().anyMatch(itemStack -> !itemStack.isEmpty())) {
            return true;
        }
        if(stack.getOrDefault(ModDataComponents.TIER, 0) >= Tiers.DIAMOND.getOrdinal()) {
            return true;
        }
        return false;
    }

    @Nullable
    private BackpackItemEntity createBackpackEntity(Level level, ItemEntity itemEntity, ItemStack itemstack) {
        BackpackItemEntity backpackItemEntity = ModItems.BACKPACK_ITEM_ENTITY.get().create(level);
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
        CauldronInteraction.WATER.map().put(ModItems.STANDARD_TRAVELERS_BACKPACK.get(), CauldronInteraction.DYED_ITEM);
    }
}