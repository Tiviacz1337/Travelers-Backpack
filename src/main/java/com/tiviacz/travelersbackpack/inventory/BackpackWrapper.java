package com.tiviacz.travelersbackpack.inventory;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.components.*;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import com.tiviacz.travelersbackpack.inventory.handler.StorageAccessWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.sorter.SortSelector;
import com.tiviacz.travelersbackpack.inventory.upgrades.IEnable;
import com.tiviacz.travelersbackpack.inventory.upgrades.ITickableUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.IUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.item.upgrades.TanksUpgradeItem;
import com.tiviacz.travelersbackpack.item.upgrades.UpgradeItem;
import com.tiviacz.travelersbackpack.network.ClientboundSyncItemStackPacket;
import com.tiviacz.travelersbackpack.util.ItemStackUtils;
import com.tiviacz.travelersbackpack.util.PacketDistributor;
import com.tiviacz.travelersbackpack.util.Reference;
import com.tiviacz.travelersbackpack.util.RegistryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class BackpackWrapper {
    public static final BackpackWrapper DUMMY = new BackpackWrapper(ModItems.STANDARD_TRAVELERS_BACKPACK.getDefaultInstance(), Reference.BLOCK_ENTITY_SCREEN_ID, null, null);

    protected ItemStack stack;
    private ItemStackHandler inventory;
    private ItemStackHandler upgrades;
    private ItemStackHandler tools;

    public ItemStackHandler upgradesTracker;

    private final UpgradeManager upgradeManager;
    private Player owner;
    public ArrayList<Player> playersUsing = new ArrayList<>();
    protected Level level;
    private final int screenID;
    private long tanksCapacity = 0;
    public int index = -1;

    //Storage, Upgrades, Tools
    //0 - off, 1 - on
    public int[] dataLoad = new int[]{1, 1, 1};

    public Runnable saveHandler = () -> {
    };
    public Runnable abilityHandler = () -> {
    };
    public BlockPos backpackPos;

    public static final int STORAGE_ID = 0;
    public static final int UPGRADES_ID = 1;
    public static final int TOOLS_ID = 2;

    public BackpackWrapper(ItemStack stack, int screenID, @Nullable Player player, @Nullable Level level, int index) {
        this(stack, screenID, player, level);
        this.index = index;
    }

    public BackpackWrapper(ItemStack stack, int screenID, @Nullable Player player, @Nullable Level level) {
        this(stack, screenID, player, level, new int[]{1, 1, 1});
    }

    public BackpackWrapper(ItemStack stack, int screenID, @Nullable Player player, @Nullable Level level, int[] dataLoad) {
        if(player != null) {
            this.playersUsing.add(player);
        }
        if(screenID == Reference.WEARABLE_SCREEN_ID) {
            this.setBackpackOwner(player);
        }

        this.stack = stack;

        if(!isSizeInitialized(stack)) {
            initializeSize(stack);
        }

        this.screenID = screenID;
        this.level = level;
        this.dataLoad = dataLoad;

        this.loadHandlers();
        this.setBackpackTankCapacity();
        this.upgradeManager = new UpgradeManager(this);

        if(upgrades != null) {
            if(!this.stack.has(ModDataComponents.RENDER_INFO)) {
                this.setRenderInfo(RenderInfo.EMPTY.compoundTag());
            }

            if(stack.has(ModDataComponents.STARTER_UPGRADES)) {
                StarterUpgrades upgrades = stack.get(ModDataComponents.STARTER_UPGRADES);
                if(upgrades != null) {
                    upgrades.upgrades().forEach(this::setStarterUpgrade);
                    stack.remove(ModDataComponents.STARTER_UPGRADES);
                }
            }

            //Old Data Conversion (Should not run in regular case)
            if(stack.has(ModDataComponents.FLUID_TANKS_OLD)) {
                ItemStack oldTanks = ModItems.TANKS_UPGRADE.getDefaultInstance();
                oldTanks.set(ModDataComponents.FLUIDS, new Fluids(new FluidVariantWrapper(stack.get(ModDataComponents.FLUID_TANKS_OLD).leftTank().fluidVariant(), stack.get(ModDataComponents.FLUID_TANKS_OLD).leftTank().amount()),
                        new FluidVariantWrapper(stack.get(ModDataComponents.FLUID_TANKS_OLD).rightTank().fluidVariant(), stack.get(ModDataComponents.FLUID_TANKS_OLD).rightTank().amount())));
                this.setStarterUpgrade(oldTanks);
                stack.remove(ModDataComponents.FLUID_TANKS_OLD);
            }
        }
    }

    //Create wrapper from the Backpack Stack
    public static BackpackWrapper fromStack(ItemStack backpackStack) {
        return new BackpackWrapper(backpackStack, Reference.ITEM_SCREEN_ID, null, null);
    }

    public void setBackpackStack(ItemStack backpack) {
        this.stack = backpack;

        //Update client tanks if present
        getUpgradeManager().getUpgrade(TanksUpgrade.class).ifPresent(tanksUpgrade -> tanksUpgrade.syncClients(backpack));
    }

    public ItemStack getBackpackStack() {
        return this.stack;
    }

    public int getBackpackSlotIndex() {
        return this.index;
    }

    public void setBackpackOwner(Player player) {
        this.owner = player;
    }

    @Nullable
    public Player getBackpackOwner() {
        return this.owner;
    }

    public ArrayList<Player> getPlayersUsing() {
        return this.playersUsing;
    }

    public void addUser(Player player) {
        if(!this.playersUsing.contains(player)) {
            this.playersUsing.add(player);
        }
    }

    public Level getLevel() {
        return this.level;
    }

    public ItemStackHandler loadHandler(DataComponentType<BackpackContainerContents> data, int defaultSize, int dataId, BiFunction<NonNullList<ItemStack>, Integer, ItemStackHandler> handlerFunction) {
        if(this.stack.has(data)) {
            BackpackContainerContents contents = this.stack.get(data);
            if(contents.getItems().size() < defaultSize) {
                contents = expandContents(contents, defaultSize, this.stack, data);
            }

            NonNullList<ItemStack> stacks = NonNullList.withSize(contents.getItems().size(), ItemStack.EMPTY);
            contents.copyInto(stacks);
            return handlerFunction.apply(stacks, dataId);
        }
        return handlerFunction.apply(NonNullList.withSize(defaultSize, ItemStack.EMPTY), dataId);
    }

    public void loadHandlers() {
        if(this.dataLoad[STORAGE_ID] == 1) {
            loadStorage();
        }
        if(this.dataLoad[UPGRADES_ID] == 1) {
            loadUpgrades();
        }
        if(this.dataLoad[TOOLS_ID] == 1) {
            loadTools();
        }
    }

    public void loadStorage() {
        this.inventory = loadHandler(ModDataComponents.BACKPACK_CONTAINER, getStorageSize(), STORAGE_ID, this::createHandler);
    }

    public void loadUpgrades() {
        this.upgrades = loadHandler(ModDataComponents.UPGRADES, getUpgradesSize(), UPGRADES_ID, this::createUpgradeHandler);
        this.upgradesTracker = loadHandler(ModDataComponents.UPGRADES, getUpgradesSize(), UPGRADES_ID, (stacks, data) -> new ItemStackHandler(stacks));
    }

    public void loadTools() {
        this.tools = loadHandler(ModDataComponents.TOOLS_CONTAINER, getToolSize(), TOOLS_ID, this::createHandler);
    }

    public void loadAdditionally(int type) {
        //Load handler additionally if not loaded in artificial wrapper
        if(dataLoad[type] == 0) {
            if(type == STORAGE_ID) loadStorage();
            if(type == UPGRADES_ID) loadUpgrades();
            if(type == TOOLS_ID) loadTools();
            dataLoad[type] = 1;
        }
    }

    public BackpackContainerContents expandContents(BackpackContainerContents contents, int size, ItemStack backpack, DataComponentType type) {
        if(contents.getItems().size() < size) {
            List<ItemStack> oldItems = contents.getItems();
            //Populate expanded items list with empty stacks
            ArrayList<ItemStack> itemList = new ArrayList<>(Collections.nCopies(size, ItemStack.EMPTY));

            for(int i = 0; i < oldItems.size(); i++) {
                if(!oldItems.get(i).isEmpty()) {
                    itemList.set(i, oldItems.get(i));
                }
            }
            //Expanded items
            BackpackContainerContents expandedContents = BackpackContainerContents.fromItems(size, itemList);
            backpack.set(type, expandedContents);
            return expandedContents;
        }
        return contents;
    }

    public void setStarterUpgrade(ItemStack upgrade) {
        if(this.level == null) {
            return;
        }
        if(upgrade.getItem().isEnabled(this.level.enabledFeatures())) {
            for(int i = 0; i < this.upgrades.getSlots(); i++) {
                if(this.upgrades.getStackInSlot(i).isEmpty()) {
                    this.upgrades.setStackInSlot(i, upgrade);
                    this.upgradesTracker.setStackInSlot(i, upgrade);

                    if(upgrade.getItem() instanceof TanksUpgradeItem) {
                        this.setRenderInfo(TanksUpgradeItem.writeToRenderData().compoundTag());
                    }
                    break;
                }
            }
        }
    }

    public int getStorageSize() {
        return this.stack.getOrDefault(ModDataComponents.STORAGE_SLOTS, Tiers.LEATHER.getStorageSlots());
    }

    public int getUpgradesSize() {
        return this.stack.getOrDefault(ModDataComponents.UPGRADE_SLOTS, Tiers.LEATHER.getUpgradeSlots());
    }

    public int getToolSize() {
        return this.stack.getOrDefault(ModDataComponents.TOOL_SLOTS, Tiers.LEATHER.getToolSlots());
    }

    public StorageAccessWrapper getStorageForInputOutput() {
        return new StorageAccessWrapper(this, getStorage());
    }

    public ItemStackHandler getStorage() {
        return this.inventory;
    }

    public ItemStackHandler getUpgrades() {
        return this.upgrades;
    }

    public ItemStackHandler getTools() {
        return this.tools;
    }

    public UpgradeManager getUpgradeManager() {
        return this.upgradeManager;
    }

    @Nullable
    public RegistryAccess getRegistriesAccess() {
        if(level != null) {
            return level.registryAccess();
        }
        if(!playersUsing.isEmpty() && playersUsing.get(0).level().registryAccess() != null) {
            return playersUsing.get(0).level().registryAccess();
        }
        if(RegistryHelper.getRegistryAccess().isPresent()) {
            return RegistryHelper.getRegistryAccess().get();
        }
        return null;
    }

    public List<Integer> getUnsortableSlots() {
        return this.stack.getOrDefault(ModDataComponents.SLOTS, Slots.EMPTY).unsortables();
    }

    public List<Pair<Integer, Pair<ItemStack, Boolean>>> getMemorySlots() {
        return this.stack.getOrDefault(ModDataComponents.SLOTS, Slots.EMPTY).memory();
    }

    public int getScreenID() {
        return this.screenID;
    }

    public Component getBackpackScreenTitle() {
        return this.stack.has(DataComponents.CUSTOM_NAME) ? this.stack.get(DataComponents.CUSTOM_NAME) : Component.translatable("screen.travelersbackpack.title");
    }

    public void setUnsortableSlots(List<Integer> unsortables) {
        Slots old = this.stack.getOrDefault(ModDataComponents.SLOTS, Slots.EMPTY);
        setData(ModDataComponents.SLOTS, Slots.updateUnsortables(old, unsortables));
    }

    public void setMemorySlots(List<Pair<Integer, Pair<ItemStack, Boolean>>> memory) {
        Slots old = this.stack.getOrDefault(ModDataComponents.SLOTS, Slots.EMPTY);
        setData(ModDataComponents.SLOTS, Slots.updateMemory(old, memory));
    }

    public <T> void setDataAndSync(DataComponentType<T> dataComponentType, T value) {
        setData(dataComponentType, value);

        //Update on client
        sendDataToClients(dataComponentType);
    }

    public <T> void setData(DataComponentType<T> dataComponentType, T value) {
        this.stack.set(dataComponentType, value);
        this.saveHandler.run();

        if(dataComponentType == ModDataComponents.ABILITY_ENABLED) {
            this.abilityHandler.run();
        }
    }

    public boolean showToolSlots() {
        return this.stack.getOrDefault(ModDataComponents.SHOW_TOOL_SLOTS, false);
    }

    public boolean showMoreButtons() {
        return this.stack.getOrDefault(ModDataComponents.SHOW_MORE_BUTTONS, false);
    }

    public boolean tanksVisible() {
        if(this.stack.has(ModDataComponents.RENDER_INFO)) {
            return this.stack.get(ModDataComponents.RENDER_INFO).hasTanks();
        }
        return getUpgradeManager().getUpgrade(TanksUpgrade.class).isPresent();
    }

    public long getBackpackTankCapacity() {
        return this.tanksCapacity;
    }

    public void setBackpackPos(BlockPos pos) {
        this.backpackPos = pos;
    }

    public BlockPos getBackpackPos() {
        return this.backpackPos;
    }

    public void setBackpackTankCapacity() {
        int rows = getRows() + (isExtended() ? 2 : 0);
        this.tanksCapacity = Tiers.of(this.stack.getOrDefault(ModDataComponents.TIER, 0)).getTankCapacityPerRow() * rows;
    }

    public RenderInfo getRenderInfo() {
        return this.stack.getOrDefault(ModDataComponents.RENDER_INFO, RenderInfo.EMPTY);
    }

    public void setRenderInfo(CompoundTag compound) {
        setDataAndSync(ModDataComponents.RENDER_INFO, new RenderInfo(compound));
    }

    public void removeRenderInfo() {
        setRenderInfo(new CompoundTag());
    }

    public boolean isAbilityEnabled() {
        return this.stack.getOrDefault(ModDataComponents.ABILITY_ENABLED, TravelersBackpackConfig.getConfig().backpackAbilities.forceAbilityEnabled);
    }

    public SortSelector.SortType getSortType() {
        int type = this.stack.getOrDefault(ModDataComponents.SORT_TYPE, 0);
        return SortSelector.SortType.values()[type];
    }

    public void setNextSortType() {
        SortSelector.SortType type = getSortType();
        setDataAndSync(ModDataComponents.SORT_TYPE, type.next().ordinal());
    }

    public boolean hasSleepingBag() {
        return this.stack.has(ModDataComponents.SLEEPING_BAG_COLOR);
    }

    public int getSleepingBagColor() {
        return this.stack.getOrDefault(ModDataComponents.SLEEPING_BAG_COLOR, -1);
    }

    public void setSleepingBagColor(int colorId) {
        setData(ModDataComponents.SLEEPING_BAG_COLOR, colorId);
    }

    public boolean isOwner(Player player) {
        if(getBackpackOwner() != null) {
            return getBackpackOwner().getId() == player.getId();
        }
        return true;
    }

    public boolean isDyed() {
        return this.stack.has(DataComponents.DYED_COLOR);
    }

    public int getDyeColor() {
        return this.stack.getOrDefault(DataComponents.DYED_COLOR, new DyedItemColor(-1, false)).rgb();
    }

    public int getCooldown() {
        return this.stack.getOrDefault(ModDataComponents.COOLDOWN, 0);
    }

    public void setCooldown(int cooldownInSeconds) {
        setDataAndSync(ModDataComponents.COOLDOWN, cooldownInSeconds);
    }

    //Block Entity
    public void decreaseCooldown() {
        if(getCooldown() > 0) {
            this.stack.update(ModDataComponents.COOLDOWN, 0, currentCooldown -> currentCooldown - 1);
            this.saveHandler.run();
        }
    }

    public boolean canUpgradeTick() {
        return this.stack.has(ModDataComponents.UPGRADE_TICK_INTERVAL);
    }

    public boolean hasTickingUpgrade() {
        return this.upgradeManager.hasTickingUpgrade();
    }

    public int getUpgradeTickInterval() {
        return this.stack.getOrDefault(ModDataComponents.UPGRADE_TICK_INTERVAL, 100);
    }

    public void setUpgradeTickInterval(int ticks) {
        setDataAndSync(ModDataComponents.UPGRADE_TICK_INTERVAL, ticks);
    }

    public void removeUpgradeTickInterval() {
        this.stack.remove(ModDataComponents.UPGRADE_TICK_INTERVAL);
    }

    public boolean isExtended() {
        return getStorageSize() > 81;
    }

    public int getSlotsInRow() {
        if(isExtended()) {
            return 11;
        }
        return 9;
    }

    public int getRows() {
        return (int)Math.ceil((double)getStorageSize() / getSlotsInRow());
    }

    public void sendDataToClients(DataComponentType... dataComponentTypes) {
        //Other methods sync data for block entities
        if(getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) return;

        //Sync stack in slot or hand
        if(getScreenID() == Reference.ITEM_SCREEN_ID && !getPlayersUsing().stream().filter(p -> !p.level().isClientSide).toList().isEmpty()) {
            int slotIndex = this.index == -1 ? getPlayersUsing().get(0).getInventory().selected : this.index;
            PacketDistributor.sendToPlayer((ServerPlayer)this.getPlayersUsing().get(0), new ClientboundSyncItemStackPacket(getPlayersUsing().get(0).getId(), slotIndex, getBackpackStack(), ItemStackUtils.createDataComponentMap(getBackpackStack(), dataComponentTypes)));
            return;
        }
        //Sync stack equipped in back slot
        if(TravelersBackpack.enableIntegration()) {
            //Sync backpack data on clients differently for integration, because of the way backpacks are handled
            if(getScreenID() == Reference.WEARABLE_SCREEN_ID && !getPlayersUsing().stream().filter(p -> !p.level().isClientSide).toList().isEmpty()) {
                for(Player player : getPlayersUsing()) {
                    PacketDistributor.sendToPlayer((ServerPlayer)player, new ClientboundSyncItemStackPacket(player.getId(), -1, getBackpackStack(), ItemStackUtils.createDataComponentMap(getBackpackStack(), dataComponentTypes)));
                }
            }
            return;
        }
        //Sync attachment stack
        if(getBackpackOwner() != null) {
            DataComponentMap.Builder mapBuilder = DataComponentMap.builder();
            ItemStack serverDataHolder = ComponentUtils.getWearingBackpack(getBackpackOwner()).copy();
            for(DataComponentType type : dataComponentTypes) {
                ItemStack serverDataHolderCopy = ItemStackUtils.reduceSize(serverDataHolder);
                if(!serverDataHolderCopy.has(type)) {
                    continue;
                }
                mapBuilder.set(type, serverDataHolderCopy.get(type));
            }
            ComponentUtils.getComponent(getBackpackOwner()).ifPresent(data -> data.synchronise(mapBuilder.build()));
        }
    }

    public Optional<Pair<Integer, Pair<ItemStack, Boolean>>> getMemorizedSlot(int slot) {
        return getMemorySlots().stream()
                .filter(pair -> pair.getFirst() == slot)
                .findFirst();
    }

    private ItemStackHandler createHandler(NonNullList<ItemStack> stacks, int dataId) {
        return new ItemStackHandler(stacks) {
            @Override
            protected void onContentsChanged(int slot) {
                setSlotChanged(slot, getStackInSlot(slot), dataId);

                if(dataId == TOOLS_ID) {
                    sendDataToClients(ModDataComponents.TOOLS_CONTAINER);
                }

                //Update comparator
                saveHandler.run();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                if(dataId == 2) {
                    return ToolSlotItemHandler.isValid(stack);
                }
                return BackpackSlotItemHandler.isItemValid(stack);
            }
        };
    }

    public void setSlotChanged(int index, ItemStack stack, int dataId) {
        switch(dataId) {
            case STORAGE_ID:
                this.stack.update(ModDataComponents.BACKPACK_CONTAINER, new BackpackContainerContents(this.getStorage().getSlots()), new BackpackContainerContents.Slot(index, stack), BackpackContainerContents::updateSlot);
                break;
            case UPGRADES_ID:
                this.stack.update(ModDataComponents.UPGRADES, new BackpackContainerContents(this.getUpgrades().getSlots()), new BackpackContainerContents.Slot(index, stack), BackpackContainerContents::updateSlot);
                break;
            case TOOLS_ID:
                this.stack.update(ModDataComponents.TOOLS_CONTAINER, new BackpackContainerContents(this.getTools().getSlots()), new BackpackContainerContents.Slot(index, stack), BackpackContainerContents::updateSlot);
                break;
        }
    }

    public void applyLowestTickInterval() {
        int minimalTickInterval = 100;
        for(int i = 0; i < this.upgrades.getSlots(); i++) {
            ItemStack upgrade = this.upgrades.getStackInSlot(i);
            if(!upgrade.isEmpty()) {
                if(upgrade.getOrDefault(ModDataComponents.UPGRADE_ENABLED, true) && upgrade.has(ModDataComponents.COOLDOWN)) {
                    minimalTickInterval = Math.min(minimalTickInterval, upgrade.get(ModDataComponents.COOLDOWN));
                }
            }
        }
        if(!canUpgradeTick() || minimalTickInterval != getUpgradeTickInterval()) {
            setUpgradeTickInterval(minimalTickInterval);
        }
    }

    public void updateMinimalTickInterval(ItemStack newStack) {
        if(level != null && level.isClientSide) return;

        boolean applyLowestTickInterval = false;
        if(newStack.getItem() instanceof UpgradeItem upgradeItem) {
            if(upgradeItem.isTickingUpgrade()) {
                if(newStack.getOrDefault(ModDataComponents.UPGRADE_ENABLED, true)) {
                    int tickInterval = getUpgradeTickInterval();
                    if(newStack.has(ModDataComponents.COOLDOWN)) {
                        tickInterval = newStack.get(ModDataComponents.COOLDOWN);
                    }
                    if(!canUpgradeTick() || tickInterval < getUpgradeTickInterval()) {
                        setUpgradeTickInterval(tickInterval);
                    } else if(tickInterval > getUpgradeTickInterval()) {
                        applyLowestTickInterval = true;
                    }
                } else {
                    applyLowestTickInterval = true;
                }
            }
        } else {
            applyLowestTickInterval = true;
        }

        if(canUpgradeTick()) {
            if(!hasTickingUpgrade()) {
                removeUpgradeTickInterval();
            } else if(applyLowestTickInterval) {
                applyLowestTickInterval();
            }
        }
    }

    private ItemStackHandler createUpgradeHandler(NonNullList<ItemStack> stacks, int dataId) {
        return new ItemStackHandler(stacks) {
            @Override
            protected void onContentsChanged(int slot) {
                setSlotChanged(slot, getStackInSlot(slot), dataId);

                //Menu and screen updates
                if(!getPlayersUsing().isEmpty()) {
                    getUpgradeManager().detectedChange(upgradesTracker, slot);
                }

                updateMinimalTickInterval(getStackInSlot(slot));

                //Update client
                saveHandler.run();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                boolean isValid = true;
                //Check if upgrade is already present
                for(int i = 0; i < this.getSlots(); i++) {
                    if(getStackInSlot(i).getItem() == stack.getItem()) {
                        isValid = false;
                        break;
                    }
                }
                if(!isValid) {
                    return false;
                }
                if(stack.getItem() instanceof TanksUpgradeItem) {
                    isValid = TanksUpgradeItem.canBePutInBackpack(getBackpackTankCapacity(), stack);
                }
                if(!checkIfUpgradeValid(stack)) {
                    isValid = false;
                }
                return isValid;
            }

            public boolean checkIfUpgradeValid(ItemStack upgradeStack) {
                if(upgradeStack.getItem() instanceof UpgradeItem upgradeItem) {
                    Player player = getPlayersUsing().isEmpty() ? null : getPlayersUsing().getFirst();
                    if(player == null) {
                        return false;
                    }
                    return upgradeItem.isEnabled(player.level().enabledFeatures());
                }
                return false;
            }
        };
    }

    public static boolean isSizeInitialized(ItemStack stack) {
        return stack.has(ModDataComponents.STORAGE_SLOTS) && stack.has(ModDataComponents.UPGRADE_SLOTS) && stack.has(ModDataComponents.TOOL_SLOTS);
    }

    public static void initializeSize(ItemStack stack) {
        Tiers.Tier tier = Tiers.LEATHER;
        if(stack.has(ModDataComponents.TIER)) {
            tier = Tiers.of(stack.get(ModDataComponents.TIER));
        }
        if(!stack.has(ModDataComponents.STORAGE_SLOTS)) {
            stack.set(ModDataComponents.STORAGE_SLOTS, tier.getStorageSlots());
        }
        if(!stack.has(ModDataComponents.UPGRADE_SLOTS)) {
            stack.set(ModDataComponents.UPGRADE_SLOTS, tier.getUpgradeSlots());
        }
        if(!stack.has(ModDataComponents.TOOL_SLOTS)) {
            stack.set(ModDataComponents.TOOL_SLOTS, tier.getToolSlots());
        }
    }

    //Used if slots are removed/added - reconstructs modifiable slots & updates screen
    public void requestMenuAndScreenUpdate() {
        requestMenuUpdate();
        requestScreenUpdate();
    }

    public void requestMenuUpdate() {
        if(!getPlayersUsing().isEmpty()) {
            getPlayersUsing().stream().filter(player -> player.containerMenu instanceof BackpackBaseMenu).forEach(player -> ((BackpackBaseMenu)player.containerMenu).updateModifiableSlots());
        }
    }

    public void requestScreenUpdate() {
        if(!getPlayersUsing().isEmpty() && !getPlayersUsing().stream().filter(player -> player.level().isClientSide).toList().isEmpty()) {
            if(Minecraft.getInstance().screen instanceof BackpackScreen screen) {
                screen.updateScreen(false);
            }
        }
    }

    public static void tickForBlockEntity(BackpackBlockEntity backpackBlockEntity) {
        BackpackWrapper wrapper = backpackBlockEntity.getWrapper();
        if(wrapper != BackpackWrapper.DUMMY) {
            if(wrapper.isAbilityEnabled() && BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, wrapper.getBackpackStack())) {
                boolean decreaseCooldown = BackpackAbilities.ABILITIES.abilityTickBlock(backpackBlockEntity);
                if(wrapper.getCooldown() > 0) {
                    if(decreaseCooldown) {
                        wrapper.decreaseCooldown();
                    }
                }
            }
        }
    }

    @Nullable
    public static BackpackWrapper getBackpackWrapper(Player player, ItemStack backpack, int[] dataLoad) {
        if(ComponentUtils.isWearingBackpack(player)) {
            if(player.containerMenu instanceof BackpackItemMenu menu && menu.getWrapper().getScreenID() == Reference.WEARABLE_SCREEN_ID) {
                return menu.getWrapper();
            } else {
                for(Player otherPlayer : player.level().players()) {
                    if(otherPlayer.containerMenu instanceof BackpackItemMenu menu && menu.getWrapper().isOwner(player) && menu.getWrapper().getScreenID() == Reference.WEARABLE_SCREEN_ID) {
                        return menu.getWrapper();
                    }
                }
                return new BackpackWrapper(backpack, Reference.WEARABLE_SCREEN_ID, player, player.level(), dataLoad);
            }
        }
        return null;
    }

    public static void tick(ItemStack stack, Player player, boolean integration) {
        if(!integration) {
            if(TravelersBackpack.enableIntegration()) return;
        }

        if(player.isAlive() && ComponentUtils.isWearingBackpack(player)) {
            int ticks = (int)player.level().getGameTime();
            if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, ComponentUtils.getWearingBackpack(player))) {
                if(BackpackAbilities.isAbilityEnabledInConfig(stack)) {
                    if(stack.getOrDefault(ModDataComponents.ABILITY_ENABLED, TravelersBackpackConfig.getConfig().backpackAbilities.forceAbilityEnabled)) {
                        boolean decreaseCooldown = BackpackAbilities.ABILITIES.abilityTick(stack, player);
                        if(stack.getOrDefault(ModDataComponents.COOLDOWN, 0) > 0) {
                            BackpackWrapper wrapper;
                            if(ticks % 100 == 0) {
                                if(decreaseCooldown) {
                                    wrapper = ComponentUtils.getBackpackWrapper(player, stack, ComponentUtils.NO_ITEMS);
                                    int cooldown = wrapper.getCooldown();
                                    if(player.level().isClientSide) return;
                                    if(cooldown - 100 < 0) {
                                        wrapper.setCooldown(0);
                                    } else {
                                        wrapper.setCooldown(cooldown - 100);
                                    }
                                }
                            }
                        }
                    } else { //Tick cooldown even if ability switched off
                        if(stack.getOrDefault(ModDataComponents.COOLDOWN, 0) > 0) {
                            BackpackWrapper wrapper;
                            if(ticks % 100 == 0) {
                                wrapper = ComponentUtils.getBackpackWrapper(player, stack, ComponentUtils.NO_ITEMS);
                                int cooldown = wrapper.getCooldown();
                                if(player.level().isClientSide) return;
                                if(cooldown - 100 < 0) {
                                    wrapper.setCooldown(0);
                                } else {
                                    wrapper.setCooldown(cooldown - 100);
                                }
                            }
                        }
                    }
                }
            } else if(stack.getOrDefault(ModDataComponents.ABILITY_ENABLED, false)) {
                stack.set(ModDataComponents.ABILITY_ENABLED, false);
            }

            if(stack.has(ModDataComponents.UPGRADE_TICK_INTERVAL)) {
                int upgradeTicks = stack.get(ModDataComponents.UPGRADE_TICK_INTERVAL);
                if(upgradeTicks == 0) return;
                BackpackWrapper wrapper;
                if(ticks % upgradeTicks == 0) {
                    wrapper = ComponentUtils.getBackpackWrapper(player, stack, ComponentUtils.UPGRADES_ONLY);
                    wrapper.getUpgradeManager().upgrades.forEach(upgradeBase -> {
                        if(upgradeBase instanceof ITickableUpgrade tickable) {
                            boolean tick = true;
                            if(upgradeBase instanceof IEnable enable) {
                                tick = enable.isEnabled(upgradeBase);
                            }
                            if(tick) {
                                tickable.tick(player, player.level(), player.blockPosition(), ticks);
                            }
                        }
                    });
                }
            }
        }
    }
}
