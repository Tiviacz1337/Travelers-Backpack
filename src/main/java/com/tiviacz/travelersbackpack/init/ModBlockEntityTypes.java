package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.tileentity.TravelersBackpackBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
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

                ModBlocks.HAY_TRAVELERS_BACKPACK,
                ModBlocks.MELON_TRAVELERS_BACKPACK,
                ModBlocks.PUMPKIN_TRAVELERS_BACKPACK,

                ModBlocks.BLAZE_TRAVELERS_BACKPACK,

                ModBlocks.BAT_TRAVELERS_BACKPACK,
                ModBlocks.WOLF_TRAVELERS_BACKPACK,
                ModBlocks.FOX_TRAVELERS_BACKPACK,
                ModBlocks.OCELOT_TRAVELERS_BACKPACK,
                ModBlocks.COW_TRAVELERS_BACKPACK,
                ModBlocks.PIG_TRAVELERS_BACKPACK,
                ModBlocks.CHICKEN_TRAVELERS_BACKPACK,
                ModBlocks.VILLAGER_TRAVELERS_BACKPACK).build(null));
    }

    public static void initSidedFluidStorage()
    {
        FluidStorage.SIDED.registerForBlockEntity((ModBlockEntityTypes::getProperTankSide), TRAVELERS_BACKPACK_BLOCK_ENTITY_TYPE);
    }

    public static SingleVariantStorage<FluidVariant> getProperTankSide(TravelersBackpackBlockEntity entity, Direction clickedDirection)
    {
        Direction backpackDirection = entity.getBlockDirection(entity);

        switch(clickedDirection)
                {
                    case NORTH: return backpackDirection == Direction.WEST ? entity.leftTank : backpackDirection == Direction.EAST ? entity.rightTank : null;
                    case EAST: return backpackDirection == Direction.NORTH ? entity.leftTank : backpackDirection == Direction.SOUTH ? entity.rightTank : null;
                    case SOUTH: return backpackDirection == Direction.EAST ? entity.leftTank : backpackDirection == Direction.WEST ? entity.rightTank : null;
                    case WEST: return backpackDirection == Direction.SOUTH ? entity.leftTank : backpackDirection == Direction.NORTH ? entity.rightTank : null;
                    default: return null;
                }
    }
}
