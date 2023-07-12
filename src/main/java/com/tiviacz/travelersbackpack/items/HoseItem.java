package com.tiviacz.travelersbackpack.items;

import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HoseItem extends Item
{
    public HoseItem(Item.Settings settings)
    {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack)
    {
        if(getHoseMode(stack) == DRINK_MODE)
        {
            return UseAction.DRINK;
        }
        return UseAction.NONE;
    }

    @Override
    public int getMaxUseTime(ItemStack stack)
    {
        return 24;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getStackInHand(hand);

        if(ComponentUtils.isWearingBackpack(player) && hand == Hand.MAIN_HAND)
        {
            //Configure nbt

            if(stack.getNbt() == null)
            {
                this.setCompoundTag(stack);
                return TypedActionResult.pass(stack);
            }

            TravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);
            SingleVariantStorage<FluidVariant> tank = this.getSelectedFluidTank(stack, inv);

            if(getHoseMode(stack) == SUCK_MODE)
            {
                //Pick fluid from block
                BlockHitResult result = raycast(world, player, RaycastContext.FluidHandling.SOURCE_ONLY);

                BlockPos blockpos = result.getBlockPos();
                Direction direction1 = result.getSide();
                BlockPos blockpos1 = blockpos.offset(result.getSide());

                if(world.canPlayerModifyAt(player, blockpos) && player.canPlaceOn(blockpos1, direction1, stack))
                {
                    BlockState blockstate1 = world.getBlockState(blockpos);

                    if(blockstate1.getBlock() instanceof FluidDrainable drainable)
                    {
                        Fluid fluid = blockstate1.getFluidState().getFluid();

                        if(fluid != Fluids.EMPTY && blockstate1.getFluidState().isStill())
                        {
                            FluidVariant fluidStack = FluidVariant.of(fluid);
                            long tankAmount = tank.isResourceBlank() ? 0 : tank.getAmount();
                            boolean canFill = tank.isResourceBlank() || tank.getResource().getFluid().matchesType(fluidStack.getFluid());

                            if(canFill && (FluidConstants.BUCKET + tankAmount <= tank.getCapacity()))
                            {
                                ItemStack actualFluid = drainable.tryDrainFluid(world, blockpos, blockstate1);

                                if(!actualFluid.isEmpty())
                                {
                                    try (Transaction transaction = Transaction.openOuter()) {
                                        // Try to insert, will return how much was actually inserted.
                                        long amountInserted = tank.insert(FluidVariant.of(blockstate1.getFluidState().getFluid()), FluidConstants.BUCKET, transaction);
                                        if (amountInserted == FluidConstants.BUCKET) {
                                            // "Commit" the transaction: this validates all the operations that were part of this transaction.
                                            // You should call this if you are satisfied with the result of the operation, and want to keep it.
                                            world.playSound(player, result.getBlockPos(), fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                            transaction.commit();
                                            inv.markDataDirty(ITravelersBackpackInventory.TANKS_DATA);
                                            return TypedActionResult.success(stack);
                                        } else {
                                            return TypedActionResult.pass(stack);
                                            // Doing nothing "aborts" the transaction: this cancels the insertion.
                                            // You should call this if you are not satisfied with the result of the operation, and want to abort it.
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if(getHoseMode(stack) == DRINK_MODE)
            {
                if(!tank.isResourceBlank())
                {
                    if(EffectFluidRegistry.hasExecutableEffects(tank, world, player))
                    {
                        player.setCurrentHand(Hand.MAIN_HAND);
                        return TypedActionResult.success(stack);
                    }
                }
            }

        }
        return TypedActionResult.pass(stack);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context)
    {
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        Direction direction = context.getSide();
        ItemStack stack = player.getStackInHand(context.getHand());

        if(ComponentUtils.isWearingBackpack(player) && context.getHand() == Hand.MAIN_HAND)
        {
            //Configure nbt

            if(stack.getNbt() == null)
            {
                this.setCompoundTag(stack);
                return ActionResult.PASS;
            }

            Storage<FluidVariant> fluidVariantStorage = null;
            TravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);
            SingleVariantStorage<FluidVariant> tank = this.getSelectedFluidTank(stack, inv);

            if(!world.isClient)
            {
                fluidVariantStorage = FluidStorage.SIDED.find(world, pos, direction);
            }
            if(getHoseMode(stack) == SUCK_MODE)
            {
                //From fluid storage to backpack tank

                if(fluidVariantStorage != null)
                {
                    try(Transaction transaction = Transaction.openOuter())
                    {
                        FluidVariant fluidVariant = StorageUtil.findStoredResource(fluidVariantStorage, tank.isResourceBlank() ? p -> true : p -> p.equals(tank.variant), transaction);

                        if(fluidVariant != null && !fluidVariant.isBlank())
                        {
                            long amountInserted = tank.insert(fluidVariant, FluidConstants.BUCKET, transaction);
                            long amountExtracted = fluidVariantStorage.extract(fluidVariant, FluidConstants.BUCKET, transaction);
                            if(amountInserted == FluidConstants.BUCKET && amountExtracted == FluidConstants.BUCKET)
                            {
                                world.playSound(player, pos, fluidVariant.getFluid().isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                inv.markDataDirty(ITravelersBackpackInventory.TANKS_DATA);
                                transaction.commit();
                                return ActionResult.SUCCESS;
                            }
                            else {
                                return ActionResult.PASS;
                            }
                        }
                    }
                }

                //Pick fluid from block

                BlockHitResult result = raycast(world, player, RaycastContext.FluidHandling.SOURCE_ONLY);

                BlockPos blockpos = result.getBlockPos();
                Direction direction1 = result.getSide();
                BlockPos blockpos1 = blockpos.offset(direction);

                if(world.canPlayerModifyAt(player, blockpos) && player.canPlaceOn(blockpos1, direction1, stack))
                {
                    BlockState blockstate1 = world.getBlockState(blockpos);

                    if(blockstate1.getBlock() instanceof FluidDrainable drainable)
                    {
                        Fluid fluid = blockstate1.getFluidState().getFluid();

                        if(fluid != Fluids.EMPTY)
                        {
                            FluidVariant fluidStack = FluidVariant.of(fluid);
                            long tankAmount = tank.isResourceBlank() ? 0 : tank.getAmount();
                            boolean canFill = tank.isResourceBlank() || tank.getResource().getFluid().matchesType(fluidStack.getFluid());

                            if(canFill && (FluidConstants.BUCKET + tankAmount <= tank.getCapacity()))
                            {
                                ItemStack actualFluid = drainable.tryDrainFluid(world, blockpos, blockstate1);

                                if(!actualFluid.isEmpty())
                                {
                                    try (Transaction transaction = Transaction.openOuter()) {
                                        // Try to insert, will return how much was actually inserted.
                                        long amountInserted = tank.insert(FluidVariant.of(blockstate1.getFluidState().getFluid()), FluidConstants.BUCKET, transaction);
                                        if (amountInserted == FluidConstants.BUCKET) {
                                            // "Commit" the transaction: this validates all the operations that were part of this transaction.
                                            // You should call this if you are satisfied with the result of the operation, and want to keep it.
                                            world.playSound(player, result.getBlockPos(), fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                            transaction.commit();
                                            inv.markDataDirty(ITravelersBackpackInventory.TANKS_DATA);
                                            return ActionResult.SUCCESS;
                                        } else {
                                            return ActionResult.PASS;
                                            // Doing nothing "aborts" the transaction: this cancels the insertion.
                                            // You should call this if you are not satisfied with the result of the operation, and want to abort it.
                                        }
                                    }
                                }
                            }
                        }

                        if(blockstate1.getOrEmpty(Properties.LEVEL_15).isPresent() && blockstate1.get(Properties.LEVEL_15) == 0 && fluid != Fluids.EMPTY)
                        {
                            FluidVariant fluidStack = FluidVariant.of(fluid);
                            long tankAmount = tank.isResourceBlank() ? 0 : tank.getAmount();
                            boolean canFill = tank.isResourceBlank() || tank.getResource().getFluid().matchesType(fluidStack.getFluid());

                            if(canFill && (FluidConstants.BUCKET + tankAmount <= tank.getCapacity()))
                            {
                                ItemStack actualFluid = drainable.tryDrainFluid(world, blockpos, blockstate1);

                                if(!actualFluid.isEmpty())
                                {
                                    try (Transaction transaction = Transaction.openOuter()) {
                                        // Try to insert, will return how much was actually inserted.
                                        long amountInserted = tank.insert(FluidVariant.of(blockstate1.getFluidState().getFluid()), FluidConstants.BUCKET, transaction);
                                        if (amountInserted == FluidConstants.BUCKET) {
                                            // "Commit" the transaction: this validates all the operations that were part of this transaction.
                                            // You should call this if you are satisfied with the result of the operation, and want to keep it.
                                            world.playSound(player, result.getBlockPos(), fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                            transaction.commit();
                                            inv.markDataDirty(ITravelersBackpackInventory.TANKS_DATA);
                                            return ActionResult.SUCCESS;
                                        } else {
                                            return ActionResult.PASS;
                                            // Doing nothing "aborts" the transaction: this cancels the insertion.
                                            // You should call this if you are not satisfied with the result of the operation, and want to abort it.
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(getHoseMode(stack) == SPILL_MODE)
            {
                //From Backpack tank to fluid Storage

                if(fluidVariantStorage != null && !tank.isResourceBlank())
                {
                    FluidVariant variant = tank.getResource();
                    try (Transaction transaction = Transaction.openOuter()) {
                        for (StorageView<FluidVariant> view : fluidVariantStorage.iterable(transaction))
                        {
                            if ((view.isResourceBlank() || view.getResource().equals(variant)) && tank.extract(variant, FluidConstants.BUCKET, transaction) == FluidConstants.BUCKET && fluidVariantStorage.insert(variant, FluidConstants.BUCKET, transaction) == FluidConstants.BUCKET) {
                                world.playSound(player, player.getBlockPos(), tank.getResource().getFluid().isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                inv.markDataDirty(ITravelersBackpackInventory.TANKS_DATA);
                                transaction.commit();
                                return ActionResult.SUCCESS;
                            }
                            else {
                                return ActionResult.PASS;
                            }
                        }
                    }
                }

                //Try to put fluid in the world

                if(!tank.isResourceBlank())
                {
                    BlockState blockState = world.getBlockState(pos);
                    Block block = blockState.getBlock();
                    Fluid fluid = tank.getResource().getFluid();

                    if(tank.getAmount() >= Reference.BUCKET && fluid instanceof FlowableFluid)
                    {
                        if(block instanceof FluidFillable fillable && fillable.canFillWithFluid(world, pos, blockState, fluid))
                        {
                            try (Transaction transaction = Transaction.openOuter()) {
                                long amountExtracted = tank.extract(tank.getResource(), FluidConstants.BUCKET, transaction);
                                if (amountExtracted == FluidConstants.BUCKET) {
                                    world.playSound(player, player.getBlockPos(), fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                    fillable.tryFillWithFluid(world, pos, blockState, fluid.getDefaultState());
                                    transaction.commit();
                                    inv.markDataDirty(ITravelersBackpackInventory.TANKS_DATA);
                                    return ActionResult.SUCCESS;
                                } else {
                                    return ActionResult.PASS;
                                }
                            }
                        }
                    }

                    int x = pos.getX();
                    int y = pos.getY();
                    int z = pos.getZ();

                    if(!world.getBlockState(pos).canBucketPlace(fluid))
                    {
                        switch(context.getSide())
                        {
                            case WEST:
                                --x;
                                break;
                            case EAST:
                                ++x;
                                break;
                            case NORTH:
                                --z;
                                break;
                            case SOUTH:
                                ++z;
                                break;
                            case UP:
                                ++y;
                                break;
                            case DOWN:
                                --y;
                                break;
                            default:
                                break;
                        }
                    }

                    BlockPos newPos = new BlockPos(x,y,z);
                    FluidVariant fluidStack = tank.getResource();

                    if(world.getBlockState(newPos).canBucketPlace(fluid))
                    {
                        Material material = world.getBlockState(newPos).getMaterial();
                        boolean flag = !material.isSolid();

                        if(world.getDimension().isUltrawarm() && fluidStack.getFluid().isIn(FluidTags.WATER))
                        {
                            try (Transaction transaction = Transaction.openOuter()) {
                                long amountExtracted = tank.extract(tank.getResource(), FluidConstants.BUCKET, transaction);
                                if (amountExtracted == FluidConstants.BUCKET) {

                                    world.playSound(null, newPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

                                    for(int i = 0; i < 3; ++i)
                                    {
                                        double d0 = newPos.getX() + world.random.nextDouble();
                                        double d1 = newPos.getY() + world.random.nextDouble() * 0.5D + 0.5D;
                                        double d2 = newPos.getZ() + world.random.nextDouble();
                                        world.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                                    }

                                    transaction.commit();
                                    inv.markDataDirty(ITravelersBackpackInventory.TANKS_DATA);
                                    return ActionResult.SUCCESS;
                                } else {
                                    return ActionResult.PASS;
                                }
                            }
                        }
                        if(tank.getAmount() >= Reference.BUCKET)
                        {
                            if(!world.isClient && flag && !material.isLiquid())
                            {
                                world.removeBlock(newPos, false);
                            }

                            if(world.setBlockState(newPos, fluidStack.getFluid().getDefaultState().getBlockState()))
                            {
                                try (Transaction transaction = Transaction.openOuter()) {
                                    long amountExtracted = tank.extract(tank.getResource(), FluidConstants.BUCKET, transaction);
                                    if (amountExtracted == FluidConstants.BUCKET) {

                                        world.playSound(player, newPos, fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                        world.updateNeighborsAlways(newPos, fluidStack.getFluid().getDefaultState().getBlockState().getBlock());
                                        transaction.commit();
                                        inv.markDataDirty(ITravelersBackpackInventory.TANKS_DATA);
                                        return ActionResult.SUCCESS;
                                    } else {
                                        return ActionResult.PASS;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(getHoseMode(stack) == DRINK_MODE)
            {
                if(!tank.isResourceBlank())
                {
                    if(EffectFluidRegistry.hasExecutableEffects(tank, world, player))
                    {
                        player.setCurrentHand(context.getHand());
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }
        return ActionResult.FAIL;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World worldIn, LivingEntity entityLiving)
    {
        if(entityLiving instanceof PlayerEntity player)
        {
            if(ComponentUtils.isWearingBackpack(player))
            {
                TravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);
                SingleVariantStorage<FluidVariant> tank = this.getSelectedFluidTank(stack, inv);

                if(getHoseMode(stack) == DRINK_MODE)
                {
                    if(tank != null)
                    {
                        long drainAmount = EffectFluidRegistry.getHighestFluidEffectAmount(tank.getResource().getFluid());

                        if(ServerActions.setFluidEffect(worldIn, player, tank))
                        {
                            try (Transaction transaction = Transaction.openOuter()) {
                                long amountExtracted = tank.extract(tank.getResource(), drainAmount, transaction);
                                if (amountExtracted == drainAmount) {
                                    transaction.commit();
                                    inv.markDataDirty(ITravelersBackpackInventory.TANKS_DATA);
                                }
                            }
                        }
                    }
                }
            }
        }
        return stack;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand)
    {
        if(hand == Hand.MAIN_HAND && getHoseMode(stack) == SUCK_MODE)
        {
            TravelersBackpackInventory inv = ComponentUtils.getBackpackInv(user);
            SingleVariantStorage<FluidVariant> tank = this.getSelectedFluidTank(stack, inv);
            FluidVariant milk = FluidVariant.of(ModFluids.MILK_STILL);

            if(milk != null)
            {
                if(entity instanceof CowEntity)
                {
                    try(Transaction transaction = Transaction.openOuter())
                    {
                        long amountInserted = tank.insert(milk, FluidConstants.BUCKET, transaction);

                        if(amountInserted == FluidConstants.BUCKET)
                        {
                            user.world.playSound(user, user.getBlockPos(), SoundEvents.ENTITY_COW_MILK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            inv.markDataDirty(ITravelersBackpackInventory.TANKS_DATA);
                            transaction.commit();
                            return ActionResult.SUCCESS;
                        }
                        else {
                            return ActionResult.PASS;
                        }
                    }
                }
            }
        }
        return ActionResult.PASS;
    }

    public static final int NO_ASSIGN = 0;
    public static final int SUCK_MODE = 1;
    public static final int SPILL_MODE = 2;
    public static final int DRINK_MODE = 3;

    public static int getHoseMode(ItemStack stack)
    {
        if(stack.getNbt() != null)
        {
            return stack.getNbt().getInt("Mode");
            //1 = Suck mode
            //2 = Spill mode
            //3 = Drink mode
        }
        return NO_ASSIGN;
    }

    public static int getHoseTank(ItemStack stack)
    {
        if(stack.getNbt() != null)
        {
            return stack.getNbt().getInt("Tank");
            //1 = Left tank
            //2 = Right tank
        }
        return 0;
    }

    public SingleVariantStorage<FluidVariant> getSelectedFluidTank(ItemStack stack, TravelersBackpackInventory inv)
    {
        return getHoseTank(stack) == 1 ? inv.getLeftTank() : inv.getRightTank();
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        if(entityIn instanceof PlayerEntity player)
        {
            if(!ComponentUtils.isWearingBackpack(player))
            {
                if(stack.getNbt() != null)
                {
                    stack.setNbt(null);
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
    {
        if(getHoseMode(stack) == NO_ASSIGN)
        {
            tooltip.add(new TranslatableText("hose.travelersbackpack.not_assigned").formatted(Formatting.BLUE));
        }
        else
        {
            if(stack.getNbt() != null)
            {
                NbtCompound compound = stack.getNbt();

                if(compound.getInt("Mode") == SUCK_MODE)
                {
                    tooltip.add(new TranslatableText("hose.travelersbackpack.current_mode_suck").formatted(Formatting.BLUE));
                }

                if(compound.getInt("Mode") == SPILL_MODE)
                {
                    tooltip.add(new TranslatableText("hose.travelersbackpack.current_mode_spill").formatted(Formatting.BLUE));
                }

                if(compound.getInt("Mode") == DRINK_MODE)
                {
                    tooltip.add(new TranslatableText("hose.travelersbackpack.current_mode_drink").formatted(Formatting.BLUE));
                }

                if(compound.getInt("Tank") == 1)
                {
                    tooltip.add(new TranslatableText("hose.travelersbackpack.current_tank_left").formatted(Formatting.BLUE));
                }

                if(compound.getInt("Tank") == 2)
                {
                    tooltip.add(new TranslatableText("hose.travelersbackpack.current_tank_right").formatted(Formatting.BLUE));
                }
            }
        }
    }

    @Override
    public Text getName(ItemStack stack)
    {
        int x = getHoseMode(stack);
        String mode = "";
        String localizedName = new TranslatableText("item.travelersbackpack.hose").getString();
        String suckMode = new TranslatableText("item.travelersbackpack.hose.suck").getString();
        String spillMode = new TranslatableText("item.travelersbackpack.hose.spill").getString();
        String drinkMode = new TranslatableText("item.travelersbackpack.hose.drink").getString();

        if(x == SUCK_MODE)
        {
            mode = " " + suckMode;
        }
        else if(x == SPILL_MODE)
        {
            mode = " " + spillMode;
        }
        else if(x == DRINK_MODE)
        {
            mode = " " + drinkMode;
        }

        return new LiteralText(localizedName + mode);
    }

    public void setCompoundTag(ItemStack stack)
    {
        NbtCompound tag = stack.getOrCreateNbt();

        if(!tag.containsUuid("Tank"))
        {
            tag.putInt("Tank", 1);
        }

        if(!tag.containsUuid("Mode"))
        {
            tag.putInt("Mode", 1);
        }
    }
}