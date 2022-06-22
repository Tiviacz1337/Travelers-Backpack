package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.common.TravelersBackpackItemGroup;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.tileentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TravelersBackpackItem extends BlockItem
{
    public TravelersBackpackItem(Block block)
    {
        super(block, new Settings().fireproof().maxCount(1).group(TravelersBackpackItemGroup.INSTANCE));
    }

    @Override
    public Text getName(ItemStack stack)
    {
        return new TranslatableText(this.getTranslationKey(stack)).append(" ").append(new TranslatableText("block.travelersbackpack.travelers_backpack"));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
    {
        if(TravelersBackpackConfig.obtainTips)
        {
            if(stack.getItem() == ModItems.BAT_TRAVELERS_BACKPACK)
            {
                tooltip.add(new TranslatableText("obtain.travelersbackpack.bat").formatted(Formatting.BLUE));
            }

            if(stack.getItem() == ModItems.VILLAGER_TRAVELERS_BACKPACK)
            {
                tooltip.add(new TranslatableText("obtain.travelersbackpack.villager").formatted(Formatting.BLUE));
            }
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context)
    {
        ActionResult actionresult = this.place(new ItemPlacementContext(context));
        return !actionresult.isAccepted() ? this.use(context.getWorld(), context.getPlayer(), context.getHand()).getResult() : actionresult;
    }

    @Override
    public ActionResult place(ItemPlacementContext context)
    {
        if(!context.canPlace() || (context.getHand() == Hand.MAIN_HAND && !context.getPlayer().isSneaking()))
        {
            return ActionResult.FAIL;
        }
        else
        {
            ItemPlacementContext itemPlacementContext = this.getPlacementContext(context);

            if(itemPlacementContext == null)
            {
                return ActionResult.FAIL;
            }
            else
            {
                BlockState blockState = this.getPlacementState(itemPlacementContext);

                if(blockState == null)
                {
                    return ActionResult.FAIL;
                }
                else if(!this.place(itemPlacementContext, blockState))
                {
                    return ActionResult.FAIL;
                }
                else
                {
                    BlockPos blockPos = itemPlacementContext.getBlockPos();
                    World world = itemPlacementContext.getWorld();
                    PlayerEntity playerEntity = itemPlacementContext.getPlayer();
                    ItemStack itemStack = itemPlacementContext.getStack();
                    BlockState blockState2 = world.getBlockState(blockPos);

                    if(blockState2.isOf(blockState.getBlock()))
                    {
                        this.postPlacement(blockPos, world, playerEntity, itemStack, blockState2);
                        blockState2.getBlock().onPlaced(world, blockPos, blockState2, playerEntity, itemStack);

                        if(itemStack.getNbt() != null && world.getBlockEntity(blockPos) instanceof TravelersBackpackBlockEntity)
                        {
                            ((TravelersBackpackBlockEntity)world.getBlockEntity(blockPos)).readAllData(itemStack.getNbt());

                            if(itemStack.hasCustomName())
                            {
                                ((TravelersBackpackBlockEntity)world.getBlockEntity(blockPos)).setCustomName(itemStack.getName());
                            }
                        }

                        if(playerEntity instanceof ServerPlayerEntity)
                        {
                            Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos, itemStack);
                        }
                    }

                    BlockSoundGroup blockSoundGroup = blockState2.getSoundGroup();
                    world.playSound(playerEntity, blockPos, this.getPlaceSound(blockState2), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F);
                    world.emitGameEvent(playerEntity, GameEvent.BLOCK_PLACE, blockPos);

                    if(playerEntity == null || !playerEntity.getAbilities().creativeMode)
                    {
                        itemStack.decrement(1);
                    }

                    return ActionResult.success(world.isClient);
                }
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        ItemStack itemstack = user.getStackInHand(hand);

        if(!world.isClient)
        {
            if(hand == Hand.MAIN_HAND)
            {
                if(itemstack.getItem() == this && !user.isSneaking())
                {
                    TravelersBackpackInventory.openGUI(user, user.getMainHandStack(), Reference.TRAVELERS_BACKPACK_ITEM_SCREEN_ID);
                }
            }
        }
        return TypedActionResult.pass(itemstack);
    }
}