package com.tiviacz.travelersbackpack.inventory.upgrades.feeding;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.upgrades.FilterUpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.IEnable;
import com.tiviacz.travelersbackpack.inventory.upgrades.ITickableUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.FilterHandler;
import com.tiviacz.travelersbackpack.util.InventoryHelper;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FeedingUpgrade extends FilterUpgradeBase<FeedingUpgrade, FeedingFilterSettings> implements IEnable, ITickableUpgrade {
    private static final int STILL_HUNGRY_COOLDOWN = 10;
    private static final double FEEDING_RANGE = 3.0D;

    public BlockPos particlePos = null;

    public FeedingUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> filter) {
        super(manager, dataHolderSlot, new Point(66, 49),
                TravelersBackpackConfig.SERVER.backpackUpgrades.feedingUpgradeSettings.filterSlotCount.get(),
                TravelersBackpackConfig.SERVER.backpackUpgrades.feedingUpgradeSettings.slotsInRow.get(), filter, List.of());
    }

    @Override
    public boolean hasTagSelector() {
        return false;
    }

    @Override
    public List<Integer> getFilter() {
        List<Integer> filter = NbtHelper.getOrDefault(getDataHolderStack(), ModDataHelper.FILTER_SETTINGS, List.of(1, 1, 0));
        //Conversion error fix - #TODO to remove
        if(filter.size() != 3) {
            filter = List.of(1, 1, 0);
        }
        return filter;
    }

    @Override
    public FeedingFilterSettings createFilterSettings(UpgradeManager manager, NonNullList<ItemStack> filter, List<String> filterTags) {
        return new FeedingFilterSettings(manager.getWrapper().getStorage(), filter.stream().limit(getFilterSlotCount()).filter(stack -> !stack.isEmpty()).toList(), getFilter());
    }

    public boolean canEat(Player player, ItemStack stack) {
        return getFilterSettings().matchesFilter(player, stack) && !player.getCooldowns().isOnCooldown(stack.getItem()); //Cooldown patch for everlasting foods from Artifacts
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public WidgetBase<BackpackScreen> createWidget(BackpackScreen screen, int x, int y) {
        return new FeedingWidget(screen, this, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y));
    }

    @Override
    protected FilterHandler createFilter(NonNullList<ItemStack> stacks, int size) {
        return new FilterHandler(stacks, size) {
            @Override
            protected void onContentsChanged(int slot) {
                updateDataHolderUnchecked(ModDataHelper.BACKPACK_CONTAINER, filter);

                getFilterSettings().updateFilter(NbtHelper.get(getDataHolderStack(), ModDataHelper.BACKPACK_CONTAINER));
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return stack.isEdible();
            }
        };
    }

    @Override
    public int getTickRate() {
        return TravelersBackpackConfig.SERVER.backpackUpgrades.feedingUpgradeSettings.tickRate.get();
    }

    @Override
    public void tick(@Nullable Player player, Level level, BlockPos pos, int currentTick) {
        if(getCooldown() == 0) {
            return;
        }
        if(currentTick % getCooldown() != 0) {
            return;
        }

        if(level.isClientSide) {
            return;
        }

        boolean stillHungry = false;
        if(getUpgradeManager().getWrapper().getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) {
            AtomicBoolean stillHungryPlayer = new AtomicBoolean(false);
            this.particlePos = pos;
            level.getEntities(EntityType.PLAYER, new AABB(pos).inflate(FEEDING_RANGE), p -> true).forEach(p -> stillHungryPlayer.set(stillHungryPlayer.get() || feedPlayerAndGetHungry(p, level)));
            stillHungry = stillHungryPlayer.get();
            this.particlePos = null;
        } else {
            if(feedPlayerAndGetHungry(player, level)) {
                stillHungry = true;
            }
        }

        if(stillHungry) {
            setCooldown(STILL_HUNGRY_COOLDOWN);
            return;
        }

        if(!hasCooldown() || getCooldown() != getTickRate()) {
            setCooldown(getTickRate());
        }
    }

    private boolean feedPlayerAndGetHungry(Player player, Level level) {
        int hungerLevel = 20 - player.getFoodData().getFoodLevel();
        if(hungerLevel == 0 || level.isClientSide) {
            return false;
        }
        //Load storage if not loaded in artificial wrapper
        getUpgradeManager().getWrapper().loadAdditionally(BackpackWrapper.STORAGE_ID);
        return tryFeedingFoodFromStorage(level, hungerLevel, player) && player.getFoodData().getFoodLevel() < 20;
    }

    private boolean tryFeedingFoodFromStorage(Level level, int hungerLevel, Player player) {
        IItemHandlerModifiable storage = getUpgradeManager().getWrapper().getStorageForInputOutput();
        return InventoryHelper.iterate(storage, (slot, stack) -> tryFeedingStack(level, hungerLevel, player, slot, stack, storage));
    }

    private boolean tryFeedingStack(Level level, int hungerLevel, Player player, Integer slot, ItemStack stack, IItemHandlerModifiable backpackStorage) {
        if(isEdible(stack, player) && canEat(player, stack)) {
            ItemStack mainHandItem = player.getMainHandItem();
            player.getInventory().items.set(player.getInventory().selected, stack);

            ItemStack singleItemCopy = stack.copy();
            singleItemCopy.setCount(1);

            if(singleItemCopy.use(level, player, InteractionHand.MAIN_HAND).getResult() == InteractionResult.CONSUME) {
                player.getInventory().items.set(player.getInventory().selected, mainHandItem);

                stack.shrink(1);
                backpackStorage.setStackInSlot(slot, stack);

                ItemStack resultItem = ForgeEventFactory.onItemUseFinish(player, singleItemCopy, 0, singleItemCopy.getItem().finishUsingItem(singleItemCopy, level, player));
                if(!resultItem.isEmpty()) {
                    ItemStack insertResult = InventoryHelper.addItemStackToHandler(backpackStorage, resultItem, false);
                    if(!insertResult.isEmpty()) {
                        player.drop(insertResult, true);
                    }
                }
                if(this.particlePos != null) {
                    this.spawnHeartParticles(level, this.particlePos);
                }
                return true;
            }
            player.getInventory().items.set(player.getInventory().selected, mainHandItem);
        }
        return false;
    }

    private static boolean isEdible(ItemStack stack, LivingEntity player) {
        if(!stack.isEdible()) {
            return false;
        }
        FoodProperties foodProperties = stack.getItem().getFoodProperties(stack, player);
        return foodProperties != null && foodProperties.getNutrition() >= 1;
    }

    private void spawnHeartParticles(Level level, BlockPos pos) {
        if(level instanceof ServerLevel serverLevel) {
            RandomSource rand = level.getRandom();
            double centerX = pos.getX() + 0.5;
            double centerY = pos.getY() + 0.5;
            double centerZ = pos.getZ() + 0.5;

            double radius = 0.5;
            double angle = rand.nextDouble() * Math.PI * 2;
            double distance = rand.nextDouble() * radius;
            double offsetX = Math.cos(angle) * distance;
            double offsetZ = Math.sin(angle) * distance;
            double offsetY = (rand.nextDouble() - 0.5) * 0.6;

            serverLevel.sendParticles(ParticleTypes.HEART, centerX + offsetX, centerY + offsetY, centerZ + offsetZ, 0, 0.0, 0.0, 0.0, 1.0);
        }
    }
}