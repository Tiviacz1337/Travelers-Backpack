package com.tiviacz.travelersbackpack.inventory;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.components.Fluids;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
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
import com.tiviacz.travelersbackpack.util.NbtHelper;
import com.tiviacz.travelersbackpack.util.PacketDistributorHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BackpackWrapper {
    public static final BackpackWrapper DUMMY = new BackpackWrapper(ModItems.STANDARD_TRAVELERS_BACKPACK.getDefaultInstance(), Reference.BLOCK_ENTITY_SCREEN_ID, null, null);

    protected ItemStack stack;
    public final ItemStackHandler inventory;
    public final ItemStackHandler upgrades;
    public final ItemStackHandler tools;

    public ItemStackHandler upgradesTracker;

    private final UpgradeManager upgradeManager;
    private final SlotPositioner slotPositioner;
    private Player owner;
    public ArrayList<Player> playersUsing = new ArrayList<>();
    protected LevelAccessor levelAccessor;
    private final byte screenID;
    private long tanksCapacity = 0;

    public Runnable saveHandler = () -> {
    };
    public Runnable abilityHandler = () -> {
    };
    public BlockPos backpackPos;

    public static final byte STORAGE_ID = (byte)0;
    public static final byte UGPRADES_ID = (byte)1;
    public static final byte TOOLS_ID = (byte)2;

    public BackpackWrapper(ItemStack stack, byte screenID, @Nullable Player player, @Nullable LevelAccessor levelAccessor) {
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
        int storageSlots = NbtHelper.get(stack, ModDataHelper.STORAGE_SLOTS);
        int upgradeSlots = NbtHelper.get(stack, ModDataHelper.UPGRADE_SLOTS);
        int toolSlots = NbtHelper.get(stack, ModDataHelper.TOOL_SLOTS);

        this.screenID = screenID;
        this.levelAccessor = levelAccessor;

        this.inventory = createHandler(storageSlots, STORAGE_ID);
        this.upgrades = createUpgradeHandler(upgradeSlots, UGPRADES_ID);
        this.tools = createHandler(toolSlots, TOOLS_ID);

        this.upgradesTracker = new ItemStackHandler(this.upgrades.getSlots());
        this.loadInventoriesFromComponent(this.stack);

        this.slotPositioner = new SlotPositioner(storageSlots);
        this.setBackpackTankCapacity();

        this.upgradeManager = new UpgradeManager(this);
        if(!NbtHelper.has(stack, ModDataHelper.RENDER_INFO)) {
            this.setRenderInfo(RenderInfo.EMPTY.compoundTag());
        }

        if(NbtHelper.has(stack, ModDataHelper.STARTER_UPGRADES)) {
            List<ItemStack> upgrades = NbtHelper.get(stack, ModDataHelper.STARTER_UPGRADES);
            upgrades.forEach(this::setStarterUpgrade);
            NbtHelper.remove(stack, ModDataHelper.STARTER_UPGRADES);
        }

        this.converter(stack, storageSlots, toolSlots);

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

    public void loadInventoriesFromComponent(ItemStack backpack) {
        if(NbtHelper.has(backpack, ModDataHelper.BACKPACK_CONTAINER)) {
            CompoundTag contents = NbtHelper.getHandlerNbt(backpack, ModDataHelper.BACKPACK_CONTAINER);
            if(contents.contains("Size")) {
                if(contents.getInt("Size") < getStorageSize()) {
                    contents = expandContents(contents, getStorageSize(), backpack, ModDataHelper.BACKPACK_CONTAINER);
                }
            }
            this.inventory.deserializeNBT(contents);
        }
        if(NbtHelper.has(backpack, ModDataHelper.UPGRADES)) {
            CompoundTag contents = NbtHelper.getHandlerNbt(backpack, ModDataHelper.UPGRADES);
            if(contents.contains("Size")) {
                if(contents.getInt("Size") < getUpgradesSize()) {
                    contents = expandContents(contents, getUpgradesSize(), backpack, ModDataHelper.UPGRADES);
                }
            }
            this.upgrades.deserializeNBT(contents);
            this.upgradesTracker.deserializeNBT(contents);
        }

        if(NbtHelper.has(backpack, ModDataHelper.TOOLS_CONTAINER)) {
            CompoundTag contents = NbtHelper.getHandlerNbt(backpack, ModDataHelper.TOOLS_CONTAINER);
            if(contents.contains("Size")) {
                if(contents.getInt("Size") < getToolSize()) {
                    contents = expandContents(contents, getToolSize(), backpack, ModDataHelper.TOOLS_CONTAINER);
                }
            }
            this.tools.deserializeNBT(contents);
        }
    }

    public CompoundTag expandContents(CompoundTag contents, int size, ItemStack backpack, String type) {
        if(contents.getInt("Size") < size) {
            NonNullList<ItemStack> stacks = NonNullList.withSize(size, ItemStack.EMPTY);
            ListTag tagList = contents.getList("Items", 10);
            for(int i = 0; i < tagList.size(); ++i) {
                CompoundTag itemTags = tagList.getCompound(i);
                int slot = itemTags.getInt("Slot");
                if(slot >= 0 && slot < stacks.size()) {
                    stacks.set(slot, ItemStack.of(itemTags));
                }
            }
            CompoundTag expandedContents = NbtHelper.serializeHandler(new ItemStackHandler(stacks));
            backpack.getOrCreateTag().put(type, expandedContents);
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
        return NbtHelper.getOrDefault(this.stack, ModDataHelper.STORAGE_SLOTS, Tiers.LEATHER.getStorageSlots());
    }

    public int getUpgradesSize() {
        return NbtHelper.getOrDefault(this.stack, ModDataHelper.UPGRADE_SLOTS, Tiers.LEATHER.getUpgradeSlots());
    }

    public int getToolSize() {
        return NbtHelper.getOrDefault(this.stack, ModDataHelper.TOOL_SLOTS, Tiers.LEATHER.getToolSlots());
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

    public List<Integer> getUnsortableSlots() {
        return NbtHelper.getOrDefault(this.stack, ModDataHelper.UNSORTABLE_SLOTS, List.of());
    }

    public List<Pair<Integer, Pair<ItemStack, Boolean>>> getMemorySlots() {
        return NbtHelper.getOrDefault(this.stack, ModDataHelper.MEMORY_SLOTS, List.of());
    }

    public byte getScreenID() {
        return this.screenID;
    }

    public void setUnsortableSlots(List<Integer> unsortables) {
        NbtHelper.set(this.stack, ModDataHelper.UNSORTABLE_SLOTS, unsortables);
        this.saveHandler.run();
    }

    public void setMemorySlots(List<Pair<Integer, Pair<ItemStack, Boolean>>> memory) {
        NbtHelper.set(this.stack, ModDataHelper.MEMORY_SLOTS, memory);
        this.saveHandler.run();
    }

    public boolean showToolSlots() {
        return NbtHelper.getOrDefault(this.stack, ModDataHelper.SHOW_TOOL_SLOTS, false);
    }

    public void setShowToolSlots(boolean show) {
        NbtHelper.set(this.stack, ModDataHelper.SHOW_TOOL_SLOTS, show);
        this.saveHandler.run();
    }

    public boolean tanksVisible() {
        if(NbtHelper.has(this.stack, ModDataHelper.RENDER_INFO)) {
            return ((RenderInfo)NbtHelper.get(this.stack, ModDataHelper.RENDER_INFO)).hasTanks();
        }
        return getUpgradeManager().tanksUpgrade.isPresent();
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
        SlotPositioner pos = getSlotPositioner();
        int rows = pos.getRows() + (pos.isExtended() ? 2 : 0);
        this.tanksCapacity = Tiers.of(NbtHelper.getOrDefault(this.stack, ModDataHelper.TIER, 0)).getTankCapacityPerRow() * rows;
    }

    public void setRenderInfo(CompoundTag compound) {
        NbtHelper.set(this.stack, ModDataHelper.RENDER_INFO, new RenderInfo(compound));
        this.saveHandler.run();
    }

    public void removeRenderInfo() {
        NbtHelper.set(this.stack, ModDataHelper.RENDER_INFO, new RenderInfo(new CompoundTag()));
        this.saveHandler.run();
    }

    public boolean isAbilityEnabled() {
        return NbtHelper.getOrDefault(this.stack, ModDataHelper.ABILITY_ENABLED, TravelersBackpackConfig.getConfig().backpackAbilities.forceAbilityEnabled);
    }

    public void setAbilityEnabled(boolean enabled) {
        NbtHelper.set(this.stack, ModDataHelper.ABILITY_ENABLED, enabled);
        this.saveHandler.run();
        this.abilityHandler.run();
    }

    public boolean hasSleepingBag() {
        return NbtHelper.has(this.stack, ModDataHelper.SLEEPING_BAG_COLOR);
    }

    public int getSleepingBagColor() {
        return NbtHelper.getOrDefault(this.stack, ModDataHelper.SLEEPING_BAG_COLOR, DyeColor.RED.getId());
    }

    public void setSleepingBagColor(int colorId) {
        NbtHelper.set(this.stack, ModDataHelper.SLEEPING_BAG_COLOR, colorId);
    }

    public boolean isOwner(Player player) {
        if(getBackpackOwner() != null) {
            return getBackpackOwner().getId() == player.getId();
        }
        return true;
    }

    public void setVisibility(boolean visibility) {
        //this.stack.set(ModDataComponents.IS_VISIBLE.get(), visibility);
        NbtHelper.set(this.stack, ModDataHelper.IS_VISIBLE, visibility);
        this.saveHandler.run();

        sendDataToClients(ModDataHelper.IS_VISIBLE);
    }

    public int getCooldown() {
        return NbtHelper.getOrDefault(this.stack, ModDataHelper.COOLDOWN, 0);
    }

    public void setCooldown(int cooldownInSeconds) {
        NbtHelper.set(this.stack, ModDataHelper.COOLDOWN, cooldownInSeconds);
        this.saveHandler.run();

        sendDataToClients(ModDataHelper.COOLDOWN);
    }

    //Block Entity
    public void decreaseCooldown() {
        if(getCooldown() > 0) {
            int currentCooldown = getCooldown();
            NbtHelper.set(this.stack, ModDataHelper.COOLDOWN, currentCooldown - 1);
            this.saveHandler.run();
        }
    }

    public void setAbilityState() {
        if(!TravelersBackpackConfig.getConfig().backpackAbilities.enableBackpackAbilities || !BackpackAbilities.ALLOWED_ABILITIES.contains(getBackpackStack().getItem())) {
            if(NbtHelper.getOrDefault(getBackpackStack(), ModDataHelper.ABILITY_ENABLED, false)) {
                this.setAbilityEnabled(false);
            }
            return;
        }
        if(!NbtHelper.has(getBackpackStack(), ModDataHelper.ABILITY_ENABLED)) {
            if(TravelersBackpackConfig.getConfig().backpackAbilities.forceAbilityEnabled) {
                this.setAbilityEnabled(true);
            }
        }
    }

    public boolean canUpgradeTick() {
        return NbtHelper.has(this.stack, ModDataHelper.UPGRADE_TICK_INTERVAL);
    }

    public boolean hasTickingUpgrade() {
        return this.upgradeManager.hasTickingUpgrade();
    }

    public int getUpgradeTickInterval() {
        return NbtHelper.getOrDefault(this.stack, ModDataHelper.UPGRADE_TICK_INTERVAL, 100);
    }

    public void setUpgradeTickInterval(int ticks) {
        NbtHelper.set(this.stack, ModDataHelper.UPGRADE_TICK_INTERVAL, ticks);
    }

    public void removeUpgradeTickInterval() {
        NbtHelper.remove(this.stack, ModDataHelper.UPGRADE_TICK_INTERVAL);
    }

    public void sendDataToClients(String... keys) {
        if(getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) return;

        if(getScreenID() == Reference.ITEM_SCREEN_ID && !getPlayersUsing().stream().filter(p -> !p.level().isClientSide).toList().isEmpty()) {
            CompoundTag builder = new CompoundTag();
            ItemStack serverDataHolder = getBackpackStack().copy();
            ItemStack serverDataHolderCopy = ItemStackUtils.reduceSize(serverDataHolder);
            for(String key : keys) {
                if(!serverDataHolderCopy.getTag().contains(key)) continue;
                builder.put(key, serverDataHolderCopy.getTag().get(key));
            }
            PacketDistributorHelper.sendToPlayer((ServerPlayer)this.getPlayersUsing().get(0), new ClientboundSyncItemStackPacket(getPlayersUsing().get(0).getId(), getScreenID() == Reference.WEARABLE_SCREEN_ID ? -1 : getPlayersUsing().get(0).getInventory().selected, getBackpackStack(), builder));
            return;
        }
        if(TravelersBackpack.enableIntegration()) {
            //Sync backpack data on clients differently, because of the way backpacks are handled
            if(getScreenID() == Reference.WEARABLE_SCREEN_ID && !getPlayersUsing().stream().filter(p -> !p.level().isClientSide).toList().isEmpty()) {
                for(Player player : getPlayersUsing()) {
                    CompoundTag builder = new CompoundTag();
                    ItemStack serverDataHolder = getBackpackStack().copy();
                    ItemStack serverDataHolderCopy = ItemStackUtils.reduceSize(serverDataHolder);
                    for(String key : keys) {
                        if(!serverDataHolderCopy.getTag().contains(key)) continue;
                        builder.put(key, serverDataHolderCopy.getTag().get(key));
                    }
                    PacketDistributorHelper.sendToPlayer((ServerPlayer)player, new ClientboundSyncItemStackPacket(player.getId(), -1, getBackpackStack(), builder));
                }
                return;
            }
        }
        //Sync selected backpack attachment data on clients
        if(getUpgradeManager().getWrapper().getBackpackOwner() != null) {
            CompoundTag builder = new CompoundTag();
            ItemStack serverDataHolder = ComponentUtils.getWearingBackpack(getBackpackOwner()).copy();
            ItemStack serverDataHolderCopy = ItemStackUtils.reduceSize(serverDataHolder);
            for(String key : keys) {
                if(!serverDataHolderCopy.getTag().contains(key)) continue;
                builder.put(key, serverDataHolderCopy.getTag().get(key));
            }
            ComponentUtils.getComponent(getUpgradeManager().getWrapper().getBackpackOwner()).ifPresent(data -> data.synchronise(builder));
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
                    sendDataToClients(ModDataHelper.TOOLS_CONTAINER);
                }

                //Update comparator
                saveHandler.run();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
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
                NbtHelper.update(this.stack, ModDataHelper.BACKPACK_CONTAINER, this.getStorage().getSlots(), index, stack);
                break;
            case UGPRADES_ID:
                NbtHelper.update(this.stack, ModDataHelper.UPGRADES, this.getUpgrades().getSlots(), index, stack);
                break;
            case TOOLS_ID:
                NbtHelper.update(this.stack, ModDataHelper.TOOLS_CONTAINER, this.getTools().getSlots(), index, stack);
                break;
        }
    }

    public void updateMinimalTickInterval(ItemStack newStack) {
        if(getScreenID() == Reference.WEARABLE_SCREEN_ID && (newStack.getItem() == ModItems.FEEDING_UPGRADE || newStack.getItem() == ModItems.MAGNET_UPGRADE)) {
            if(NbtHelper.getOrDefault(newStack, ModDataHelper.UPGRADE_ENABLED, true)) {
                int minimalInterval = 100;
                for(int i = 0; i < this.upgrades.getSlots(); i++) {
                    ItemStack upgrade = this.upgrades.getStackInSlot(i);
                    if(NbtHelper.has(upgrade, ModDataHelper.COOLDOWN)) {
                        minimalInterval = Math.min(minimalInterval, NbtHelper.get(upgrade, ModDataHelper.COOLDOWN));
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
                    Player player = getPlayersUsing().isEmpty() ? null : getPlayersUsing().get(0);
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
        return NbtHelper.has(stack, ModDataHelper.STORAGE_SLOTS) && NbtHelper.has(stack, ModDataHelper.UPGRADE_SLOTS) && NbtHelper.has(stack, ModDataHelper.TOOL_SLOTS);
    }

    public static void initializeSize(ItemStack stack) {
        Tiers.Tier tier = Tiers.LEATHER;
        if(NbtHelper.has(stack, ModDataHelper.TIER)) {
            tier = Tiers.of((int)NbtHelper.get(stack, ModDataHelper.TIER));
        }
        if(!NbtHelper.has(stack, ModDataHelper.STORAGE_SLOTS)) {
            NbtHelper.set(stack, ModDataHelper.STORAGE_SLOTS, tier.getStorageSlots());
        }
        if(!NbtHelper.has(stack, ModDataHelper.UPGRADE_SLOTS)) {
            NbtHelper.set(stack, ModDataHelper.UPGRADE_SLOTS, tier.getUpgradeSlots());
        }
        if(!NbtHelper.has(stack, ModDataHelper.TOOL_SLOTS)) {
            NbtHelper.set(stack, ModDataHelper.TOOL_SLOTS, tier.getToolSlots());
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
                screen.updateScreen();
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
        if(ComponentUtils.isWearingBackpack(player)) {
            if(player.containerMenu instanceof BackpackItemMenu menu && menu.getWrapper().getScreenID() == Reference.WEARABLE_SCREEN_ID) {
                return menu.getWrapper();
            } else {
                for(Player otherPlayer : player.level().players()) {
                    if(otherPlayer.containerMenu instanceof BackpackItemMenu menu && menu.getWrapper().isOwner(player) && menu.getWrapper().getScreenID() == Reference.WEARABLE_SCREEN_ID) {
                        return menu.getWrapper();
                    }
                }
                return new BackpackWrapper(backpack, Reference.WEARABLE_SCREEN_ID, player, player.level());
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
                    if(NbtHelper.getOrDefault(stack, ModDataHelper.ABILITY_ENABLED, TravelersBackpackConfig.getConfig().backpackAbilities.forceAbilityEnabled)) {
                        boolean decreaseCooldown = BackpackAbilities.ABILITIES.abilityTick(stack, player);
                        if(NbtHelper.getOrDefault(stack, ModDataHelper.COOLDOWN, 0) > 0) {
                            BackpackWrapper wrapper;
                            if(ticks % 100 == 0) {
                                if(decreaseCooldown) {
                                    wrapper = ComponentUtils.getBackpackWrapper(player, stack);
                                    int cooldown = wrapper.getCooldown();
                                    if(player.level().isClientSide) return;
                                    wrapper.setCooldown(cooldown - 100);
                                }
                            }
                        }
                    }
                }
            } else if(NbtHelper.getOrDefault(stack, ModDataHelper.ABILITY_ENABLED, false)) {
                NbtHelper.set(stack, ModDataHelper.ABILITY_ENABLED, false);
            }
            if(NbtHelper.has(stack, ModDataHelper.UPGRADE_TICK_INTERVAL)) {
                int upgradeTicks = NbtHelper.get(stack, ModDataHelper.UPGRADE_TICK_INTERVAL);
                BackpackWrapper wrapper;
                if(ticks % upgradeTicks == 0) {
                    wrapper = ComponentUtils.getBackpackWrapper(player, stack);
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

    public void converter(ItemStack stack, int storageSlots, int toolSlots) {
        if(NbtHelper.has(stack, ModDataHelper.BACKPACK_CONTAINER)) {
            if(storageSlots != ((NonNullList<ItemStack>)NbtHelper.get(stack, ModDataHelper.BACKPACK_CONTAINER)).size()) {
                stack.getTag().put(ModDataHelper.BACKPACK_CONTAINER, NbtHelper.expandTag(stack, ModDataHelper.BACKPACK_CONTAINER, storageSlots));
            }
        }

        if(NbtHelper.has(stack, ModDataHelper.TOOLS_CONTAINER)) {
            if(toolSlots != ((NonNullList<ItemStack>)NbtHelper.get(stack, ModDataHelper.TOOLS_CONTAINER)).size()) {
                stack.getTag().put(ModDataHelper.TOOLS_CONTAINER, NbtHelper.expandTag(stack, ModDataHelper.TOOLS_CONTAINER, toolSlots));
            }
        }

        //Old Data Conversion (Should not run in regular case)
        if(NbtHelper.has(stack, ModDataHelper.LEFT_TANK) || NbtHelper.has(stack, ModDataHelper.RIGHT_TANK)) {
            CompoundTag oldTank = NbtHelper.has(stack, ModDataHelper.LEFT_TANK) ? stack.getTag().getCompound(ModDataHelper.LEFT_TANK) : new CompoundTag();
            CompoundTag oldTank2 = NbtHelper.has(stack, ModDataHelper.RIGHT_TANK) ? stack.getTag().getCompound(ModDataHelper.RIGHT_TANK) : new CompoundTag();
            FluidVariantWrapper leftFluidStack = FluidVariantWrapper.blank();
            FluidVariantWrapper rightFluidStack = FluidVariantWrapper.blank();

            if(!oldTank.isEmpty()) {
                FluidVariantWrapper fluidStack = FluidVariantWrapper.parseOptional(oldTank);
                if(!fluidStack.isEmpty()) {
                    leftFluidStack = fluidStack;
                }
            }
            if(!oldTank2.isEmpty()) {
                FluidVariantWrapper fluidStack = FluidVariantWrapper.parseOptional(oldTank2);
                if(!fluidStack.isEmpty()) {
                    rightFluidStack = fluidStack;
                }
            }
            ItemStack oldTanks = ModItems.TANKS_UPGRADE.getDefaultInstance();
            NbtHelper.set(oldTanks, ModDataHelper.FLUIDS, new Fluids(leftFluidStack, rightFluidStack));
            this.setStarterUpgrade(oldTanks);

            stack.getTag().remove(ModDataHelper.LEFT_TANK);
            stack.getTag().remove(ModDataHelper.RIGHT_TANK);
        }
    }
}
