package com.tiviacz.travelersbackpack.inventory;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.attachment.AttachmentUtils;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.component.RenderInfo;
import com.tiviacz.travelersbackpack.component.Slots;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.sorter.SortSelector;
import com.tiviacz.travelersbackpack.inventory.upgrades.IEnable;
import com.tiviacz.travelersbackpack.inventory.upgrades.ITickableUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.smelting.FurnaceUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.item.SleepingBagItem;
import com.tiviacz.travelersbackpack.item.upgrade.TanksUpgradeItem;
import com.tiviacz.travelersbackpack.item.upgrade.UpgradeItem;
import com.tiviacz.travelersbackpack.network.ClientboundSyncItemStackPacket;
import com.tiviacz.travelersbackpack.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class BackpackWrapper {
    public static final BackpackWrapper DUMMY = new BackpackWrapper(ModItems.STANDARD_TRAVELERS_BACKPACK.toStack(), Reference.BLOCK_ENTITY_SCREEN_ID, null, null);

    protected ItemStack stack;
    private ItemStacksResourceHandler inventory;
    private ItemStacksResourceHandler upgrades;
    private ItemStacksResourceHandler tools;

    private final UpgradeManager upgradeManager;
    private Player owner;
    public ArrayList<Player> playersUsing = new ArrayList<>();
    protected Level level;
    private final int screenID;
    private int tanksCapacity = 0;
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

        initializeSleepingBag(stack);
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
                var upgrades = stack.get(ModDataComponents.STARTER_UPGRADES);
                if(upgrades != null) {
                    upgrades.nonEmptyItemCopyStream().forEach(this::setStarterUpgrade);
                    stack.remove(ModDataComponents.STARTER_UPGRADES);
                }
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
        getUpgradeManager().getUpgrade(FurnaceUpgrade.class).ifPresent(furnaceUpgrade -> furnaceUpgrade.syncClient(backpack));
        //Update Sleeping Bag after detachment
        setSleepingBagColor(backpack.getOrDefault(ModDataComponents.SLEEPING_BAG_COLOR, DyeColor.RED.getId()));
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

    public void setLevel(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return this.level;
    }

    public ServerLevel getServerLevel() {
        if(this.level instanceof ServerLevel serverLevel) {
            return serverLevel;
        }
        return null;
    }

    public ItemStacksResourceHandler loadHandler(DataComponentType<ItemContainerContents> data, int defaultSize, int dataId, BiFunction<NonNullList<ItemStack>, Integer, ItemStacksResourceHandler> handlerFunction) {
        if(this.stack.has(data)) {
            NonNullList<ItemStack> stacks = ContainerContentsHelper.getItems(this.stack.get(data), defaultSize);
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
        this.inventory = loadHandler(ModDataComponents.BACKPACK_CONTAINER.get(), getStorageSize(), STORAGE_ID, this::createHandler);
    }

    public void loadUpgrades() {
        this.upgrades = loadHandler(ModDataComponents.UPGRADES.get(), getUpgradesSize(), UPGRADES_ID, this::createUpgradeHandler);
    }

    public void loadTools() {
        this.tools = loadHandler(ModDataComponents.TOOLS_CONTAINER.get(), getToolSize(), TOOLS_ID, this::createHandler);
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

    public void setStarterUpgrade(ItemStack upgrade) {
        if(this.level == null) {
            return;
        }
        if(upgrade.getItem().isEnabled(this.level.enabledFeatures())) {
            for(int i = 0; i < this.upgrades.size(); i++) {
                if(this.upgrades.getResource(i).isEmpty()) {
                    this.upgrades.set(i, ItemResource.of(upgrade), upgrade.getCount());

                    if(upgrade.getItem() instanceof TanksUpgradeItem) {
                        this.updateRenderInfo(TanksUpgradeItem::writeToRenderData);
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

    public ItemStacksResourceHandler getStorage() {
        return this.inventory;
    }

    public ItemStacksResourceHandler getUpgrades() {
        return this.upgrades;
    }

    public ItemStacksResourceHandler getTools() {
        return this.tools;
    }

    public UpgradeManager getUpgradeManager() {
        return this.upgradeManager;
    }

    @Nullable
    public RegistryAccess getRegistryAccess() {
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

    public <T extends UpgradeBase<T>> Optional<T> getUpgrade(Class<T> upgradeClass) {
        return this.getUpgradeManager().getUpgrade(upgradeClass);
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
        setData(ModDataComponents.SLOTS.get(), Slots.updateUnsortables(old, unsortables));
    }

    public void setMemorySlots(List<Pair<Integer, Pair<ItemStack, Boolean>>> memory) {
        Slots old = this.stack.getOrDefault(ModDataComponents.SLOTS, Slots.EMPTY);
        setData(ModDataComponents.SLOTS.get(), Slots.updateMemory(old, memory));
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

    public int getBackpackTankCapacity() {
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
        setDataAndSync(ModDataComponents.RENDER_INFO.get(), new RenderInfo(compound));
    }

    public void updateRenderInfo(Consumer<CompoundTag> compoundConsumer) {
        CompoundTag currentInfo = getRenderInfo().compoundTag().copy();
        compoundConsumer.accept(currentInfo);
        if(!getRenderInfo().compoundTag().equals(currentInfo)) {
            setDataAndSync(ModDataComponents.RENDER_INFO.get(), new RenderInfo(currentInfo));
        }
    }

    public boolean isAbilityEnabled() {
        return this.stack.getOrDefault(ModDataComponents.ABILITY_ENABLED, TravelersBackpackConfig.SERVER.backpackAbilities.forceAbilityEnabled.get());
    }

    public SortSelector.SortType getSortType() {
        int type = this.stack.getOrDefault(ModDataComponents.SORT_TYPE, 0);
        return SortSelector.SortType.values()[type];
    }

    public void setNextSortType() {
        SortSelector.SortType type = getSortType();
        setDataAndSync(ModDataComponents.SORT_TYPE.get(), type.next().ordinal());
    }

    public int getSleepingBagColor() {
        return this.stack.getOrDefault(ModDataComponents.SLEEPING_BAG_COLOR, SleepingBagItem.getDefaultColor());
    }

    public void setSleepingBagColor(int colorId) {
        setDataAndSync(ModDataComponents.SLEEPING_BAG_COLOR.get(), colorId);
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
        return this.stack.getOrDefault(DataComponents.DYED_COLOR, new DyedItemColor(-1)).rgb();
    }

    public int getCooldown() {
        return this.stack.getOrDefault(ModDataComponents.COOLDOWN, 0);
    }

    public void setCooldown(int cooldownInSeconds) {
        setDataAndSync(ModDataComponents.COOLDOWN.get(), cooldownInSeconds);
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
        setDataAndSync(ModDataComponents.UPGRADE_TICK_INTERVAL.get(), ticks);
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
        if(getScreenID() == Reference.ITEM_SCREEN_ID && !getPlayersUsing().stream().filter(p -> !p.level().isClientSide()).toList().isEmpty()) {
            int slotIndex = this.index == -1 ? getPlayersUsing().get(0).getInventory().getSelectedSlot() : this.index;
            PacketDistributor.sendToPlayer((ServerPlayer)this.getPlayersUsing().get(0), new ClientboundSyncItemStackPacket(getPlayersUsing().get(0).getId(), slotIndex, getBackpackStack().typeHolder(), ItemStackUtils.createDataComponentMap(getBackpackStack(), dataComponentTypes)));
            return;
        }
        //Sync stack equipped in back slot
        if(TravelersBackpack.enableIntegration()) {
            //Sync backpack data on clients differently for integration, because of the way backpacks are handled
            if(getScreenID() == Reference.WEARABLE_SCREEN_ID && !getPlayersUsing().stream().filter(p -> !p.level().isClientSide()).toList().isEmpty()) {
                for(Player player : getPlayersUsing()) {
                    if(((ServerPlayer)player).connection == null) continue; //?
                    PacketDistributor.sendToPlayer((ServerPlayer)player, new ClientboundSyncItemStackPacket(player.getId(), -1, getBackpackStack().typeHolder(), ItemStackUtils.createDataComponentMap(getBackpackStack(), dataComponentTypes)));
                }
            }
            return;
        }
        //Sync attachment stack
        if(getBackpackOwner() != null) {
            DataComponentMap.Builder mapBuilder = DataComponentMap.builder();
            ItemStack serverDataHolder = AttachmentUtils.getWearingBackpack(getBackpackOwner()).copy();
            for(DataComponentType type : dataComponentTypes) {
                ItemStack serverDataHolderCopy = ItemStackUtils.reduceSize(serverDataHolder);
                if(!serverDataHolderCopy.has(type)) {
                    continue;
                }
                mapBuilder.set(type, serverDataHolderCopy.get(type));
            }
            if(getBackpackOwner() instanceof ServerPlayer serverPlayer && serverPlayer.connection == null) return; //?
            AttachmentUtils.getAttachment(getBackpackOwner()).ifPresent(data -> data.synchronise(mapBuilder.build()));
        }
    }

    public Optional<Pair<Integer, Pair<ItemStack, Boolean>>> getMemorizedSlot(int slot) {
        return getMemorySlots().stream()
                .filter(pair -> pair.getFirst() == slot)
                .findFirst();
    }

    private ItemStacksResourceHandler createHandler(NonNullList<ItemStack> stacks, int dataId) {
        return new ItemStacksResourceHandler(stacks) {
            @Override
            protected void onContentsChanged(int slot, ItemStack previous) {
                setSlotChanged(slot, StacksHandlerUtils.getStackInSlot(this, slot), dataId);

                if(dataId == TOOLS_ID) {
                    sendDataToClients(ModDataComponents.TOOLS_CONTAINER.get());
                }

                //Update comparator
                saveHandler.run();
            }

            @Override
            public boolean isValid(int slot, ItemResource resource) {
                if(dataId == TOOLS_ID) {
                    return ToolSlotItemHandler.isValid(resource.toStack());
                }
                return BackpackSlotItemHandler.isItemValid(resource.toStack());
            }

            @Override
            protected int getCapacity(int index, ItemResource resource) {
                return resource.isEmpty() ? Item.ABSOLUTE_MAX_STACK_SIZE : Math.min(resource.getMaxStackSize(), 8192);
            }
        };
    }

    public void setSlotChanged(int index, ItemStack stack, int dataId) {
        switch(dataId) {
            case STORAGE_ID:
                this.stack.update(ModDataComponents.BACKPACK_CONTAINER, ItemContainerContents.EMPTY, currentContents -> ContainerContentsHelper.updateStack(currentContents, getStorageSize(), stack, index));
                break;
            case UPGRADES_ID:
                this.stack.update(ModDataComponents.UPGRADES, ItemContainerContents.EMPTY, currentContents -> ContainerContentsHelper.updateStack(currentContents, getUpgradesSize(), stack, index));
                break;
            case TOOLS_ID:
                this.stack.update(ModDataComponents.TOOLS_CONTAINER, ItemContainerContents.EMPTY, currentContents -> ContainerContentsHelper.updateStack(currentContents, getToolSize(), stack, index));
                break;
        }
    }

    public void applyLowestTickInterval() {
        int minimalTickInterval = 100;
        for(int i = 0; i < this.upgrades.size(); i++) {
            ItemStack upgrade = this.upgrades.getResource(i).toStack();
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
        if(level != null && level.isClientSide()) return;

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

    private ItemStacksResourceHandler createUpgradeHandler(NonNullList<ItemStack> stacks, int dataId) {
        return new ItemStacksResourceHandler(stacks) {
            @Override
            protected void onContentsChanged(int slot, ItemStack previousStack) {
                setSlotChanged(slot, StacksHandlerUtils.getStackInSlot(this, slot), dataId);

                //Menu and screen updates
                if(!getPlayersUsing().isEmpty()) {
                    getUpgradeManager().detectedChange(previousStack, slot);
                }

                updateMinimalTickInterval(StacksHandlerUtils.getStackInSlot(this, slot));

                //Update client
                saveHandler.run();
            }

            @Override
            public int getCapacity(int index, ItemResource resource) {
                return 1;
            }

            @Override
            public boolean isValid(int slot, ItemResource resource) {
                boolean isValid = true;
                //Check if upgrade is already present
                for(int i = 0; i < StacksHandlerUtils.getSlots(this); i++) {
                    if(StacksHandlerUtils.getStackInSlot(this, i).getItem() == resource.getItem()) {
                        isValid = false;
                        break;
                    }
                }
                if(!isValid) {
                    return false;
                }
                if(resource.getItem() instanceof TanksUpgradeItem) {
                    isValid = TanksUpgradeItem.canBePutInBackpack(getBackpackTankCapacity(), resource.toStack());
                }
                if(!checkIfUpgradeValid(resource.toStack())) {
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
            stack.set(ModDataComponents.STORAGE_SLOTS.get(), tier.getStorageSlots());
        }
        if(!stack.has(ModDataComponents.UPGRADE_SLOTS)) {
            stack.set(ModDataComponents.UPGRADE_SLOTS.get(), tier.getUpgradeSlots());
        }
        if(!stack.has(ModDataComponents.TOOL_SLOTS)) {
            stack.set(ModDataComponents.TOOL_SLOTS.get(), tier.getToolSlots());
        }
    }

    public void initializeSleepingBag(ItemStack stack) {
        if(TravelersBackpackConfig.serverSpec.isLoaded()) {
            if(!TravelersBackpackConfig.SERVER.backpackUpgrades.enableSleepingBag.get()) { //#TODO change
                if(!stack.has(ModDataComponents.SLEEPING_BAG_COLOR)) {
                    stack.set(ModDataComponents.SLEEPING_BAG_COLOR, -1);
                }
            }
        }
    }

    //Used if slots are removed/added - reconstructs modifiable slots & updates screen
    public void requestMenuAndScreenUpdate() {
        requestMenuUpdate();
        requestScreenUpdate();
    }

    public void requestMenuAndScreenUpdate(int slot) {
        requestMenuUpdate(slot);
        requestScreenUpdate();
    }

    public void requestMenuUpdate() {
        if(!getPlayersUsing().isEmpty()) {
            getPlayersUsing().stream().filter(player -> player.containerMenu instanceof BackpackBaseMenu).forEach(player -> ((BackpackBaseMenu)player.containerMenu).rebuildModifiableSlots());
        }
    }

    public void requestMenuUpdate(int slot) {
        if(!getPlayersUsing().isEmpty()) {
            getPlayersUsing().stream().filter(player -> player.containerMenu instanceof BackpackBaseMenu).forEach(player -> ((BackpackBaseMenu)player.containerMenu).updateModifiableSlotsPosition(slot));
        }
    }

    public void requestScreenUpdate() {
        if(!getPlayersUsing().isEmpty() && !getPlayersUsing().stream().filter(player -> player.level().isClientSide()).toList().isEmpty()) {
            if(Minecraft.getInstance().screen instanceof BackpackScreen screen) {
                screen.updateScreen(false);
            }
        }
    }

    public static void tickForBlockEntity(BackpackBlockEntity backpackBlockEntity) {
        BackpackWrapper wrapper = backpackBlockEntity.getWrapper();
        if(wrapper != BackpackWrapper.DUMMY) {
            if(wrapper.hasTickingUpgrade()) {
                int ticks = (int)backpackBlockEntity.getLevel().getGameTime();
                int upgradeTicks = wrapper.getUpgradeTickInterval();
                if(upgradeTicks == 0) return;

                if(ticks % upgradeTicks == 0) {
                    wrapper.getUpgradeManager().upgrades.forEach(upgradeBase -> {
                        if(upgradeBase instanceof ITickableUpgrade tickable) {
                            boolean tick = true;
                            if(upgradeBase instanceof IEnable enable) {
                                tick = enable.isEnabled(upgradeBase);
                            }
                            if(tick) {
                                tickable.tick(null, backpackBlockEntity.getLevel(), backpackBlockEntity.getBlockPos(), ticks);
                            }
                        }
                    });
                }
            }

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
        if(AttachmentUtils.isWearingBackpack(player)) {
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

        if(player.isAlive() && AttachmentUtils.isWearingBackpack(player)) {
            int ticks = (int)player.level().getGameTime();

            if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, AttachmentUtils.getWearingBackpack(player))) {
                if(BackpackAbilities.isAbilityEnabledInConfig(stack)) {
                    if(stack.getOrDefault(ModDataComponents.ABILITY_ENABLED, TravelersBackpackConfig.SERVER.backpackAbilities.forceAbilityEnabled.get())) {
                        boolean decreaseCooldown = BackpackAbilities.ABILITIES.abilityTick(stack, player);
                        if(stack.getOrDefault(ModDataComponents.COOLDOWN, 0) > 0) {
                            BackpackWrapper wrapper;
                            if(ticks % 100 == 0) {
                                if(decreaseCooldown) {
                                    wrapper = AttachmentUtils.getBackpackWrapper(player, stack, AttachmentUtils.NO_ITEMS.get());
                                    int cooldown = wrapper.getCooldown();
                                    if(player.level().isClientSide()) return;
                                    if(cooldown - 100 < 0) {
                                        wrapper.setCooldown(0);
                                    } else {
                                        wrapper.setCooldown(cooldown - 100);
                                    }
                                }
                            }
                        }
                    } else { //Tick cooldown even if ability switched off
                        if(stack.getOrDefault(ModDataComponents.COOLDOWN.get(), 0) > 0) {
                            BackpackWrapper wrapper;
                            if(ticks % 100 == 0) {
                                wrapper = AttachmentUtils.getBackpackWrapper(player, stack, AttachmentUtils.NO_ITEMS.get());
                                int cooldown = wrapper.getCooldown();
                                if(player.level().isClientSide()) return;
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
                    wrapper = AttachmentUtils.getBackpackWrapper(player, stack, AttachmentUtils.UPGRADES_ONLY.get());
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
