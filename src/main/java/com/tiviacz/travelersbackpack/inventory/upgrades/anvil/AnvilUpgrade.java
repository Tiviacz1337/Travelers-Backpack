package com.tiviacz.travelersbackpack.inventory.upgrades.anvil;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.UpgradeSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.item.upgrade.AnvilUpgradeItem;
import com.tiviacz.travelersbackpack.util.ContainerContentsHelper;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.LevelEvent;

import java.util.ArrayList;
import java.util.List;

public class AnvilUpgrade extends UpgradeBase<AnvilUpgrade> {
    public static final int INPUT_SLOT = 0;
    public static final int ADDITIONAL_SLOT = 1;
    public static final int MAX_NAME_LENGTH = AnvilMenu.MAX_NAME_LENGTH;
    public static final int TOO_EXPENSIVE_COST = 40;
    public static final float BREAK_CHANCE = 0.12F;

    private final ItemStackHandler anvilHandler;
    private final ResultContainer resultContainer = new ResultContainer();
    private final AnvilResultSlot resultSlot;
    private int cost;
    private int repairItemCountCost;
    private boolean onlyRenaming;
    private String itemName = "";
    //Fabric ItemStackHandler#onContentsChanged doesn't pass the previous stack, track the input to detect changes
    private ItemStack lastInputStack = ItemStack.EMPTY;

