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
            () -> TileEntityType.Builder.create(TravelersBackpackTileEntity::new,
                    ModBlocks.STANDARD_TRAVELERS_BACKPACK.get()
                    //   ModBlocks.BAT_TRAVELERS_BACKPACK.get()

            ).build(null));
}