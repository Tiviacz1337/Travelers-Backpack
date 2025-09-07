package com.tiviacz.travelersbackpack.items;

import com.google.common.collect.Multimap;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.advancements.ActionTypeTrigger;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.renderer.BackpackItemStackRenderer;
import com.tiviacz.travelersbackpack.client.screens.tooltip.BackpackTooltipComponent;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.common.recipes.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.compat.curios.TravelersBackpackCurio;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.entity.BackpackItemEntity;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.BackpackContainer;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.util.KeyHelper;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import com.tiviacz.travelersbackpack.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TravelersBackpackItem extends BlockItem {
    public static final DecimalFormat ATTRIBUTE_MODIFIER_FORMAT = Util.make(
            new DecimalFormat("#.##"), decimalFormat -> decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT))
    );
    @Deprecated(forRemoval = true)
    public final ResourceLocation texture;

    public TravelersBackpackItem(Block block) {
        this(block, "");
    }

    @Deprecated(forRemoval = true)
    public TravelersBackpackItem(Block block, String name) {
        this(block, new ResourceLocation(TravelersBackpack.MODID, "textures/model/" + name.toLowerCase(Locale.ENGLISH) + ".png"));
    }

    @Deprecated(forRemoval = true)
    public TravelersBackpackItem(Block block, ResourceLocation texture) {
        super(block, new Properties().stacksTo(1));

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
            if(!CapabilityUtils.isWearingBackpack(player) && !TravelersBackpack.enableIntegration()) {
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
                            blockEntity.setBackpack(itemstack);
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
            if(pStack.getTag() == null) {
                return false;
            }
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if(blockEntity != null && blockEntity instanceof BackpackBlockEntity backpackBlockEntity) {
                if(pLevel.isClientSide || !blockEntity.onlyOpCanSetNbt() || pPlayer != null && pPlayer.canUseGameMasterBlocks()) {
                    if(pStack.hasCustomHoverName()) {
                        backpackBlockEntity.setCustomName(pStack.getHoverName());
                    }
                    blockEntity.setChanged();
                    return true;
                }
            }
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Level context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        if(NbtHelper.has(stack, ModDataHelper.TIER)) {
            tooltipComponents.add(Component.translatable("tier.travelersbackpack." + Tiers.of((int)NbtHelper.get(stack, ModDataHelper.TIER)).getName()));
        }

        if(NbtHelper.has(stack, ModDataHelper.BACKPACK_CONTAINER) && !KeyHelper.isCtrlPressed()) {
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
                        mutablecomponent.append(Component.translatable(entry.getValue().effect().getDescriptionId()));
                        MobEffect mobeffect = entry.getValue().effect();
                        if(entry.getValue().amplifier() > 0) {
                            mutablecomponent = Component.translatable("potion.withAmplifier", mutablecomponent, Component.translatable("potion.potency." + entry.getValue().amplifier()));
                        }
                        if(BackpackAbilities.getCooldowns().containsKey(stack.getItem())) {
                            mutablecomponent.append(" " + TextUtils.getConvertedTime(entry.getValue().minDuration()));
                        }
                        tooltipComponents.add(mutablecomponent.withStyle(mobeffect.getCategory().getTooltipFormatting()));
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

    public void addAttributeModifierTooltip(ItemStack stack, List<Component> tooltipComponents, boolean whenEquippedPresent) {
        Multimap<Attribute, AttributeModifier> multimap = BackpackAbilities.ABILITIES.getAttributeAbilityMultimap(stack);
        if(!multimap.isEmpty()) {
            if(!whenEquippedPresent) {
                tooltipComponents.add(Component.translatable("ability.travelersbackpack.when_equipped").withStyle(ChatFormatting.DARK_PURPLE));
            }

            for(Map.Entry<Attribute, AttributeModifier> entry : multimap.entries()) {
                AttributeModifier attributeModifier = entry.getValue();
                double d = attributeModifier.getAmount();
                boolean bl = false;
                double e;
                if(attributeModifier.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE
                        || attributeModifier.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) {
                    e = d * 100.0;
                } else if(entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
                    e = d * 10.0;
                } else {
                    e = d;
                }

                if(bl) {
                    tooltipComponents.add(
                            CommonComponents.space()
                                    .append(
                                            Component.translatable(
                                                    "attribute.modifier.equals." + attributeModifier.getOperation().toValue(),
                                                    ATTRIBUTE_MODIFIER_FORMAT.format(e),
                                                    Component.translatable(entry.getKey().getDescriptionId())
                                            )
                                    )
                                    .withStyle(ChatFormatting.DARK_GREEN)
                    );
                } else if(d > 0.0) {
                    tooltipComponents.add(
                            Component.translatable(
                                            "attribute.modifier.plus." + attributeModifier.getOperation().toValue(),
                                            ATTRIBUTE_MODIFIER_FORMAT.format(e),
                                            Component.translatable(entry.getKey().getDescriptionId())
                                    )
                                    .withStyle(ChatFormatting.BLUE)
                    );
                } else if(d < 0.0) {
                    e *= -1.0;
                    tooltipComponents.add(
                            Component.translatable(
                                            "attribute.modifier.take." + attributeModifier.getOperation().toValue(),
                                            ATTRIBUTE_MODIFIER_FORMAT.format(e),
                                            Component.translatable(entry.getKey().getDescriptionId())
                                    )
                                    .withStyle(ChatFormatting.RED)
                    );
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
        if(NbtHelper.getOrDefault(stack, ModDataHelper.BACKPACK_CONTAINER, NonNullList.withSize(0, ItemStack.EMPTY)).stream().anyMatch(itemStack -> !itemStack.isEmpty())) {
            return true;
        }
        NonNullList<ItemStack> upgrades = NbtHelper.getOrDefault(stack, ModDataHelper.UPGRADES, NonNullList.withSize(0, ItemStack.EMPTY));
        if(upgrades.stream().anyMatch(itemStack -> !itemStack.isEmpty() && !itemStack.is(ModItems.TANKS_UPGRADE.get())) && upgrades.stream().anyMatch(itemStack -> itemStack.is(ModItems.TANKS_UPGRADE.get()))) {
            return true;
        }
        if(NbtHelper.getOrDefault(stack, ModDataHelper.TOOLS_CONTAINER, NonNullList.withSize(0, ItemStack.EMPTY)).stream().anyMatch(itemStack -> !itemStack.isEmpty())) {
            return true;
        }
        return NbtHelper.getOrDefault(stack, ModDataHelper.TIER, 0) >= Tiers.DIAMOND.getOrdinal();
    }

    @Nullable
    private BackpackItemEntity createBackpackEntity(Level level, ItemEntity itemEntity, ItemStack itemstack) {
        BackpackItemEntity backpackItemEntity = ModItems.BACKPACK_ITEM_ENTITY.get().create(level);
        if(backpackItemEntity != null) {
            backpackItemEntity.setPos(itemEntity.getX(), itemEntity.getY(), itemEntity.getZ());
            backpackItemEntity.setItem(itemstack.copy());
            backpackItemEntity.setPickUpDelay(itemEntity.pickupDelay);
            if(itemEntity.getOwner() != null) {
                backpackItemEntity.setThrower(itemEntity.getOwner().getUUID());
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

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        final Supplier<BlockEntityWithoutLevelRenderer> renderer = () -> new BackpackItemStackRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());

        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer.get();
            }
        });
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if(TravelersBackpack.enableCurios()) {
            return new ICapabilityProvider() {
                final LazyOptional<ICurio> curio = LazyOptional.of(() -> new TravelersBackpackCurio(stack));

                @Nonnull
                @Override
                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                    return CuriosCapability.ITEM.orEmpty(cap, curio);
                }
            };
        }
        return null;
    }

    public static void registerCauldronInteraction() {
        CauldronInteraction.WATER.put(ModItems.STANDARD_TRAVELERS_BACKPACK.get(), DYED_BACKPACK);
    }

    public static CauldronInteraction DYED_BACKPACK = (state, level, pos, player, hand, stack) -> {
        Item item = stack.getItem();
        if(!(item instanceof TravelersBackpackItem)) {
            return InteractionResult.PASS;
        } else if(!BackpackDyeRecipe.hasColor(stack)) {
            return InteractionResult.PASS;
        } else {
            if(!level.isClientSide) {
                stack.getTag().remove(ModDataHelper.COLOR);
                if(player instanceof ServerPlayer serverPlayer) {
                    ActionTypeTrigger.INSTANCE.trigger(serverPlayer, ActionTypeTrigger.UNDYE_BACKPACK);
                }
                LayeredCauldronBlock.lowerFillLevel(state, level, pos);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    };
}