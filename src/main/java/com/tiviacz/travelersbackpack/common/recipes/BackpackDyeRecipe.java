package com.tiviacz.travelersbackpack.common.recipes;

import com.google.common.collect.Lists;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModRecipeSerializers;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

public class BackpackDyeRecipe extends SpecialCraftingRecipe
{
    public BackpackDyeRecipe(CraftingRecipeCategory category)
    {
        super(category);
    }

    @Override
    public boolean matches(RecipeInputInventory inv, World worldIn)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        List<ItemStack> list = Lists.newArrayList();

        for(int i = 0; i < inv.size(); ++i)
        {
            ItemStack itemstack1 = inv.getStack(i);
            if(!itemstack1.isEmpty())
            {
                if(itemstack1.getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK)
                {
                    if(!itemstack.isEmpty())
                    {
                        return false;
                    }

                    itemstack = itemstack1;
                }
                else
                {
                    if(!(itemstack1.getItem() instanceof DyeItem))
                    {
                        return false;
                    }

                    list.add(itemstack1);
                }
            }
        }

        return !itemstack.isEmpty() && !list.isEmpty();
    }

    @Override
    public ItemStack craft(final RecipeInputInventory inv, DynamicRegistryManager manager)
    {
        List<DyeItem> list = Lists.newArrayList();
        ItemStack stack = ItemStack.EMPTY;

        for(int i = 0; i < inv.size(); ++i)
        {
            ItemStack ingredient = inv.getStack(i);
            if(!ingredient.isEmpty())
            {
                if(ingredient.getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK)
                {
                    stack = inv.getStack(i).copy();
                }
                else
                {
                    if(!(ingredient.getItem() instanceof DyeItem))
                    {
                        return ItemStack.EMPTY;
                    }

                    list.add((DyeItem)ingredient.getItem());
                }
            }
        }

        return !stack.isEmpty() && !list.isEmpty() ? dyeItem(stack, list) : ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height)
    {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ModRecipeSerializers.BACKPACK_DYE;
    }

    public static boolean hasColor(ItemStack stack)
    {
        if(stack.getNbt() != null)
        {
            return stack.getNbt().contains(ITravelersBackpackInventory.COLOR);
        }
        return false;
    }

    public static int getColor(ItemStack stack)
    {
        return stack.getNbt().getInt(ITravelersBackpackInventory.COLOR);
    }

    public static void setColor(ItemStack stack, int color)
    {
        if(stack.getNbt() != null)
        {
            stack.getNbt().putInt(ITravelersBackpackInventory.COLOR, color);
        }
        else
        {
            NbtCompound compoundNBT = new NbtCompound();
            compoundNBT.putInt(ITravelersBackpackInventory.COLOR, color);
            stack.setNbt(compoundNBT);
        }
    }

    public static ItemStack dyeItem(ItemStack stack, List<DyeItem> dyeItem)
    {
        if(stack.getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK)
        {
            int[] ints = new int[3];
            int lvt_4_1_ = 0;
            int lvt_5_1_ = 0;

            int lvt_8_2_;
            float b;
            int lvt_13_1_;

            int baseColor;

            if(hasColor(stack))
            {
                baseColor = getColor(stack);
                float r = (float)(baseColor >> 16 & 255) / 255.0F;
                float g = (float)(baseColor >> 8 & 255) / 255.0F;
                b = (float)(baseColor & 255) / 255.0F;
                lvt_4_1_ = (int)((float)lvt_4_1_ + Math.max(r, Math.max(g, b)) * 255.0F);
                ints[0] = (int)((float)ints[0] + r * 255.0F);
                ints[1] = (int)((float)ints[1] + g * 255.0F);
                ints[2] = (int)((float)ints[2] + b * 255.0F);
                ++lvt_5_1_;
            }

            for(Iterator var14 = dyeItem.iterator(); var14.hasNext(); ++lvt_5_1_)
            {
                DyeItem lvt_9_2_ = (DyeItem)var14.next();
                float[] lvt_10_2_ = lvt_9_2_.getColor().getColorComponents();
                int lvt_11_2_ = (int)(lvt_10_2_[0] * 255.0F);
                int lvt_12_1_ = (int)(lvt_10_2_[1] * 255.0F);
                lvt_13_1_ = (int)(lvt_10_2_[2] * 255.0F);
                lvt_4_1_ += Math.max(lvt_11_2_, Math.max(lvt_12_1_, lvt_13_1_));
                ints[0] += lvt_11_2_;
                ints[1] += lvt_12_1_;
                ints[2] += lvt_13_1_;
            }

            lvt_8_2_ = ints[0] / lvt_5_1_;
            int lvt_9_3_ = ints[1] / lvt_5_1_;
            int lvt_10_3_ = ints[2] / lvt_5_1_;
            b = (float)lvt_4_1_ / (float)lvt_5_1_;
            float lvt_12_2_ = (float)Math.max(lvt_8_2_, Math.max(lvt_9_3_, lvt_10_3_));
            lvt_8_2_ = (int)((float)lvt_8_2_ * b / lvt_12_2_);
            lvt_9_3_ = (int)((float)lvt_9_3_ * b / lvt_12_2_);
            lvt_10_3_ = (int)((float)lvt_10_3_ * b / lvt_12_2_);
            lvt_13_1_ = (lvt_8_2_ << 8) + lvt_9_3_;
            lvt_13_1_ = (lvt_13_1_ << 8) + lvt_10_3_;
            setColor(stack, lvt_13_1_);
        }
        return stack;
    }
}