package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.impl.transfer.item.InventoryStorageImpl;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class ModBlockEntityTypes
{
    public static BlockEntityType<TravelersBackpackBlockEntity> TRAVELERS_BACKPACK_BLOCK_ENTITY_TYPE;

    public static void init()
    {
        TRAVELERS_BACKPACK_BLOCK_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, TravelersBackpack.MODID + ":travelers_backpack", BlockEntityType.Builder.create(TravelersBackpackBlockEntity::new, ModBlocks.STANDARD_TRAVELERS_BACKPACK,
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

    public static void initSidedStorages()
    {
        FluidStorage.SIDED.registerForBlockEntity((ModBlockEntityTypes::getProperTankSide), TRAVELERS_BACKPACK_BLOCK_ENTITY_TYPE);
        ItemStorage.SIDED.registerForBlockEntity((ModBlockEntityTypes::getProperInventory), TRAVELERS_BACKPACK_BLOCK_ENTITY_TYPE);
    }

    public static SingleVariantStorage<FluidVariant> getProperTankSide(TravelersBackpackBlockEntity blockEntity, Direction clickedDirection)
    {
        Direction backpackDirection = blockEntity.getBlockDirection(blockEntity);

        switch(clickedDirection)
                {
                    case NORTH: return backpackDirection == Direction.WEST ? blockEntity.leftTank : backpackDirection == Direction.EAST ? blockEntity.rightTank : null;
                    case EAST: return backpackDirection == Direction.NORTH ? blockEntity.leftTank : backpackDirection == Direction.SOUTH ? blockEntity.rightTank : null;
                    case SOUTH: return backpackDirection == Direction.EAST ? blockEntity.leftTank : backpackDirection == Direction.WEST ? blockEntity.rightTank : null;
                    case WEST: return backpackDirection == Direction.SOUTH ? blockEntity.leftTank : backpackDirection == Direction.NORTH ? blockEntity.rightTank : null;
                    default: return null;
                }
    }

    public static Storage<ItemVariant> getProperInventory(TravelersBackpackBlockEntity blockEntity, Direction clickedDirection)
    {
        Direction backpackDirection = blockEntity.getBlockDirection(blockEntity);

        Storage<ItemVariant> mainInv = InventoryStorageImpl.of(blockEntity.inventory, null);
        Storage<ItemVariant> craftingInv = InventoryStorageImpl.of(blockEntity.craftingInventory, null);

        switch(clickedDirection)
        {
            case DOWN:
            case UP:
                return mainInv;
            case NORTH:
            case SOUTH:
            case WEST:
            case EAST:
                if(clickedDirection == backpackDirection || clickedDirection == backpackDirection.getOpposite()) return craftingInv;
            default: return null;
        }
    }
}