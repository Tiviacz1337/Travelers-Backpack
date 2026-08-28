package com.tiviacz.travelersbackpack.inventory.upgrades.feeding;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.transfer.BackpackResourceHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.FilterUpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.IEnable;
import com.tiviacz.travelersbackpack.inventory.upgrades.ITickableUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.FilterHandler;
import com.tiviacz.travelersbackpack.util.InventoryHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
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
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
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
        return getDataHolderStack().getOrDefault(ModDataComponents.FILTER_SETTINGS, List.of(1, 1, 0));
    }

    @Override
    public FeedingFilterSettings createFilterSettings(UpgradeManager manager, NonNullList<ItemStack> filter, List<String> filterTags) {
        return new FeedingFilterSettings(manager.getWrapper().getStorage(), filter.stream().limit(getFilterSlotCount()).filter(stack -> !stack.isEmpty()).toList(), getFilter(), manager.getWrapper().getRegistryAccess());
    }

    public boolean canEat(Player player, ItemStack stack) {
        return getFilterSettings().matchesFilter(player, stack) && !player.getCooldowns().isOnCooldown(stack); //Cooldown patch for everlasting foods from Artifacts
    }

    @Override
    protected FilterHandler createFilter(NonNullList<ItemStack> stacks, int size) {
        return new FilterHandler(stacks, size) {
            @Override
            protected void onContentsChanged(int slot, ItemStack previousStack) {
                updateDataHolderUnchecked(ModDataComponents.BACKPACK_CONTAINER.get(), InventoryHelper.itemsToList(size, filter));

                getFilterSettings().updateFilter(getDataHolderStack().get(ModDataComponents.BACKPACK_CONTAINER).getItems());
            }

            @Override
            public boolean isValid(int slot, ItemResource resource) {
                return resource.has(DataComponents.FOOD);
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

        if(level.isClientSide()) {
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
        if(hungerLevel == 0 || level.isClientSide()) {
            return false;
        }
        //Load storage if not loaded in artificial wrapper
        getUpgradeManager().getWrapper().loadAdditionally(BackpackWrapper.STORAGE_ID);
        return tryFeedingFoodFromStorage(level, hungerLevel, player) && player.getFoodData().getFoodLevel() < 20;
    }

    private boolean tryFeedingFoodFromStorage(Level level, int hungerLevel, Player player) {
        BackpackResourceHandler storage = getUpgradeManager().getWrapper().getStorage(); //#TODO potential issue with artifacts eternal steak and void upgrade not fixed here
        return InventoryHelper.iterate(storage, (slot, stack) -> tryFeedingStack(level, hungerLevel, player, slot, stack, storage));
    }

    private boolean tryFeedingStack(Level level, int hungerLevel, Player player, Integer slot, ItemStack stack, BackpackResourceHandler backpackStorage) {
        if(isEdible(stack, player) && canEat(player, stack)) {
            ItemStack mainHandItem = player.getMainHandItem();
            player.getInventory().getNonEquipmentItems().set(player.getInventory().getSelectedSlot(), stack);

            ItemStack singleItemCopy = stack.copy();
            singleItemCopy.setCount(1);

            if(singleItemCopy.use(level, player, InteractionHand.MAIN_HAND) == InteractionResult.CONSUME) {

                stack.shrink(1);
                backpackStorage.setStackInSlot(slot, stack);

                ItemStack resultItem = EventHooks.onItemUseFinish(player, singleItemCopy.copy(), 0, singleItemCopy.finishUsingItem(level, player));

                if(!resultItem.isEmpty()) {
                    int inserted = ResourceHandlerUtil.insertStacking(getUpgradeManager().getWrapper().getStorageForInputOutput(), ItemResource.of(resultItem), resultItem.getCount(), null);
                    if(inserted == 0) {
                        player.drop(resultItem, true);
                    }
                }
                player.getInventory().getNonEquipmentItems().set(player.getInventory().getSelectedSlot(), mainHandItem);
                if(this.particlePos != null) {
                    this.spawnHeartParticles(level, this.particlePos);
                }
                return true;
            }
            player.getInventory().getNonEquipmentItems().set(player.getInventory().getSelectedSlot(), mainHandItem);
        }
        return false;
    }

    private static boolean isEdible(ItemStack stack, LivingEntity player) {
        if(!stack.has(DataComponents.FOOD)) {
            return false;
        }
        FoodProperties foodProperties = stack.get(DataComponents.FOOD); //stack, player);
        return foodProperties != null && foodProperties.nutrition() >= 1;
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