package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TravelersBackpack.MODID);

    public static final RegistryObject<Block> STANDARD_TRAVELERS_BACKPACK = BLOCKS.register("standard", () -> new TravelersBackpackBlock(Block.Properties.create(new Material.Builder(MaterialColor.BROWN).build()).sound(SoundType.CLOTH)));
    //public static final RegistryObject<Block> BAT_TRAVELERS_BACKPACK = BLOCKS.register("bat", () -> new TravelersBackpackBlock(Block.Properties.create(new Material.Builder(MaterialColor.BROWN).build())));
    public static final RegistryObject<Block> SLEEPING_BAG = BLOCKS.register("sleeping_bag", () -> new SleepingBagBlock(Block.Properties.create(new Material.Builder(MaterialColor.RED).build()).sound(SoundType.CLOTH).hardnessAndResistance(0.2F).harvestLevel(0).notSolid()));
}