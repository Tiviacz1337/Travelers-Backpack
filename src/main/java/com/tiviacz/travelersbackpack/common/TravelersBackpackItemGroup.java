package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;

public class TravelersBackpackItemGroup extends ItemGroup
{
    public static final ItemGroup instance = new TravelersBackpackItemGroup(ItemGroup.GROUPS.length, TravelersBackpack.MODID);

    private TravelersBackpackItemGroup(int index, String label)
    {
        super(index, label);
    }

    @Override
    public ItemStack createIcon()
    {
        return new ItemStack(ModItems.STANDARD_TRAVELERS_BACKPACK.get());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fill(NonNullList<ItemStack> items)
    {
        addItem(items, ModItems.SLEEPING_BAG.get());
        addItem(items, ModItems.BACKPACK_TANK.get());
        addItem(items, ModItems.HOSE_NOZZLE.get());
        addItem(items, ModItems.HOSE.get());

        addBlock(items, ModBlocks.STANDARD_TRAVELERS_BACKPACK);
    }

    public void addItem(NonNullList<ItemStack> items, Item item)
    {
        items.add(new ItemStack(item));
    }

    public void addBlock(NonNullList<ItemStack> items, RegistryObject<Block> block)
    {
        items.add(new ItemStack(block.get()));
    }
}
