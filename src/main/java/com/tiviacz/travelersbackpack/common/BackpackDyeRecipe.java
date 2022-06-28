package com.tiviacz.travelersbackpack.common;

import com.google.common.collect.Lists;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModRecipeSerializers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.Iterator;
import java.util.List;

public class BackpackDyeRecipe extends CustomRecipe
{
    public BackpackDyeRecipe(ResourceLocation id)
    {
        super(id);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        List<ItemStack> list = Lists.newArrayList();

        for(int i = 0; i < container.getContainerSize(); ++i)
        {
            ItemStack itemstack1 = container.getItem(i);
            if(!itemstack1.isEmpty())
            {
                if(itemstack1.getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK.get())
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
    public ItemStack assemble(CraftingContainer container)
    {
        List<DyeItem> list = Lists.newArrayList();
        ItemStack stack = ItemStack.EMPTY;

        for(int i = 0; i < container.getContainerSize(); ++i)
        {
            ItemStack ingredient = container.getItem(i);
            if(!ingredient.isEmpty())
            {
                if(ingredient.getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK.get())
                {
                    stack = container.getItem(i).copy();
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
    public boolean canCraftInDimensions(int width, int height)
    {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ModRecipeSerializers.BACKPACK_DYE.get();
    }

    public static boolean hasColor(ItemStack stack)
    {
        if(stack.getTag() != null)
        {
            return stack.getTag().contains("Color");
        }
        return false;
    }

    public static int getColor(ItemStack stack)
    {
        return stack.getTag().getInt("Color");
    }

    public static void setColor(ItemStack stack, int color)
    {
        if(stack.getTag() != null)
        {
            stack.getTag().putInt("Color", color);
        }
        else
        {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putInt("Color", color);
            stack.setTag(compoundTag);
        }
    }

    public static ItemStack dyeItem(ItemStack stack, List<DyeItem> dyeItem)
    {
        if(stack.getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK.get())
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
                float[] lvt_10_2_ = lvt_9_2_.getDyeColor().getTextureDiffuseColors();
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
