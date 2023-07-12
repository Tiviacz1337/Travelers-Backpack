package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackItemStackRenderer;
import com.tiviacz.travelersbackpack.client.screens.tooltip.BackpackTooltipComponent;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.compat.curios.TravelersBackpackCurios;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModBlocks;
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
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullLazy;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class TravelersBackpackItem extends BlockItem
{
    public TravelersBackpackItem(Block block)
    {
        super(block, new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack itemstack = player.getItemInHand(hand);

        if(hand == InteractionHand.OFF_HAND || player.isCrouching())
        {
            return InteractionResultHolder.fail(itemstack);
        }

        if(!level.isClientSide)
        {
            TravelersBackpackContainer.openGUI((ServerPlayer) player, player.getInventory().getSelected(), Reference.ITEM_SCREEN_ID);
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
        if(!context.canPlace() || (context.getHand() == InteractionHand.MAIN_HAND && !context.getPlayer().isCrouching()))
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
                        updateCustomBlockEntityTag(level, player, blockpos, itemstack);
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

    public static boolean updateCustomBlockEntityTag(Level pLevel, @Nullable Player pPlayer, BlockPos pPos, ItemStack pStack)
    {
        MinecraftServer minecraftserver = pLevel.getServer();
        if(minecraftserver == null)
        {
            return false;
        }
        else
        {
            CompoundTag compoundtag = pStack.getTag();

            if(compoundtag != null)
            {
                if(pLevel.getBlockEntity(pPos) instanceof TravelersBackpackBlockEntity blockEntity)
                {
                    if(!pLevel.isClientSide && blockEntity.onlyOpCanSetNbt() && (pPlayer == null || !pPlayer.canUseGameMasterBlocks()))
                    {
                        return false;
                    }

                    CompoundTag compoundtag1 = blockEntity.saveWithoutMetadata();
                    CompoundTag compoundtag2 = compoundtag1.copy();
                    compoundtag1.merge(compoundtag);

                    if(!compoundtag1.equals(compoundtag2))
                    {
                        if(pStack.hasCustomHoverName())
                        {
                            blockEntity.setCustomName(pStack.getHoverName());
                        }

                        blockEntity.load(compoundtag1);
                        blockEntity.setChanged();
                        return true;
                    }
                }
            }
            return false;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
    {
        if(stack.hasTag())
        {
            if(stack.getTag().contains(Tiers.TIER))
            {
                tooltip.add(Component.translatable("tier.travelersbackpack." + Tiers.of(stack.getTag().getInt(Tiers.TIER)).getName()));
            }

            if(!BackpackUtils.isCtrlPressed())
            {
                tooltip.add(Component.translatable("item.travelersbackpack.inventory_tooltip").withStyle(ChatFormatting.BLUE));
            }
        }

        if(TravelersBackpackConfig.obtainTips)
        {
            if(stack.getItem() == ModItems.BAT_TRAVELERS_BACKPACK.get())
            {
                tooltip.add(Component.translatable("obtain.travelersbackpack.bat").withStyle(ChatFormatting.BLUE));
            }

            if(stack.getItem() == ModItems.VILLAGER_TRAVELERS_BACKPACK.get())
            {
                tooltip.add(Component.translatable("obtain.travelersbackpack.villager").withStyle(ChatFormatting.BLUE));
            }

            if(stack.getItem() == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get())
            {
                tooltip.add(Component.translatable("obtain.travelersbackpack.iron_golem").withStyle(ChatFormatting.BLUE));
            }
        }

        if(BackpackAbilities.isOnList(BackpackAbilities.ALL_ABILITIES_LIST, stack))
        {
            if(BackpackUtils.isShiftPressed())
            {
                tooltip.add(Component.translatable("ability.travelersbackpack." + this.getDescriptionId(stack).replaceAll("block.travelersbackpack.", "")).withStyle(ChatFormatting.BLUE));

                if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, stack) && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, stack))
                {
                    tooltip.add(Component.translatable("ability.travelersbackpack.item_and_block"));
                }
                else if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, stack) && !BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, stack))
                {
                    tooltip.add(Component.translatable("ability.travelersbackpack.block"));
                }
                else if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, stack) && !BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, stack))
                {
                    tooltip.add(Component.translatable("ability.travelersbackpack.item"));
                }
            }
            else
            {
                tooltip.add(Component.translatable("ability.travelersbackpack.hold_shift").withStyle(ChatFormatting.BLUE));
            }
        }
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
            private final NonNullLazy<BlockEntityWithoutLevelRenderer> renderer = NonNullLazy.of(() -> new TravelersBackpackItemStackRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels(), () -> new TravelersBackpackBlockEntity(BlockPos.ZERO, ModBlocks.STANDARD_TRAVELERS_BACKPACK.get().defaultBlockState())));

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer()
            {
                return renderer.get();
            }
        });
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        if(TravelersBackpack.enableCurios())
        {
            return new ICapabilityProvider()
            {
                final LazyOptional<ICurio> curio = LazyOptional.of(TravelersBackpackCurios::createBackpackProvider);

                @Nonnull
                @Override
                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
                {
                    return CuriosCapability.ITEM.orEmpty(cap, curio);
                }
            };
        }
        return null;
    }
}