package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackItemStackRenderer;
import com.tiviacz.travelersbackpack.client.screens.tooltip.BackpackTooltipComponent;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.entity.BackpackItemEntity;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.util.NonNullLazy;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class TravelersBackpackItem extends BlockItem
{
    public final ResourceLocation texture;

    //For external backpacks, provide ResourceLocation for your backpack texture
    public TravelersBackpackItem(Block block, ResourceLocation texture)
    {
        super(block, new Properties().stacksTo(1)
                .component(ModDataComponents.TIER.get(), 0) // Tier
                .component(ModDataComponents.VISIBILITY.get(), true)); //Visibility

        //Texture location
        this.texture = texture;
    }

    //Internal only
    public TravelersBackpackItem(Block block, String name)
    {
        super(block, new Properties().stacksTo(1)
                .component(ModDataComponents.TIER.get(), 0) // Tier
                .component(ModDataComponents.VISIBILITY.get(), true)); //Visibility

        this.texture = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/" + name.toLowerCase(Locale.ENGLISH) + ".png");
    }

    public ResourceLocation getBackpackTexture()
    {
        return this.texture;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack itemstack = player.getItemInHand(hand);

        if(hand == InteractionHand.OFF_HAND || player.isCrouching())
        {
            return InteractionResultHolder.fail(itemstack);
        }

        if(!TravelersBackpackConfig.SERVER.backpackSettings.allowOnlyEquippedBackpack.get())
        {
            if(!level.isClientSide)
            {
                TravelersBackpackContainer.openGUI((ServerPlayer) player, player.getInventory().getSelected(), Reference.ITEM_SCREEN_ID);
            }
        }
        else
        {
            if(!CapabilityUtils.isWearingBackpack(player))
            {
                ServerActions.equipBackpack(player);
                player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }
        }
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        InteractionResult interactionResult = this.place(new BlockPlaceContext(context));
        return !interactionResult.consumesAction() ? this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult() : interactionResult;
    }

    @Override
    public InteractionResult place(BlockPlaceContext context)
    {
        if(!context.canPlace() || (context.getHand() == InteractionHand.MAIN_HAND && context.getPlayer() != null && !context.getPlayer().isCrouching()))
        {
            return InteractionResult.FAIL;
        }
        else
        {
            BlockPlaceContext blockitemusecontext = this.updatePlacementContext(context);

            if(blockitemusecontext == null)
            {
                return InteractionResult.FAIL;
            }
            else
            {
                BlockState blockstate = this.getPlacementState(blockitemusecontext);

                if(blockstate == null)
                {
                    return InteractionResult.FAIL;
                }

                else if(!this.placeBlock(blockitemusecontext, blockstate))
                {
                    return InteractionResult.FAIL;
                }
                else
                {
                    BlockPos blockpos = blockitemusecontext.getClickedPos();
                    Level level = blockitemusecontext.getLevel();
                    Player player = blockitemusecontext.getPlayer();
                    ItemStack itemstack = blockitemusecontext.getItemInHand();
                    BlockState blockstate1 = level.getBlockState(blockpos);

                    if(blockstate1.is(blockstate.getBlock()))
                    {
                        this.updateCustomBlockEntityTag(blockpos, level, player, itemstack, blockstate1);
                        blockstate1.getBlock().setPlacedBy(level, blockpos, blockstate1, player, itemstack);

                        if(player instanceof ServerPlayer serverPlayer)
                        {
                            CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, blockpos, itemstack);
                        }
                    }

                    level.gameEvent(player, GameEvent.BLOCK_PLACE, blockpos);
                    SoundType soundtype = blockstate1.getSoundType(level, blockpos, context.getPlayer());
                    level.playSound(player, blockpos, this.getPlaceSound(blockstate1, level, blockpos, player), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

                    if(player == null || !player.getAbilities().instabuild)
                    {
                        itemstack.shrink(1);
                    }

                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState)
    {
        return updateCustomBlockEntityTag(pLevel, pPlayer, pPos, pStack);
    }

    public static boolean updateCustomBlockEntityTag(Level pLevel, @Nullable Player pPlayer, BlockPos pPos, ItemStack pStack)
    {
        MinecraftServer minecraftserver = pLevel.getServer();
        if(minecraftserver == null)
        {
            return false;
        }
        else
        {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if(blockEntity != null)
            {
                if(pLevel.isClientSide || !blockEntity.onlyOpCanSetNbt() || pPlayer != null && pPlayer.canUseGameMasterBlocks())
                {
                    blockEntity.applyComponentsFromItemStack(pStack);
                    return true;
                }
            }
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
    {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        if(stack.has(ModDataComponents.TIER.get()))
        {
            tooltipComponents.add(Component.translatable("tier.travelersbackpack." + Tiers.of(stack.get(ModDataComponents.TIER.get())).getName()));
        }

        if(stack.has(ModDataComponents.BACKPACK_CONTAINER.get()) && !BackpackUtils.isCtrlPressed())
        {
            tooltipComponents.add(Component.translatable("item.travelersbackpack.inventory_tooltip").withStyle(ChatFormatting.BLUE));
        }

        if(TravelersBackpackConfig.CLIENT.obtainTips.get())
        {
            if(stack.getItem() == ModItems.BAT_TRAVELERS_BACKPACK.get())
            {
                tooltipComponents.add(Component.translatable("obtain.travelersbackpack.bat").withStyle(ChatFormatting.BLUE));
            }

            if(stack.getItem() == ModItems.VILLAGER_TRAVELERS_BACKPACK.get())
            {
                tooltipComponents.add(Component.translatable("obtain.travelersbackpack.villager").withStyle(ChatFormatting.BLUE));
            }

            if(stack.getItem() == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get())
            {
                tooltipComponents.add(Component.translatable("obtain.travelersbackpack.iron_golem").withStyle(ChatFormatting.BLUE));
            }
        }

        if(BackpackAbilities.isOnList(BackpackAbilities.ALL_ABILITIES_LIST, stack) && (BackpackAbilities.ALLOWED_ABILITIES.contains(stack.getItem()) && TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get()))
        {
            if(BackpackUtils.isShiftPressed())
            {
                tooltipComponents.add(Component.translatable("ability.travelersbackpack." + this.getDescriptionId(stack).replaceAll("block.travelersbackpack.", "")).withStyle(ChatFormatting.BLUE));

                if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, stack) && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, stack))
                {
                    tooltipComponents.add(Component.translatable("ability.travelersbackpack.item_and_block"));
                }
                else if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, stack) && !BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, stack))
                {
                    tooltipComponents.add(Component.translatable("ability.travelersbackpack.block"));
                }
                else if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, stack) && !BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, stack))
                {
                    tooltipComponents.add(Component.translatable("ability.travelersbackpack.item"));
                }
            }
            else
            {
                tooltipComponents.add(Component.translatable("ability.travelersbackpack.hold_shift").withStyle(ChatFormatting.BLUE));
            }
        }
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public Entity createEntity(Level level, Entity entity, ItemStack itemstack) {
        if (!(entity instanceof ItemEntity itemEntity)) {
            return null;
        }

        return createBackpackEntity(level, itemEntity, itemstack);
    }

    @Nullable
    private BackpackItemEntity createBackpackEntity(Level level, ItemEntity itemEntity, ItemStack itemstack) {
        BackpackItemEntity backpackItemEntity = ModItems.BACKPACK_ITEM_ENTITY.get().create(level);
        if (backpackItemEntity != null) {
            backpackItemEntity.setPos(itemEntity.getX(), itemEntity.getY(), itemEntity.getZ());
            backpackItemEntity.setItem(itemstack);
            backpackItemEntity.setPickUpDelay(itemEntity.pickupDelay);
            if (itemEntity.getOwner() != null) {
                backpackItemEntity.setThrower(itemEntity.getOwner());
            }
            backpackItemEntity.setDeltaMovement(itemEntity.getDeltaMovement());
        }
        return backpackItemEntity;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack pStack)
    {
        return Optional.of(new BackpackTooltipComponent(pStack));
    }

    @Override
    public boolean canFitInsideContainerItems()
    {
        return false;
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer)
    {
        super.initializeClient(consumer);

        consumer.accept(new IClientItemExtensions()
        {
            private final NonNullLazy<BlockEntityWithoutLevelRenderer> renderer = NonNullLazy.of(() -> new TravelersBackpackItemStackRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()));

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer()
            {
                return renderer.get();
            }
        });
    }

    public static void registerCauldronInteraction()
    {
        CauldronInteraction.WATER.map().put(ModItems.STANDARD_TRAVELERS_BACKPACK.get(), CauldronInteraction.DYED_ITEM);
    }
}