    public AnvilUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> contents) {
        super(manager, dataHolderSlot, new Point(108, 92));
        this.anvilHandler = createHandler(contents);
        this.lastInputStack = getStack(INPUT_SLOT).copy();
        this.resultSlot = new AnvilResultSlot(this, this.resultContainer, 0, 85, 42);
        createResult();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public WidgetBase<BackpackScreen> createWidget(BackpackScreen screen, int x, int y) {
        return new AnvilWidget(screen, this, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y));
    }

    @Override
    public List<Pair<Integer, Integer>> getUpgradeSlotsPosition(int x, int y) {
        List<Pair<Integer, Integer>> positions = new ArrayList<>();
        positions.add(Pair.of(x + 7, y + 42));
        positions.add(Pair.of(x + 46, y + 42));
        positions.add(Pair.of(x + 85, y + 42));
        return positions;
    }

    @Override
    public List<? extends Slot> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y) {
        List<Slot> slots = new ArrayList<>();
        slots.add(new UpgradeSlotItemHandler<>(this, this.anvilHandler, INPUT_SLOT, x + 7, y + 42));
        slots.add(new UpgradeSlotItemHandler<>(this, this.anvilHandler, ADDITIONAL_SLOT, x + 46, y + 42));
        this.resultSlot.x = x + 85;
        this.resultSlot.y = y + 42;
        slots.add(this.resultSlot);
        return slots;
    }

    private ItemStackHandler createHandler(NonNullList<ItemStack> contents) {
        return new ItemStackHandler(contents) {
            @Override
            protected void onContentsChanged(int slot) {
                ItemStack current = getStackInSlot(slot);
                if(slot == INPUT_SLOT) {
                    if(!ItemStack.matches(lastInputStack, current)) {
                        itemName = "";
                    }
                    lastInputStack = current.copy();
                }
                updateDataHolderUnchecked(dataHolderStack -> dataHolderStack.update(ModDataComponents.BACKPACK_CONTAINER, ItemContainerContents.EMPTY, currentContents -> ContainerContentsHelper.updateStack(currentContents, 2, current, slot)));
                createResult();
            }
        };
    }

    private ItemStack getStack(int slot) {
        return this.anvilHandler.getStackInSlot(slot);
    }

    private void setStack(int slot, ItemStack stack) {
        this.anvilHandler.setStackInSlot(slot, stack);
    }

    public void createResult() {
        Player player = getPlayer();
        boolean infiniteMaterials = player != null && player.hasInfiniteMaterials();

        ItemStack input = getStack(INPUT_SLOT);
        this.onlyRenaming = false;
        this.cost = 1;
        int price = 0;
        long tax = 0L;
        int namingCost = 0;

        if(input.isEmpty() || !EnchantmentHelper.canStoreEnchantments(input)) {
            this.resultContainer.setItem(0, ItemStack.EMPTY);
            this.cost = 0;
            return;
        }

        ItemStack result = input.copy();
        ItemStack addition = getStack(ADDITIONAL_SLOT);
        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(result));
        tax += (long)input.getOrDefault(DataComponents.REPAIR_COST, 0) + addition.getOrDefault(DataComponents.REPAIR_COST, 0);
        this.repairItemCountCost = 0;

        if(!addition.isEmpty()) {
            boolean usingBook = addition.has(DataComponents.STORED_ENCHANTMENTS);
            if(result.isDamageableItem() && input.isValidRepairItem(addition)) {
                int repairAmount = Math.min(result.getDamageValue(), result.getMaxDamage() / 4);
                if(repairAmount <= 0) {
                    fail();
                    return;
                }

                int count;
                for(count = 0; repairAmount > 0 && count < addition.getCount(); count++) {
                    result.setDamageValue(result.getDamageValue() - repairAmount);
                    price++;
                    repairAmount = Math.min(result.getDamageValue(), result.getMaxDamage() / 4);
                }

                this.repairItemCountCost = count;
            } else {
                if(!usingBook && (!result.is(addition.getItem()) || !result.isDamageableItem())) {
                    fail();
                    return;
                }

                if(result.isDamageableItem() && !usingBook) {
                    int remaining1 = input.getMaxDamage() - input.getDamageValue();
                    int remaining2 = addition.getMaxDamage() - addition.getDamageValue();
                    int additional = remaining2 + result.getMaxDamage() * 12 / 100;
                    int remaining = remaining1 + additional;
                    int resultDamage = Math.max(0, result.getMaxDamage() - remaining);
                    if(resultDamage < result.getDamageValue()) {
                        result.setDamageValue(resultDamage);
                        price += 2;
                    }
                }

                ItemEnchantments additionalEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(addition);
                boolean isAnyEnchantmentCompatible = false;
                boolean isAnyEnchantmentNotCompatible = false;

                for(Object2IntMap.Entry<Holder<Enchantment>> entry : additionalEnchantments.entrySet()) {
                    Holder<Enchantment> enchantmentHolder = entry.getKey();
                    int current = enchantments.getLevel(enchantmentHolder);
                    int level = entry.getIntValue();
                    level = current == level ? level + 1 : Math.max(level, current);
                    Enchantment enchantment = enchantmentHolder.value();
                    //Same as vanilla AnvilMenu with Fabric API (FabricItem#canBeEnchantedWith), creative players can combine anything
                    boolean compatible = input.canBeEnchantedWith(enchantmentHolder, EnchantingContext.ACCEPTABLE) || infiniteMaterials || input.is(Items.ENCHANTED_BOOK);

                    for(Holder<Enchantment> other : enchantments.keySet()) {
                        if(!other.equals(enchantmentHolder) && !Enchantment.areCompatible(enchantmentHolder, other)) {
                            compatible = false;
                            price++;
                        }
                    }

                    if(!compatible) {
                        isAnyEnchantmentNotCompatible = true;
                    } else {
                        isAnyEnchantmentCompatible = true;
                        level = Math.min(level, enchantment.getMaxLevel());
                        enchantments.set(enchantmentHolder, level);
                        int fee = enchantment.getAnvilCost();
                        if(usingBook) {
                            fee = Math.max(1, fee / 2);
                        }

                        price += fee * level;
                        if(input.getCount() > 1) {
                            price = TOO_EXPENSIVE_COST;
                        }
                    }
                }

                if(isAnyEnchantmentNotCompatible && !isAnyEnchantmentCompatible) {
                    fail();
                    return;
                }
            }
        }

        if(!StringUtil.isBlank(this.itemName)) {
            if(!this.itemName.equals(input.getHoverName().getString())) {
                namingCost = 1;
                price += namingCost;
                result.set(DataComponents.CUSTOM_NAME, Component.literal(this.itemName));
            }
        } else if(input.has(DataComponents.CUSTOM_NAME)) {
            namingCost = 1;
            price += namingCost;
            result.remove(DataComponents.CUSTOM_NAME);
        }

        this.cost = price <= 0 ? 0 : (int)Mth.clamp(tax + price, 0L, Integer.MAX_VALUE);
        if(price <= 0) {
            result = ItemStack.EMPTY;
        }

        if(namingCost == price && namingCost > 0) {
            if(this.cost >= TOO_EXPENSIVE_COST) {
                this.cost = TOO_EXPENSIVE_COST - 1;
            }
            this.onlyRenaming = true;
        }

        if(this.cost >= TOO_EXPENSIVE_COST && !infiniteMaterials) {
            result = ItemStack.EMPTY;
        }

        if(!result.isEmpty()) {
            int baseCost = Math.max(result.getOrDefault(DataComponents.REPAIR_COST, 0), addition.getOrDefault(DataComponents.REPAIR_COST, 0));
            if(namingCost != price || namingCost == 0) {
                baseCost = AnvilMenu.calculateIncreasedRepairCost(baseCost);
            }

            result.set(DataComponents.REPAIR_COST, baseCost);
            EnchantmentHelper.setEnchantments(result, enchantments.toImmutable());
        }

        this.resultContainer.setItem(0, result);
    }

    private void fail() {
        this.resultContainer.setItem(0, ItemStack.EMPTY);
        this.cost = 0;
    }

    public boolean mayPickup(Player player) {
        return player != null && (player.hasInfiniteMaterials() || player.experienceLevel >= this.cost) && this.cost > 0;
    }

    public void onTake(Player player, ItemStack carried) {
        int levelCost = this.cost;
        int repairCount = this.repairItemCountCost;
        boolean renaming = this.onlyRenaming;

        if(!player.hasInfiniteMaterials()) {
            player.giveExperienceLevels(-levelCost);
        }

        if(repairCount > 0) {
            ItemStack addition = getStack(ADDITIONAL_SLOT);
            if(!addition.isEmpty() && addition.getCount() > repairCount) {
                addition.shrink(repairCount);
                setStack(ADDITIONAL_SLOT, addition);
            } else {
                setStack(ADDITIONAL_SLOT, ItemStack.EMPTY);
            }
        } else if(!renaming) {
            setStack(ADDITIONAL_SLOT, ItemStack.EMPTY);
        }

        this.cost = 0;
        setStack(INPUT_SLOT, ItemStack.EMPTY);

        if(player instanceof ServerPlayer serverPlayer) {
            boolean destroyed = !player.hasInfiniteMaterials() && damageAnvil(serverPlayer.getRandom());
            serverPlayer.level().levelEvent(destroyed ? LevelEvent.SOUND_ANVIL_BROKEN : LevelEvent.SOUND_ANVIL_USED, serverPlayer.blockPosition(), 0);
            if(destroyed) {
                BackpackWrapper wrapper = getWrapper();
                int slot = getDataHolderSlot();
                serverPlayer.level().getServer().execute(() -> destroyUpgrade(wrapper, slot, serverPlayer));
            }
        }
    }

    private boolean damageAnvil(RandomSource random) {
        ItemStack dataHolder = getDataHolderStack();
        if(!(dataHolder.getItem() instanceof AnvilUpgradeItem anvilItem) || !anvilItem.isBreakable() || random.nextFloat() >= BREAK_CHANCE) {
            return false;
        }
        int newDamage = AnvilUpgradeItem.getAnvilDamage(dataHolder) + 1;
        if(newDamage >= AnvilUpgradeItem.DURABILITY) {
            return true;
        }
        updateDataHolderUnchecked(ModDataComponents.ANVIL_DAMAGE, newDamage);
        return false;
    }

    private static void destroyUpgrade(BackpackWrapper wrapper, int slot, ServerPlayer player) {
        ItemStack upgradeStack = wrapper.getUpgrades().getStackInSlot(slot);
        if(!(upgradeStack.getItem() instanceof AnvilUpgradeItem)) {
            return;
        }
        NonNullList<ItemStack> leftovers = ContainerContentsHelper.getItems(upgradeStack.getOrDefault(ModDataComponents.BACKPACK_CONTAINER, ItemContainerContents.EMPTY), 2);
        wrapper.getUpgrades().setStackInSlot(slot, ItemStack.EMPTY);
        for(ItemStack leftover : leftovers) {
            if(!leftover.isEmpty()) {
                player.getInventory().placeItemBackInInventory(leftover);
            }
        }
        player.getInventory().placeItemBackInInventory(new ItemStack(ModItems.BLANK_UPGRADE));
        wrapper.saveHandler.run();
    }

    private Player getPlayer() {
        return getWrapper().getPlayersUsing().stream().findFirst().orElse(null);
    }

    public boolean setItemName(String name) {
        String filtered = StringUtil.filterText(name);
        if(filtered.length() > MAX_NAME_LENGTH || filtered.equals(this.itemName)) {
            return false;
        }
        this.itemName = filtered;
        createResult();
        return true;
    }

    public String getItemName() {
        return this.itemName;
    }

    public ItemStack getInputStack() {
        return getStack(INPUT_SLOT);
    }

    public ItemStack getAdditionStack() {
        return getStack(ADDITIONAL_SLOT);
    }

    public int getCost() {
        return this.cost;
    }

    public ItemStack getResult() {
        return this.resultContainer.getItem(0);
    }

    public static void setName(ServerPlayer player, int slot, String name) {
        if(player.containerMenu instanceof BackpackBaseMenu menu) {
            menu.getWrapper().getUpgradeManager().getUpgrade(AnvilUpgrade.class)
                    .filter(upgrade -> upgrade.getDataHolderSlot() == slot)
                    .ifPresent(upgrade -> upgrade.setItemName(name));
        }
    }
}