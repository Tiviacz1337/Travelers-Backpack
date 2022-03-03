package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackItemStackRenderer;
import com.tiviacz.travelersbackpack.compat.curios.TravelersBackpackCurios;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.tileentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
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
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TravelersBackpackItem extends BlockItem
{
    public TravelersBackpackItem(Block block)
    {
        super(block, new Item.Properties().tab(Reference.TAB_TRAVELERS_BACKPACK).stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack itemstack = player.getItemInHand(hand);

        if(!level.isClientSide)
        {
            if(hand == InteractionHand.MAIN_HAND)
            {
                if(itemstack.getItem() == this && !player.isCrouching())
                {
                    TravelersBackpackContainer.openGUI((ServerPlayer) player, player.getInventory().getSelected(), Reference.TRAVELERS_BACKPACK_ITEM_SCREEN_ID);
                }
            }
        }
        return InteractionResultHolder.pass(itemstack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        InteractionResult interactionResult = this.place(new BlockPlaceContext(context));
        return !interactionResult.consumesAction() ? this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult() : interactionResult;

   /*     BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();

        if(context.getHand() == Hand.MAIN_HAND && !player.isSneaking())
        {
            return ActionResultType.FAIL;
        }

        BlockState blockState = context.getWorld().getBlockState(context.getPos());

        if(!blockState.isReplaceable(new BlockItemUseContext(context)))
        {
            pos = pos.offset(context.getFace());
        }

        ItemStack stack = player.getHeldItem(context.getHand());

        if(!stack.isEmpty() && player.canPlayerEdit(pos, context.getPlacementHorizontalFacing(), stack))
        {
            BlockState blockState1 = block.getStateForPlacement(new BlockItemUseContext(context));

            if(placeBlockAt(stack, player, world, pos, blockState1))
            {
                if(stack.getTag() != null)
                {
                    if(world.getTileEntity(pos) instanceof TravelersBackpackTileEntity)
                    {
                        ((TravelersBackpackTileEntity)world.getTileEntity(pos)).loadAllData(stack.getTag());
                    }
                    //((TravelersBackpackTileEntity)world.getTileEntity(pos)).loadAllData(stack.getTag());
                }
                blockState1 = world.getBlockState(pos);
                SoundType soundtype = blockState1.getBlock().getSoundType(blockState1, world, pos, player);
                world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                stack.shrink(1);
            }
            return ActionResultType.SUCCESS;
        }
        else
        {
            return ActionResultType.FAIL;
        } */
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
                        this.updateCustomBlockEntityTag(blockpos, level, player, itemstack, blockstate1);
                        blockstate1.getBlock().setPlacedBy(level, blockpos, blockstate1, player, itemstack);

                        if(itemstack.getTag() != null && level.getBlockEntity(blockpos) instanceof TravelersBackpackBlockEntity)
                        {
                            ((TravelersBackpackBlockEntity)level.getBlockEntity(blockpos)).loadAllData(itemstack.getTag());

                            if(itemstack.hasCustomHoverName())
                            {
                                ((TravelersBackpackBlockEntity)level.getBlockEntity(blockpos)).setCustomName(itemstack.getHoverName());
                            }
                        }

                        if(player instanceof ServerPlayer)
                        {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
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

  /*  public boolean placeBlockAt(ItemStack stack, PlayerEntity player, World world, BlockPos pos, BlockState newState)
    {
        if(!world.setBlockState(pos, newState, 11))
        {
            return false;
        }

        BlockState state = world.getBlockState(pos);

        if(state.getBlock() == block)
        {
            setTileEntityNBT(world, player, pos, stack);
            block.onBlockPlacedBy(world, pos, state, player, stack);

            if(player instanceof ServerPlayerEntity)
            {
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)player, pos, stack);
            }
        }
        return true;
    } */

    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getName(ItemStack stack)
    {
        //return new TranslatableComponent("block.travelersbackpack.travelers_backpack");
        return new TranslatableComponent(this.getDescriptionId(stack)).append(" ").append(new TranslatableComponent("block.travelersbackpack.travelers_backpack"));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
    {
        //tooltip.add(new TranslatableComponent(this.getDescriptionId()).withStyle(ChatFormatting.BLUE));

        if(TravelersBackpackConfig.CLIENT.obtainTips.get())
        {
            if(stack.getItem() == ModItems.BAT_TRAVELERS_BACKPACK.get())
            {
                tooltip.add(new TranslatableComponent("obtain.travelersbackpack.bat").withStyle(ChatFormatting.BLUE));
            }

            if(stack.getItem() == ModItems.VILLAGER_TRAVELERS_BACKPACK.get())
            {
                tooltip.add(new TranslatableComponent("obtain.travelersbackpack.villager").withStyle(ChatFormatting.BLUE));
            }
        }
    }

    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.IItemRenderProperties> consumer)
    {
        super.initializeClient(consumer);

        consumer.accept(new IItemRenderProperties()
        {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer()
            {
                return new TravelersBackpackItemStackRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels(), () -> new TravelersBackpackBlockEntity(BlockPos.ZERO, ModBlocks.STANDARD_TRAVELERS_BACKPACK.get().defaultBlockState()));
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

    //Special

/*    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        Item item = stack.getItem();
        boolean flag = item == ModItems.NETHERITE_TRAVELERS_BACKPACK.get() ||
                item == ModItems.DIAMOND_TRAVELERS_BACKPACK.get() ||
                item == ModItems.GOLD_TRAVELERS_BACKPACK.get() ||
                item == ModItems.IRON_TRAVELERS_BACKPACK.get();

        if(flag)
        {
            return enchantment == Enchantments.INFINITY;
        }
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack)
    {
        Item item = stack.getItem();
        boolean flag = item == ModItems.NETHERITE_TRAVELERS_BACKPACK.get() ||
                item == ModItems.DIAMOND_TRAVELERS_BACKPACK.get() ||
                item == ModItems.GOLD_TRAVELERS_BACKPACK.get() ||
                item == ModItems.IRON_TRAVELERS_BACKPACK.get();

        return flag;
    }

    @Override
    public int getItemEnchantability() {
        return 30;
    } */
}
