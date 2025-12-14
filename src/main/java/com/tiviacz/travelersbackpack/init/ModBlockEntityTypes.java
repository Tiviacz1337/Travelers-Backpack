package com.tiviacz.travelersbackpack.init;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.FluidTank;
import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import com.tiviacz.travelersbackpack.inventory.handler.StorageAccessWrapper;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.util.ItemStackUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.impl.transfer.item.InventoryStorageImpl;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Iterator;

public class ModBlockEntityTypes {
    public static BlockEntityType<BackpackBlockEntity> BACKPACK;

    public static void init() {
        BACKPACK = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, TravelersBackpack.MODID + ":travelers_backpack", FabricBlockEntityTypeBuilder.create(BackpackBlockEntity::new, ModBlocks.STANDARD_TRAVELERS_BACKPACK,
                ModBlocks.NETHERITE_TRAVELERS_BACKPACK,
                ModBlocks.DIAMOND_TRAVELERS_BACKPACK,
                ModBlocks.GOLD_TRAVELERS_BACKPACK,
                ModBlocks.EMERALD_TRAVELERS_BACKPACK,
                ModBlocks.IRON_TRAVELERS_BACKPACK,
                ModBlocks.LAPIS_TRAVELERS_BACKPACK,
                ModBlocks.REDSTONE_TRAVELERS_BACKPACK,
                ModBlocks.COAL_TRAVELERS_BACKPACK,

                ModBlocks.QUARTZ_TRAVELERS_BACKPACK,
                ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK,
                ModBlocks.END_TRAVELERS_BACKPACK,
                ModBlocks.NETHER_TRAVELERS_BACKPACK,
                ModBlocks.SANDSTONE_TRAVELERS_BACKPACK,
                ModBlocks.SNOW_TRAVELERS_BACKPACK,
                ModBlocks.SPONGE_TRAVELERS_BACKPACK,

                ModBlocks.CAKE_TRAVELERS_BACKPACK,

                ModBlocks.CACTUS_TRAVELERS_BACKPACK,
                ModBlocks.HAY_TRAVELERS_BACKPACK,
                ModBlocks.MELON_TRAVELERS_BACKPACK,
                ModBlocks.PUMPKIN_TRAVELERS_BACKPACK,

                ModBlocks.CREEPER_TRAVELERS_BACKPACK,
                ModBlocks.DRAGON_TRAVELERS_BACKPACK,
                ModBlocks.ENDERMAN_TRAVELERS_BACKPACK,
                ModBlocks.BLAZE_TRAVELERS_BACKPACK,
                ModBlocks.GHAST_TRAVELERS_BACKPACK,
                ModBlocks.MAGMA_CUBE_TRAVELERS_BACKPACK,
                ModBlocks.SKELETON_TRAVELERS_BACKPACK,
                ModBlocks.SPIDER_TRAVELERS_BACKPACK,
                ModBlocks.WITHER_TRAVELERS_BACKPACK,
                ModBlocks.WARDEN_TRAVELERS_BACKPACK,

                ModBlocks.BAT_TRAVELERS_BACKPACK,
                ModBlocks.BEE_TRAVELERS_BACKPACK,
                ModBlocks.WOLF_TRAVELERS_BACKPACK,
                ModBlocks.FOX_TRAVELERS_BACKPACK,
                ModBlocks.OCELOT_TRAVELERS_BACKPACK,
                ModBlocks.HORSE_TRAVELERS_BACKPACK,
                ModBlocks.COW_TRAVELERS_BACKPACK,
                ModBlocks.PIG_TRAVELERS_BACKPACK,
                ModBlocks.SHEEP_TRAVELERS_BACKPACK,
                ModBlocks.CHICKEN_TRAVELERS_BACKPACK,
                ModBlocks.SQUID_TRAVELERS_BACKPACK,
                ModBlocks.VILLAGER_TRAVELERS_BACKPACK,
                ModBlocks.IRON_GOLEM_TRAVELERS_BACKPACK).build(null));
    }

    public static void initSidedStorage() {
        FluidStorage.SIDED.registerForBlockEntity(ModBlockEntityTypes::getProperTank, BACKPACK);
        ItemStorage.SIDED.registerForBlockEntity((ModBlockEntityTypes::getProperInventory), BACKPACK);
    }

    public static SingleVariantStorage<FluidVariant> getProperTank(BackpackBlockEntity blockEntity, Direction clickedDirection) {
        Direction direction = blockEntity.getBlockDirection();
        if(blockEntity.getWrapper() != BackpackWrapper.DUMMY && blockEntity.getWrapper().getUpgradeManager().getUpgrade(TanksUpgrade.class).isPresent()) {
            TanksUpgrade tanksUpgrade = blockEntity.getWrapper().getUpgradeManager().getUpgrade(TanksUpgrade.class).get();
            if(clickedDirection == null) return tanksUpgrade.getLeftTank();

            if(direction == Direction.NORTH) {
                switch(clickedDirection) {
                    case WEST:
                        return tanksUpgrade.getRightTank();
                    case EAST:
                        return tanksUpgrade.getLeftTank();
                }
            }
            if(direction == Direction.SOUTH) {
                switch(clickedDirection) {
                    case EAST:
                        return tanksUpgrade.getRightTank();
                    case WEST:
                        return tanksUpgrade.getLeftTank();
                }
            }

            if(direction == Direction.EAST) {
                switch(clickedDirection) {
                    case NORTH:
                        return tanksUpgrade.getRightTank();
                    case SOUTH:
                        return tanksUpgrade.getLeftTank();
                }
            }

            if(direction == Direction.WEST) {
                switch(clickedDirection) {
                    case SOUTH:
                        return tanksUpgrade.getRightTank();
                    case NORTH:
                        return tanksUpgrade.getLeftTank();
                }
            }
            return tanksUpgrade.getLeftTank();
        }
        return new FluidTank(0);
    }

    public static Storage<ItemVariant> getProperInventory(BackpackBlockEntity blockEntity, Direction clickedDirection) {
        if(blockEntity.getWrapper() != BackpackWrapper.DUMMY) {
            return new BackpackStorage(blockEntity.getWrapper().getStorageForInputOutput());
        }
        return InventoryStorageImpl.of(new ItemStackHandler(0), null);
    }

    public static class BackpackStorage implements Storage<ItemVariant> {
        private final SlottedStorage<ItemVariant> storage;
        private final StorageAccessWrapper backingStorage;

        public BackpackStorage(StorageAccessWrapper backingStorage) {
            this.storage = InventoryStorage.of(backingStorage, null);
            this.backingStorage = backingStorage;
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            ItemStack stack = resource.toStack();

            if(backingStorage.tryVoiding(stack)) {
                return maxAmount; //Accept, but void
            }
            long totalInserted = 0;
            long remaining = maxAmount;

            for(Pair<Integer, Pair<ItemStack, Boolean>> memorizedStack : backingStorage.wrapper.getMemorySlots()) {
                if(memorizedStack.getSecond().getFirst().getItem() != stack.getItem()) {
                    continue;
                }
                int result = matchesStack(stack, memorizedStack);

                if(result == -1) {
                    continue;
                }
                SingleSlotStorage<ItemVariant> slot = storage.getSlot(result);
                long inserted = slot.insert(resource, remaining, transaction);
                if(inserted > 0) {
                    totalInserted += inserted;
                    remaining -= inserted;
                    if(remaining <= 0) {
                        return totalInserted;
                    }
                }
            }

            for(int i = 0; i < storage.getSlotCount() && remaining > 0; i++) {
                if(backingStorage.wrapper.getUnsortableSlots().contains(i)) {
                    continue;
                }
                SingleSlotStorage<ItemVariant> slot = storage.getSlot(i);
                long inserted = slot.insert(resource, remaining, transaction);
                if(inserted > 0) {
                    totalInserted += inserted;
                    remaining -= inserted;
                }
            }
            return totalInserted;
        }

        public int matchesStack(ItemStack inserted, Pair<Integer, Pair<ItemStack, Boolean>> memorizedStack) {
            if(memorizedStack.getSecond().getSecond()) {
                return ItemStackUtils.isSameItemSameTags(inserted, memorizedStack.getSecond().getFirst()) ? memorizedStack.getFirst() : -1;
            } else {
                return ItemStack.isSameItem(inserted, memorizedStack.getSecond().getFirst()) ? memorizedStack.getFirst() : -1;
            }
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            long totalExtracted = 0;
            long remaining = maxAmount;

            for(int i = 0; i < storage.getSlotCount() && remaining > 0; i++) {
                if(backingStorage.wrapper.getUnsortableSlots().contains(i)) {
                    continue;
                }
                SingleSlotStorage<ItemVariant> slot = storage.getSlot(i);
                if(!slot.isResourceBlank() && slot.getResource().equals(resource)) {
                    long extracted = slot.extract(resource, remaining, transaction);
                    if(extracted > 0) {
                        totalExtracted += extracted;
                        remaining -= extracted;
                    }
                }
            }
            return totalExtracted;
        }

        @Override
        public Iterator<StorageView<ItemVariant>> iterator() {
            Iterator<StorageView<ItemVariant>> baseIt = storage.iterator();

            return new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return baseIt.hasNext();
                }

                @Override
                public StorageView<ItemVariant> next() {
                    StorageView<ItemVariant> original = baseIt.next();
                    return new StorageView<ItemVariant>() {
                        @Override
                        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
                            int slotIndex = getSlotIndex(original);
                            if(backingStorage.wrapper.getUnsortableSlots().contains(slotIndex)) {
                                return 0;
                            }
                            return original.extract(resource, maxAmount, transaction);
                        }

                        @Override
                        public ItemVariant getResource() {
                            return original.getResource();
                        }

                        @Override
                        public long getAmount() {
                            return original.getAmount();
                        }

                        @Override
                        public long getCapacity() {
                            return original.getCapacity();
                        }

                        @Override
                        public boolean isResourceBlank() {
                            return original.isResourceBlank();
                        }

                        @Override
                        public StorageView<ItemVariant> getUnderlyingView() {
                            return original;
                        }
                    };
                }

                private int getSlotIndex(StorageView<ItemVariant> view) {
                    for(int i = 0; i < storage.getSlotCount(); i++) {
                        if(storage.getSlot(i) == view) {
                            return i;
                        }
                    }
                    return -1;
                }
            };
        }
    }
}