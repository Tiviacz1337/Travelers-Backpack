package com.tiviacz.travelersbackpack.inventory.upgrades.anvil;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.menu.slot.UpgradeSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.item.upgrade.AnvilUpgradeItem;
import com.tiviacz.travelersbackpack.util.ContainerContentsHelper;
import com.tiviacz.travelersbackpack.util.StacksHandlerUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.LevelEvent;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.ArrayList;
import java.util.List;

public class AnvilUpgrade extends UpgradeBase<AnvilUpgrade> {
    protected static final int SLOT_INPUT = 0;
    protected static final int SLOT_ADDITION = 1;
    public static final int TOO_EXPENSIVE_COST = 40;
    public static final float BREAK_CHANCE = 0.12F;
    private final ItemStacksResourceHandler items;
    private final ResultContainer resultSlots = new ResultContainer();
    private final AnvilResultSlot resultSlot;
    private String itemName = "";
    private int cost;
    private int repairItemCountCost;
    private boolean onlyRenaming;

    public AnvilUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> anvilContents) {
        super(manager, dataHolderSlot, new Point(108, 92));
        this.items = createHandler(anvilContents);
        this.resultSlot = new AnvilResultSlot(this, this.resultSlots, 0, 85, 42);
        createResult();
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
    public List<Slot> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y) {
        List<Slot> slots = new ArrayList<>();
        slots.add(new UpgradeSlotItemHandler<>(this, this.items, SLOT_INPUT, x + 7, y + 42));
        slots.add(new UpgradeSlotItemHandler<>(this, this.items, SLOT_ADDITION, x + 46, y + 42));
        this.resultSlot.x = x + 85;
        this.resultSlot.y = y + 42;
        slots.add(this.resultSlot);
        return slots;
    }

    public void setSlotChanged(ItemStack dataHolderStack, int index, ItemStack stack) {
        dataHolderStack.update(ModDataComponents.BACKPACK_CONTAINER, ItemContainerContents.EMPTY, currentContents -> ContainerContentsHelper.updateStack(currentContents, 2, stack, index));
    }

    private ItemStacksResourceHandler createHandler(NonNullList<ItemStack> stacks) {
        return new ItemStacksResourceHandler(stacks) {
            @Override
            protected void onContentsChanged(int slot, ItemStack previousStack) {
                ItemStack stack = StacksHandlerUtils.getStackInSlot(this, slot);
                if(slot == SLOT_INPUT && !ItemStack.matches(previousStack, stack)) {
                    itemName = "";
                }
                updateDataHolderUnchecked(dataHolderStack -> setSlotChanged(dataHolderStack, slot, stack));
                createResult();
            }

            @Override
            public boolean isValid(int slot, ItemResource stack) {
                return BackpackSlotItemHandler.isItemValid(stack.toStack());
            }
        };
    }

    public ItemStack getStack(int slot) {
        return StacksHandlerUtils.getStackInSlot(this.items, slot);
    }

    public void setStack(int slot, ItemStack stack) {
        StacksHandlerUtils.setStackInSlot(this.items, slot, stack);
    }

    public void createResult() {
        Player player = getPlayer();
        boolean infiniteMaterials = player != null && player.hasInfiniteMaterials();
        boolean instabuild = player != null && player.getAbilities().instabuild;
        ItemStack input = getStack(SLOT_INPUT);
        this.onlyRenaming = false;
        this.cost = 1;
        int price = 0;
        long repairCost = 0L;
        int namingCost = 0;

        if(input.isEmpty() || !EnchantmentHelper.canStoreEnchantments(input)) {
            resetResult();
            return;
        }

        ItemStack result = input.copy();
        ItemStack addition = getStack(SLOT_ADDITION);
        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(result));
        repairCost += (long)input.getOrDefault(DataComponents.REPAIR_COST, 0) + addition.getOrDefault(DataComponents.REPAIR_COST, 0);
        this.repairItemCountCost = 0;

        if(!addition.isEmpty()) {
            boolean isBook = addition.has(DataComponents.STORED_ENCHANTMENTS);

            if(result.isDamageableItem() && input.isValidRepairItem(addition)) {
                int repairAmount = Math.min(result.getDamageValue(), result.getMaxDamage() / 4);
                if(repairAmount <= 0) {
                    resetResult();
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
                if(!isBook && (!result.is(addition.getItem()) || !result.isDamageableItem())) {
                    resetResult();
                    return;
                }

                if(result.isDamageableItem() && !isBook) {
                    int inputDurability = input.getMaxDamage() - input.getDamageValue();
                    int additionDurability = addition.getMaxDamage() - addition.getDamageValue() + result.getMaxDamage() * 12 / 100;
                    int damage = Math.max(0, result.getMaxDamage() - (inputDurability + additionDurability));
                    if(damage < result.getDamageValue()) {
                        result.setDamageValue(damage);
                        price += 2;
                    }
                }

                ItemEnchantments additionEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(addition);
                boolean anyCompatible = false;
                boolean anyIncompatible = false;

                for(Object2IntMap.Entry<Holder<Enchantment>> entry : additionEnchantments.entrySet()) {
                    Holder<Enchantment> enchantment = entry.getKey();
                    int currentLevel = enchantments.getLevel(enchantment);
                    int level = entry.getIntValue();
                    level = currentLevel == level ? level + 1 : Math.max(level, currentLevel);
                    boolean canApply = input.supportsEnchantment(enchantment) || instabuild;

                    for(Holder<Enchantment> other : enchantments.keySet()) {
                        if(!other.equals(enchantment) && !Enchantment.areCompatible(enchantment, other)) {
                            canApply = false;
                            price++;
                        }
                    }

                    if(!canApply) {
                        anyIncompatible = true;
                        continue;
                    }

                    anyCompatible = true;
                    level = Math.min(level, enchantment.value().getMaxLevel());
                    enchantments.set(enchantment, level);
                    int fee = enchantment.value().getAnvilCost();
                    if(isBook) {
                        fee = Math.max(1, fee / 2);
                    }
                    price += fee * level;
                    if(input.getCount() > 1) {
                        price = TOO_EXPENSIVE_COST;
                    }
                }

                if(anyIncompatible && !anyCompatible) {
                    resetResult();
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

        this.cost = price <= 0 ? 0 : (int)Mth.clamp(repairCost + price, 0L, Integer.MAX_VALUE);
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
            int baseRepairCost = Math.max(result.getOrDefault(DataComponents.REPAIR_COST, 0), addition.getOrDefault(DataComponents.REPAIR_COST, 0));
            if(namingCost != price || namingCost == 0) {
                baseRepairCost = AnvilMenu.calculateIncreasedRepairCost(baseRepairCost);
            }
            result.set(DataComponents.REPAIR_COST, baseRepairCost);
            EnchantmentHelper.setEnchantments(result, enchantments.toImmutable());
        }

        this.resultSlots.setItem(0, result);
    }

    public void resetResult() {
        this.resultSlots.setItem(0, ItemStack.EMPTY);
        this.cost = 0;
    }

    public boolean mayPickup(Player player) {
        return player != null && (player.hasInfiniteMaterials() || player.experienceLevel >= this.cost) && this.cost > 0;
    }

    public void onTake(Player player, ItemStack stack) {
        int repairCount = this.repairItemCountCost;
        boolean onlyRenaming = this.onlyRenaming;

        if(!player.hasInfiniteMaterials()) {
            player.giveExperienceLevels(-this.cost);
        }

        if(repairCount > 0) {
            ItemStack addition = getStack(SLOT_ADDITION);
            if(!addition.isEmpty() && addition.getCount() > repairCount) {
                addition.shrink(repairCount);
                setStack(SLOT_ADDITION, addition);
            } else {
                setStack(SLOT_ADDITION, ItemStack.EMPTY);
            }
        } else if(!onlyRenaming) {
            setStack(SLOT_ADDITION, ItemStack.EMPTY);
        }

        this.cost = 0;
        setStack(SLOT_INPUT, ItemStack.EMPTY);

        if(player instanceof ServerPlayer serverPlayer) {
            boolean destroyed = !player.hasInfiniteMaterials() && damageAnvil(serverPlayer);
            serverPlayer.level().levelEvent(destroyed ? LevelEvent.SOUND_ANVIL_BROKEN : LevelEvent.SOUND_ANVIL_USED, serverPlayer.blockPosition(), 0);

            if(destroyed) {
                BackpackWrapper wrapper = getWrapper();
                int slot = getDataHolderSlot();
                serverPlayer.level().getServer().execute(() -> destroyUpgrade(wrapper, slot, serverPlayer));
            }
        }
    }

    public boolean damageAnvil(ServerPlayer player) {
        ItemStack dataHolderStack = getDataHolderStack();
        if(player.getRandom().nextFloat() >= BREAK_CHANCE) {
            return false;
        }

        int damage = AnvilUpgradeItem.getAnvilDamage(dataHolderStack) + 1;
        if(damage >= AnvilUpgradeItem.DURABILITY) {
            return true;
        }
        updateDataHolderUnchecked(ModDataComponents.ANVIL_DAMAGE.get(), damage);
        return false;
    }

    public static void destroyUpgrade(BackpackWrapper wrapper, int slot, ServerPlayer player) {
        ItemStack upgradeStack = StacksHandlerUtils.getStackInSlot(wrapper.getUpgrades(), slot);
        if(!(upgradeStack.getItem() instanceof AnvilUpgradeItem)) {
            return;
        }

        NonNullList<ItemStack> contents = ContainerContentsHelper.getItems(upgradeStack.getOrDefault(ModDataComponents.BACKPACK_CONTAINER, ItemContainerContents.EMPTY), 2);
        StacksHandlerUtils.setStackInSlot(wrapper.getUpgrades(), slot, ItemStack.EMPTY);

        for(ItemStack stack : contents) {
            if(!stack.isEmpty()) {
                player.getInventory().placeItemBackInInventory(stack);
            }
        }
        player.getInventory().placeItemBackInInventory(new ItemStack(ModItems.BLANK_UPGRADE.get()));
        wrapper.saveHandler.run();
    }

    public Player getPlayer() {
        return getWrapper().getPlayersUsing().isEmpty() ? null : getWrapper().getPlayersUsing().getFirst();
    }

    public boolean setItemName(String name) {
        String filteredName = StringUtil.filterText(name);
        if(filteredName.length() > AnvilMenu.MAX_NAME_LENGTH || filteredName.equals(this.itemName)) {
            return false;
        }
        this.itemName = filteredName;
        createResult();
        return true;
    }

    public String getItemName() {
        return this.itemName;
    }

    public ItemStack getInputStack() {
        return getStack(SLOT_INPUT);
    }

    public ItemStack getAdditionStack() {
        return getStack(SLOT_ADDITION);
    }

    public int getCost() {
        return this.cost;
    }

    public ItemStack getResult() {
        return this.resultSlots.getItem(0);
    }

    public static void setName(ServerPlayer player, int slot, String name) {
        if(player.containerMenu instanceof BackpackBaseMenu menu) {
            menu.getWrapper().getUpgradeManager().getUpgrade(AnvilUpgrade.class).ifPresent(upgrade -> {
                if(upgrade.getDataHolderSlot() == slot) {
                    upgrade.setItemName(name);
                }
            });
        }
    }
}