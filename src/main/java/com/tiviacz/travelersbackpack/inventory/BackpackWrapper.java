package com.tiviacz.travelersbackpack.inventory;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.components.Slots;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.ITickableUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.IUpgrade;
import com.tiviacz.travelersbackpack.items.upgrades.TanksUpgradeItem;
import com.tiviacz.travelersbackpack.items.upgrades.UpgradeItem;
import com.tiviacz.travelersbackpack.network.ClientboundSyncItemStackPacket;
import com.tiviacz.travelersbackpack.util.ItemStackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BackpackWrapper {
    public static final BackpackWrapper DUMMY = new BackpackWrapper(ModItems.STANDARD_TRAVELERS_BACKPACK.toStack(), Reference.BLOCK_ENTITY_SCREEN_ID, null, null, null);

    protected ItemStack stack;
    public final ItemStackHandler inventory;
    public final ItemStackHandler upgrades;
    public final ItemStackHandler tools;

    public ItemStackHandler upgradesTracker;

    private final UpgradeManager upgradeManager;
    private final SlotPositioner slotPositioner;
    private Player owner;
    public ArrayList<Player> playersUsing = new ArrayList<>();
    protected HolderLookup.Provider registriesAccess;
    protected LevelAccessor levelAccessor;
    private final byte screenID;
    private int tanksCapacity = 0;

    public Runnable saveHandler = () -> {
    };
    public Runnable abilityHandler = () -> {
    };
    public BlockPos backpackPos;

    public static final byte STORAGE_ID = (byte)0;
    public static final byte UGPRADES_ID = (byte)1;
    public static final byte TOOLS_ID = (byte)2;

    public BackpackWrapper(ItemStack stack, byte screenID, HolderLookup.Provider registriesAccess, @Nullable Player player, @Nullable LevelAccessor levelAccessor) {
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
        int storageSlots = stack.get(ModDataComponents.STORAGE_SLOTS);
        int upgradeSlots = stack.get(ModDataComponents.UPGRADE_SLOTS);
        int toolSlots = stack.get(ModDataComponents.TOOL_SLOTS);

        this.screenID = screenID;
        this.registriesAccess = registriesAccess;
        this.levelAccessor = levelAccessor;

        this.inventory = createHandler(storageSlots, STORAGE_ID);
        this.upgrades = createUpgradeHandler(upgradeSlots, UGPRADES_ID);
        this.tools = createHandler(toolSlots, TOOLS_ID);

        this.upgradesTracker = new ItemStackHandler(this.upgrades.getSlots());

        if(registriesAccess != null) {
            this.loadInventoriesFromComponent(this.registriesAccess, this.stack);
        }

        this.slotPositioner = new SlotPositioner(storageSlots);
        this.setBackpackTankCapacity();

        this.upgradeManager = new UpgradeManager(this);
        if(!this.stack.has(ModDataComponents.RENDER_INFO)) {
            this.setRenderInfo(RenderInfo.EMPTY.compoundTag());
        }

        if(stack.has(ModDataComponents.STARTER_UPGRADES)) {
            List<ItemStack> upgrades = stack.get(ModDataComponents.STARTER_UPGRADES);
            upgrades.forEach(this::setStarterUpgrade);
            stack.remove(ModDataComponents.STARTER_UPGRADES);
        }

        this.setAbilityState();
    }

    public void setBackpackStack(ItemStack backpack) {
        this.stack = backpack;

        //Update client tanks if present
        getUpgradeManager().tanksUpgrade.ifPresent(tanksUpgrade -> tanksUpgrade.syncClients(backpack));
    }

    public ItemStack getBackpackStack() {
        return this.stack;
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

    public void loadInventoriesFromComponent(HolderLookup.Provider provider, ItemStack backpack) {
        if(backpack.has(ModDataComponents.BACKPACK_CONTAINER)) {
            BackpackContainerContents contents = backpack.get(ModDataComponents.BACKPACK_CONTAINER);
            if(contents.getItems().size() < getStorageSize()) {
                contents = expandContents(contents, getStorageSize(), backpack, ModDataComponents.BACKPACK_CONTAINER.get());
            }
            this.inventory.deserializeNBT(provider, contents.toNbt(provider));
        }
        if(backpack.has(ModDataComponents.UPGRADES)) {
            BackpackContainerContents contents = backpack.get(ModDataComponents.UPGRADES);
            if(contents.getItems().size() < getUpgradesSize()) {
                contents = expandContents(contents, getUpgradesSize(), backpack, ModDataComponents.UPGRADES.get());
            }
            this.upgrades.deserializeNBT(provider, contents.toNbt(provider));
            this.upgradesTracker.deserializeNBT(provider, contents.toNbt(provider));
        }

        if(backpack.has(ModDataComponents.TOOLS_CONTAINER)) {
            BackpackContainerContents contents = backpack.get(ModDataComponents.TOOLS_CONTAINER);
            if(contents.getItems().size() < getToolSize()) {
                contents = expandContents(contents, getToolSize(), backpack, ModDataComponents.TOOLS_CONTAINER.get());
            }
            this.tools.deserializeNBT(provider, contents.toNbt(provider));
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
        if(this.levelAccessor == null) {
            return;
        }
        if(upgrade.getItem().isEnabled(this.levelAccessor.enabledFeatures())) {
            this.upgrades.setStackInSlot(0, upgrade);
            this.upgradesTracker.setStackInSlot(0, upgrade);

            if(upgrade.getItem() instanceof TanksUpgradeItem) {
                this.setRenderInfo(TanksUpgradeItem.writeToRenderData().compoundTag());
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

    public SlotPositioner getSlotPositioner() {
        return this.slotPositioner;
    }

    public HolderLookup.Provider getRegistriesAccess() {
        return this.registriesAccess;
    }

    public List<Integer> getUnsortableSlots() {
        return this.stack.getOrDefault(ModDataComponents.SLOTS, Slots.EMPTY).unsortables();
    }

    public List<Pair<Integer, Pair<ItemStack, Boolean>>> getMemorySlots() {
        return this.stack.getOrDefault(ModDataComponents.SLOTS, Slots.EMPTY).memory();
    }

    public byte getScreenID() {
        return this.screenID;
    }

    public Component getBackpackScreenTitle() {
        return this.stack.has(DataComponents.CUSTOM_NAME) ? this.stack.get(DataComponents.CUSTOM_NAME) : Component.translatable("screen.travelersbackpack.title");
    }

    public void setUnsortableSlots(List<Integer> unsortables) {
        Slots old = this.stack.getOrDefault(ModDataComponents.SLOTS, Slots.EMPTY);
        this.stack.set(ModDataComponents.SLOTS, Slots.updateUnsortables(old, unsortables));
        this.saveHandler.run();
    }

    public void setMemorySlots(List<Pair<Integer, Pair<ItemStack, Boolean>>> memory) {
        Slots old = this.stack.getOrDefault(ModDataComponents.SLOTS, Slots.EMPTY);
        this.stack.set(ModDataComponents.SLOTS, Slots.updateMemory(old, memory));
        this.saveHandler.run();
    }

    public boolean showToolSlots() {
        return this.stack.getOrDefault(ModDataComponents.SHOW_TOOL_SLOTS, false);
    }

    public void setShowToolSlots(boolean show) {
        this.stack.set(ModDataComponents.SHOW_TOOL_SLOTS, show);
        this.saveHandler.run();
    }

    public boolean tanksVisible() {
        if(this.stack.has(ModDataComponents.RENDER_INFO)) {
            return this.stack.get(ModDataComponents.RENDER_INFO).hasTanks();
        }
        return getUpgradeManager().tanksUpgrade.isPresent();
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
        SlotPositioner pos = getSlotPositioner();
        int rows = pos.getRows() + (pos.isExtended() ? 2 : 0);
        this.tanksCapacity = Tiers.of(this.stack.getOrDefault(ModDataComponents.TIER, 0)).getTankCapacityPerRow() * rows;
    }

    public void setRenderInfo(CompoundTag compound) {
        this.stack.set(ModDataComponents.RENDER_INFO, new RenderInfo(compound));
        this.saveHandler.run();
    }

    public void removeRenderInfo() {
        this.stack.set(ModDataComponents.RENDER_INFO, new RenderInfo(new CompoundTag()));
        this.saveHandler.run();
    }

    public boolean isAbilityEnabled() {
        return this.stack.getOrDefault(ModDataComponents.ABILITY_ENABLED, TravelersBackpackConfig.SERVER.backpackAbilities.forceAbilityEnabled.get());
    }

    public void setAbilityEnabled(boolean enabled) {
        this.stack.set(ModDataComponents.ABILITY_ENABLED, enabled);
        this.saveHandler.run();
        this.abilityHandler.run();
    }

    public boolean hasSleepingBag() {
        return this.stack.has(ModDataComponents.SLEEPING_BAG_COLOR);
    }

    public int getSleepingBagColor() {
        return this.stack.getOrDefault(ModDataComponents.SLEEPING_BAG_COLOR, -1);
    }

    public void setSleepingBagColor(int colorId) {
        this.stack.set(ModDataComponents.SLEEPING_BAG_COLOR, colorId);
    }

    public boolean isOwner(Player player) {
        if(getBackpackOwner() != null) {
            return getBackpackOwner().getId() == player.getId();
        }
        return true;
    }

    public void setVisibility(boolean visibility) {
        this.stack.set(ModDataComponents.IS_VISIBLE, visibility);
        this.saveHandler.run();

        sendDataToClients(ModDataComponents.IS_VISIBLE.get());
    }

    public int getCooldown() {
        return this.stack.getOrDefault(ModDataComponents.COOLDOWN, 0);
    }

    public void setCooldown(int cooldownInSeconds) {
        this.stack.set(ModDataComponents.COOLDOWN, cooldownInSeconds);
        this.saveHandler.run();

        sendDataToClients(ModDataComponents.COOLDOWN.get());
    }

    //Block Entity
    public void decreaseCooldown() {
        if(getCooldown() > 0) {
            this.stack.update(ModDataComponents.COOLDOWN, 0, currentCooldown -> currentCooldown - 1);
            this.saveHandler.run();
        }
    }

    public void setAbilityState() {
        boolean abilityDisabled = !BackpackAbilities.isAbilityEnabledInConfig(getBackpackStack());
        if(abilityDisabled) {
            if(!getBackpackStack().has(ModDataComponents.ABILITY_ENABLED)) {
                this.setAbilityEnabled(false);
                return;
            }
            if(getBackpackStack().getOrDefault(ModDataComponents.ABILITY_ENABLED, false)) {
                this.setAbilityEnabled(false);
            }
            return;
        }
        if(!getBackpackStack().has(ModDataComponents.ABILITY_ENABLED)) {
            if(TravelersBackpackConfig.SERVER.backpackAbilities.forceAbilityEnabled.get()) {
                this.setAbilityEnabled(true);
            }
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
        this.stack.set(ModDataComponents.UPGRADE_TICK_INTERVAL, ticks);
    }

    public void removeUpgradeTickInterval() {
        this.stack.remove(ModDataComponents.UPGRADE_TICK_INTERVAL);
    }

    public void sendDataToClients(DataComponentType... dataComponentTypes) {
        if(getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) return;

        if(getScreenID() == Reference.ITEM_SCREEN_ID && !getPlayersUsing().stream().filter(p -> !p.level().isClientSide).toList().isEmpty()) {
            PacketDistributor.sendToPlayer((ServerPlayer)this.getPlayersUsing().get(0), new ClientboundSyncItemStackPacket(getPlayersUsing().get(0).getId(), getScreenID() == Reference.WEARABLE_SCREEN_ID ? -1 : getPlayersUsing().get(0).getInventory().selected, getBackpackStack(), ItemStackUtils.createDataComponentMap(getBackpackStack(), dataComponentTypes)));
            return;
        }
        if(TravelersBackpack.enableIntegration()) {
            //Sync backpack data on clients differently, because of the way backpacks are handled
            if(getScreenID() == Reference.WEARABLE_SCREEN_ID && !getPlayersUsing().stream().filter(p -> !p.level().isClientSide).toList().isEmpty()) {
                for(Player player : getPlayersUsing()) {
                    PacketDistributor.sendToPlayer((ServerPlayer)player, new ClientboundSyncItemStackPacket(player.getId(), -1, getBackpackStack(), ItemStackUtils.createDataComponentMap(getBackpackStack(), dataComponentTypes)));
                }
                return;
            }
        }
        //Sync selected backpack attachment data on clients
        if(getUpgradeManager().getWrapper().getBackpackOwner() != null) {
            DataComponentMap.Builder mapBuilder = DataComponentMap.builder();
            ItemStack serverDataHolder = AttachmentUtils.getWearingBackpack(getUpgradeManager().getWrapper().getBackpackOwner()).copy();
            for(DataComponentType type : dataComponentTypes) {
                ItemStack serverDataHolderCopy = ItemStackUtils.reduceSize(serverDataHolder);
                if(!serverDataHolderCopy.has(type)) {
                    continue;
                }
                mapBuilder.set(type, serverDataHolderCopy.get(type));
            }
            AttachmentUtils.getAttachment(getUpgradeManager().getWrapper().getBackpackOwner()).ifPresent(data -> data.synchronise(mapBuilder.build()));
        }
    }

    public Optional<Pair<Integer, Pair<ItemStack, Boolean>>> getMemorizedSlot(int slot) {
        return getMemorySlots().stream()
                .filter(pair -> pair.getFirst() == slot)
                .findFirst();
    }

    private ItemStackHandler createHandler(int size, byte dataId) {
        return new ItemStackHandler(size) {
            @Override
            protected void onContentsChanged(int slot) {
                setSlotChanged(slot, getStackInSlot(slot), dataId);

                if(dataId == TOOLS_ID) {
                    sendDataToClients(ModDataComponents.TOOLS_CONTAINER.get());
                }

                //Update comparator
                saveHandler.run();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if(dataId == (byte)2) {
                    return ToolSlotItemHandler.isValid(stack);
                }
                return BackpackSlotItemHandler.isItemValid(stack);
            }
        };
    }

    public void setSlotChanged(int index, ItemStack stack, byte dataId) {
        switch(dataId) {
            case STORAGE_ID:
                this.stack.update(ModDataComponents.BACKPACK_CONTAINER, new BackpackContainerContents(this.getStorage().getSlots()), new BackpackContainerContents.Slot(index, stack), BackpackContainerContents::updateSlot);
                break;
            case UGPRADES_ID:
                this.stack.update(ModDataComponents.UPGRADES, new BackpackContainerContents(this.getUpgrades().getSlots()), new BackpackContainerContents.Slot(index, stack), BackpackContainerContents::updateSlot);
                break;
            case TOOLS_ID:
                this.stack.update(ModDataComponents.TOOLS_CONTAINER, new BackpackContainerContents(this.getTools().getSlots()), new BackpackContainerContents.Slot(index, stack), BackpackContainerContents::updateSlot);
                break;
        }
    }

    public void updateMinimalTickInterval(ItemStack newStack) {
        if(getScreenID() == Reference.WEARABLE_SCREEN_ID && (newStack.getItem() == ModItems.FEEDING_UPGRADE.get() || newStack.getItem() == ModItems.MAGNET_UPGRADE.get())) {
            if(newStack.getOrDefault(ModDataComponents.UPGRADE_ENABLED, true)) {
                int minimalInterval = 100;
                for(int i = 0; i < this.upgrades.getSlots(); i++) {
                    ItemStack upgrade = this.upgrades.getStackInSlot(i);
                    if(upgrade.has(ModDataComponents.COOLDOWN)) {
                        minimalInterval = Math.min(minimalInterval, upgrade.get(ModDataComponents.COOLDOWN));
                    }
                }
                if(!canUpgradeTick() || getUpgradeTickInterval() != minimalInterval) {
                    setUpgradeTickInterval(minimalInterval);
                }
            } else if(canUpgradeTick() && !hasTickingUpgrade()) {
                removeUpgradeTickInterval();
            }
        }
    }

    private ItemStackHandler createUpgradeHandler(int size, byte dataId) {
        return new ItemStackHandler(size) {
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
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
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
                    if(upgradeItem.isEnabled(player.level().enabledFeatures())) {
                        return true;
                    }
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

    public void requestMenuAndScreenUpdate(boolean onlyTab) {
        requestMenuUpdate(onlyTab);
        requestScreenUpdate();
    }

    public void requestMenuUpdate(boolean onlyTab) {
        if(!getPlayersUsing().isEmpty() && !getPlayersUsing().stream().filter(player -> player.containerMenu instanceof BackpackBaseMenu).toList().isEmpty()) {
            for(Player player : getPlayersUsing().stream().filter(player -> player.containerMenu instanceof BackpackBaseMenu).toList()) {
                if(onlyTab) {
                    ((BackpackBaseMenu)player.containerMenu).updateModifiableSlots();
                } else {
                    ((BackpackBaseMenu)player.containerMenu).updateSlots();
                }
            }
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
    public static BackpackWrapper getBackpackWrapper(Player player, ItemStack backpack) {
        if(AttachmentUtils.isWearingBackpack(player)) {
            if(player.containerMenu instanceof BackpackItemMenu menu && menu.getWrapper().getScreenID() == Reference.WEARABLE_SCREEN_ID) {
                return menu.getWrapper();
            } else {
                for(Player otherPlayer : player.level().players()) {
                    if(otherPlayer.containerMenu instanceof BackpackItemMenu menu && menu.getWrapper().isOwner(player) && menu.getWrapper().getScreenID() == Reference.WEARABLE_SCREEN_ID) {
                        return menu.getWrapper();
                    }
                }
                return new BackpackWrapper(backpack, Reference.WEARABLE_SCREEN_ID, player.level().registryAccess(), player, player.level());
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
                                    wrapper = AttachmentUtils.getBackpackWrapper(player, stack);
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
                        if(stack.getOrDefault(ModDataComponents.COOLDOWN.get(), 0) > 0) {
                            BackpackWrapper wrapper;
                            if(ticks % 100 == 0) {
                                wrapper = AttachmentUtils.getBackpackWrapper(player, stack);
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
                BackpackWrapper wrapper;
                if(ticks % upgradeTicks == 0) {
                    wrapper = AttachmentUtils.getBackpackWrapper(player, stack);
                    for(int i = 0; i < wrapper.getUpgradeManager().mappedUpgrades.size(); i++) {
                        Optional<? extends IUpgrade> upgrade = wrapper.getUpgradeManager().mappedUpgrades.get(i);

                        if(upgrade != null && upgrade.isPresent() && upgrade.get() instanceof ITickableUpgrade) {
                            ((ITickableUpgrade)upgrade.get()).tick(player, player.level(), player.blockPosition(), ticks);
                        }
                    }
                }
            }
        }
    }
}
