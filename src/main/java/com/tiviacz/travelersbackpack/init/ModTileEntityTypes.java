package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.tileentity.TravelersBackpackTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityTypes {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, TravelersBackpack.MODID);

    public static final RegistryObject<TileEntityType<TravelersBackpackTileEntity>> TRAVELERS_BACKPACK = TILE_ENTITY_TYPES.register("travelers_backpack",
            () -> TileEntityType.Builder.of(TravelersBackpackTileEntity::new,
                    ModBlocks.STANDARD_TRAVELERS_BACKPACK.get(),
                    ModBlocks.NETHERITE_TRAVELERS_BACKPACK.get(),
                    ModBlocks.DIAMOND_TRAVELERS_BACKPACK.get(),
                    ModBlocks.GOLD_TRAVELERS_BACKPACK.get(),
                    ModBlocks.EMERALD_TRAVELERS_BACKPACK.get(),
                    ModBlocks.IRON_TRAVELERS_BACKPACK.get(),
                    ModBlocks.LAPIS_TRAVELERS_BACKPACK.get(),
                    ModBlocks.REDSTONE_TRAVELERS_BACKPACK.get(),
                    ModBlocks.COAL_TRAVELERS_BACKPACK.get(),

                    ModBlocks.QUARTZ_TRAVELERS_BACKPACK.get(),
                    ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK.get(),
                    ModBlocks.END_TRAVELERS_BACKPACK.get(),
                    ModBlocks.NETHER_TRAVELERS_BACKPACK.get(),
                    ModBlocks.SANDSTONE_TRAVELERS_BACKPACK.get(),
                    ModBlocks.SNOW_TRAVELERS_BACKPACK.get(),
                    ModBlocks.SPONGE_TRAVELERS_BACKPACK.get(),

                    ModBlocks.CAKE_TRAVELERS_BACKPACK.get(),

                    ModBlocks.CACTUS_TRAVELERS_BACKPACK.get(),
                    ModBlocks.HAY_TRAVELERS_BACKPACK.get(),
                    ModBlocks.MELON_TRAVELERS_BACKPACK.get(),
                    ModBlocks.PUMPKIN_TRAVELERS_BACKPACK.get(),

                    ModBlocks.CREEPER_TRAVELERS_BACKPACK.get(),
                    ModBlocks.DRAGON_TRAVELERS_BACKPACK.get(),
                    ModBlocks.ENDERMAN_TRAVELERS_BACKPACK.get(),
                    ModBlocks.BLAZE_TRAVELERS_BACKPACK.get(),
                    ModBlocks.GHAST_TRAVELERS_BACKPACK.get(),
                    ModBlocks.MAGMA_CUBE_TRAVELERS_BACKPACK.get(),
                    ModBlocks.SKELETON_TRAVELERS_BACKPACK.get(),
                    ModBlocks.SPIDER_TRAVELERS_BACKPACK.get(),
                    ModBlocks.WITHER_TRAVELERS_BACKPACK.get(),

                    ModBlocks.BAT_TRAVELERS_BACKPACK.get(),
                    ModBlocks.BEE_TRAVELERS_BACKPACK.get(),
                    ModBlocks.WOLF_TRAVELERS_BACKPACK.get(),
                    ModBlocks.FOX_TRAVELERS_BACKPACK.get(),
                    ModBlocks.OCELOT_TRAVELERS_BACKPACK.get(),
                    ModBlocks.HORSE_TRAVELERS_BACKPACK.get(),
                    ModBlocks.COW_TRAVELERS_BACKPACK.get(),
                    ModBlocks.PIG_TRAVELERS_BACKPACK.get(),
                    ModBlocks.SHEEP_TRAVELERS_BACKPACK.get(),
                    ModBlocks.CHICKEN_TRAVELERS_BACKPACK.get(),
                    ModBlocks.SQUID_TRAVELERS_BACKPACK.get(),
                    ModBlocks.VILLAGER_TRAVELERS_BACKPACK.get(),
                    ModBlocks.IRON_GOLEM_TRAVELERS_BACKPACK.get()

            ).build(null));
}