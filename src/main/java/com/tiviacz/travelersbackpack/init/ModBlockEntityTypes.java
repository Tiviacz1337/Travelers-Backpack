package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.FluidTank;
import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import com.tiviacz.travelersbackpack.inventory.handler.StorageAccessWrapper;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.fabricmc.fabric.impl.transfer.item.ContainerStorageImpl;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Iterator;
import java.util.stream.IntStream;

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
        return ContainerStorageImpl.of(new ItemStackHandler(0), null);
    }

    public static class BackpackStorage extends SnapshotParticipant<ItemStack[]> implements Storage<ItemVariant> {
        private final SlottedStorage<ItemVariant> storage;
        private final StorageAccessWrapper backingStorage;

        public BackpackStorage(StorageAccessWrapper backingStorage) {
            this.storage = ContainerStorage.of(backingStorage, null);
            this.backingStorage = backingStorage;
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notBlankNotNegative(resource, maxAmount);
            updateSnapshots(transaction);
            long remainingToInsert = maxAmount;
            long totalInserted = 0;

            for(int i = 0; i < backingStorage.getSlots(); i++) {
                if(remainingToInsert <= 0) break;
                int attemptAmount = (int)Math.min(remainingToInsert, resource.getItem().getDefaultMaxStackSize());
                ItemStack insertStack = resource.toStack(attemptAmount);
                ItemStack remainder = backingStorage.insertItem(i, insertStack, false);
                int insertedInSlot = attemptAmount - remainder.getCount();
                if(insertedInSlot > 0) {
                    totalInserted += insertedInSlot;
                    remainingToInsert -= insertedInSlot;
                }
            }
            return totalInserted;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notBlankNotNegative(resource, maxAmount);
            updateSnapshots(transaction);
            long remainingToExtract = maxAmount;
            long totalExtracted = 0;

            for(int i = 0; i < backingStorage.getSlots(); i++) {
                if(remainingToExtract <= 0) break;
                int attemptAmount = (int)Math.min(remainingToExtract, Integer.MAX_VALUE);
                ItemStack simulatedStack = backingStorage.extractItem(i, attemptAmount, true);
                if(!simulatedStack.isEmpty() && resource.matches(simulatedStack)) {
                    int amountExtracted = simulatedStack.getCount();
                    backingStorage.extractItem(i, amountExtracted, false);
                    totalExtracted += amountExtracted;
                    remainingToExtract -= amountExtracted;
                }
            }
            return totalExtracted;
        }

        @Override
        public Iterator<StorageView<ItemVariant>> iterator() {
            return IntStream.range(0, storage.getSlotCount())
                    .mapToObj(i -> (StorageView<ItemVariant>)new BackpackStorageView(i))
                    .iterator();
        }

        @Override
        protected ItemStack[] createSnapshot() {
            ItemStack[] snapshot = new ItemStack[backingStorage.getSlots()];
            for(int i = 0; i < backingStorage.getSlots(); i++) {
                snapshot[i] = backingStorage.getStackInSlot(i).copy();
            }
            return snapshot;
        }

        @Override
        protected void readSnapshot(ItemStack[] snapshot) {
            for(int i = 0; i < snapshot.length; i++) {
                backingStorage.setStackInSlot(i, snapshot[i]);
            }
        }

        private class BackpackStorageView implements StorageView<ItemVariant> {
            private final int slotIndex;

            public BackpackStorageView(int slotIndex) {
                this.slotIndex = slotIndex;
            }

            @Override
            public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
                return BackpackStorage.this.extract(resource, maxAmount, transaction);
            }

            @Override
            public boolean isResourceBlank() {
                return getResource().isBlank();
            }

            @Override
            public ItemVariant getResource() {
                return storage.getSlot(slotIndex).getResource();
            }

            @Override
            public long getAmount() {
                return storage.getSlot(slotIndex).getAmount();
            }

            @Override
            public long getCapacity() {
                return storage.getSlot(slotIndex).getCapacity();
            }
        }
    }
}