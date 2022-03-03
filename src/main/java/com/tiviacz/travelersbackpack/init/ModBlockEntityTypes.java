package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.tileentity.TravelersBackpackBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, TravelersBackpack.MODID);

    public static final RegistryObject<BlockEntityType<TravelersBackpackBlockEntity>> TRAVELERS_BACKPACK = BLOCK_ENTITY_TYPES.register("travelers_backpack",
            () -> BlockEntityType.Builder.of(TravelersBackpackBlockEntity::new,
                    ModBlocks.STANDARD_TRAVELERS_BACKPACK.get(),
                    ModBlocks.NETHERITE_TRAVELERS_BACKPACK.get(),
                    ModBlocks.DIAMOND_TRAVELERS_BACKPACK.get(),
                    ModBlocks.GOLD_TRAVELERS_BACKPACK.get(),
                    ModBlocks.EMERALD_TRAVELERS_BACKPACK.get(),
                    ModBlocks.IRON_TRAVELERS_BACKPACK.get(),
                    ModBlocks.LAPIS_TRAVELERS_BACKPACK.get(),
                    ModBlocks.REDSTONE_TRAVELERS_BACKPACK.get(),
                    ModBlocks.COAL_TRAVELERS_BACKPACK.get(),
                    //ModBlocks.CRYING_OBSIDIAN_TRAVELERS_BACKPACK.get(),
                    ModBlocks.QUARTZ_TRAVELERS_BACKPACK.get(),
                    ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK.get(),

                    ModBlocks.HAY_TRAVELERS_BACKPACK.get(),
                    ModBlocks.MELON_TRAVELERS_BACKPACK.get(),
                    ModBlocks.PUMPKIN_TRAVELERS_BACKPACK.get(),

                    ModBlocks.BLAZE_TRAVELERS_BACKPACK.get(),

                    ModBlocks.BAT_TRAVELERS_BACKPACK.get(),
                    ModBlocks.WOLF_TRAVELERS_BACKPACK.get(),
                    ModBlocks.FOX_TRAVELERS_BACKPACK.get(),
                    ModBlocks.OCELOT_TRAVELERS_BACKPACK.get(),
                    ModBlocks.COW_TRAVELERS_BACKPACK.get(),
                    ModBlocks.PIG_TRAVELERS_BACKPACK.get(),
                    ModBlocks.CHICKEN_TRAVELERS_BACKPACK.get(),
                    ModBlocks.VILLAGER_TRAVELERS_BACKPACK.get()

            ).build(null));
}