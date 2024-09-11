package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screen.tooltip.BackpackTooltipData;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModComponentTypes;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class TravelersBackpackItem extends BlockItem
{
    public final Identifier texture;

    //For external backpacks, provide Identifier for your backpack texture
    public TravelersBackpackItem(Block block, Identifier texture)
    {
        super(block, new Settings().fireproof().maxCount(1)
                .component(ModComponentTypes.TIER, 0)); // Tier

        //Texture location
        this.texture = texture;
    }

    //Internal only
    public TravelersBackpackItem(Block block, String name)
    {
        super(block, new Settings().fireproof().maxCount(1)
                .component(ModComponentTypes.TIER, 0)); // Tier

        this.texture = Identifier.of(TravelersBackpack.MODID, "textures/model/" + name.toLowerCase(Locale.ENGLISH) + ".png");
    }

    public Identifier getBackpackTexture()
    {
        return this.texture;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType tooltipType)
    {
        if(stack.contains(ModComponentTypes.TIER))
        {
            tooltip.add(Text.translatable("tier.travelersbackpack." + Tiers.of(stack.get(ModComponentTypes.TIER)).getName()));
        }

        if(stack.contains(ModComponentTypes.BACKPACK_CONTAINER) && !BackpackUtils.isCtrlPressed())
        {
            tooltip.add(Text.translatable("item.travelersbackpack.inventory_tooltip").formatted(Formatting.BLUE));
        }

        if(TravelersBackpackConfig.getConfig().client.obtainTips)
        {
            if(stack.getItem() == ModItems.BAT_TRAVELERS_BACKPACK)
            {
                tooltip.add(Text.translatable("obtain.travelersbackpack.bat").formatted(Formatting.BLUE));
            }

            if(stack.getItem() == ModItems.VILLAGER_TRAVELERS_BACKPACK)
            {
                tooltip.add(Text.translatable("obtain.travelersbackpack.villager").formatted(Formatting.BLUE));
            }

            if(stack.getItem() == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK)
            {
                tooltip.add(Text.translatable("obtain.travelersbackpack.iron_golem").formatted(Formatting.BLUE));
            }
        }

        if(BackpackAbilities.isOnList(BackpackAbilities.ALL_ABILITIES_LIST, stack))
        {
            if(BackpackUtils.isShiftPressed())
            {
                tooltip.add(Text.translatable("ability.travelersbackpack." + this.getTranslationKey(stack).replaceAll("block.travelersbackpack.", "")).formatted(Formatting.BLUE));

                if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, stack) && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, stack))
                {
                    tooltip.add(Text.translatable("ability.travelersbackpack.item_and_block"));
                }
                else if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, stack) && !BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, stack))
                {
                    tooltip.add(Text.translatable("ability.travelersbackpack.block"));
                }
                else if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, stack) && !BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, stack))
                {
                    tooltip.add(Text.translatable("ability.travelersbackpack.item"));
                }
            }
            else
            {
                tooltip.add(Text.translatable("ability.travelersbackpack.hold_shift").formatted(Formatting.BLUE));
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
    public ActionResult place(ItemPlacementContext context) {
        if (!this.getBlock().isEnabled(context.getWorld().getEnabledFeatures())) {
            return ActionResult.FAIL;
        }

        if (!context.canPlace() || (context.getHand() == Hand.MAIN_HAND && context.getPlayer() != null && !context.getPlayer().isSneaking())) {
            return ActionResult.FAIL;
        }
        ItemPlacementContext itemPlacementContext = this.getPlacementContext(context);
        if (itemPlacementContext == null) {
            return ActionResult.FAIL;
        }
        BlockState blockState = this.getPlacementState(itemPlacementContext);
        if (blockState == null) {
            return ActionResult.FAIL;
        }
        if (!this.place(itemPlacementContext, blockState)) {
            return ActionResult.FAIL;
        }

        BlockPos blockPos = itemPlacementContext.getBlockPos();
        World world = itemPlacementContext.getWorld();
        PlayerEntity playerEntity = itemPlacementContext.getPlayer();
        ItemStack itemStack = itemPlacementContext.getStack();
        BlockState blockState2 = world.getBlockState(blockPos);

        if (blockState2.isOf(blockState.getBlock())) {

            this.postPlacement(blockPos, world, playerEntity, itemStack, blockState2);
            blockState2.getBlock().onPlaced(world, blockPos, blockState2, playerEntity, itemStack);

            if (playerEntity instanceof ServerPlayerEntity serverPlayer) {
                Criteria.PLACED_BLOCK.trigger(serverPlayer, blockPos, itemStack);
            }
        }

        BlockSoundGroup blockSoundGroup = blockState2.getSoundGroup();
        world.playSound(playerEntity, blockPos, this.getPlaceSound(blockState2), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F);
        world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(playerEntity, blockState2));

        if (playerEntity == null || !playerEntity.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }

        return ActionResult.success(world.isClient);
    }

    @Override
    protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state)
    {
        return writeNbtToBlockEntity(world, player, pos, stack);
    }

    public static boolean writeNbtToBlockEntity(World world, @Nullable PlayerEntity player, BlockPos pos, ItemStack stack)
    {
        MinecraftServer minecraftServer = world.getServer();
        if(minecraftServer == null)
        {
            return false;
        }
        else
        {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity != null)
            {
                if(world.isClient || !blockEntity.copyItemDataRequiresOperator() || player != null && player.isCreativeLevelTwoOp())
                {
                    blockEntity.readComponents(stack);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        ItemStack itemstack = user.getStackInHand(hand);

        if(hand == Hand.OFF_HAND || user.isSneaking())
        {
            return TypedActionResult.fail(itemstack);
        }

        if(!TravelersBackpackConfig.getConfig().backpackSettings.allowOnlyEquippedBackpack)
        {
            if(!world.isClient)
            {
                TravelersBackpackInventory.openHandledScreen(user, user.getMainHandStack(), Reference.ITEM_SCREEN_ID);
            }
        }
        else
        {
            if(!ComponentUtils.isWearingBackpack(user))
            {
                ServerActions.equipBackpack(user);
                user.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            }
        }
        return TypedActionResult.success(itemstack, world.isClient);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack)
    {
        return Optional.of(new BackpackTooltipData(stack));
    }

    @Override
    public boolean canBeNested()
    {
        return false;
    }

    public static void registerCauldronBehavior()
    {
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.map().put(ModItems.STANDARD_TRAVELERS_BACKPACK, CauldronBehavior.CLEAN_DYEABLE_ITEM);
    }
}