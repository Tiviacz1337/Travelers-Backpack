package com.tiviacz.travelersbackpack.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.init.ModCrafting;
import com.tiviacz.travelersbackpack.items.SleepingBagItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.DyeColor;
import net.minecraft.util.dynamic.Codecs;

public class ShapedBackpackRecipe extends ShapedRecipe
{
    public ShapedBackpackRecipe(String group, CraftingRecipeCategory category, RawShapedRecipe raw, ItemStack result, boolean showNotification)
    {
        super(group, category, raw, result, showNotification);
    }

    @Override
    public ItemStack craft(RecipeInputInventory inv, DynamicRegistryManager manager)
    {
        final ItemStack output = super.craft(inv, manager);

        if(!output.isEmpty())
        {
            for(int i = 0; i < inv.size(); i++)
            {
                final ItemStack ingredient = inv.getStack(i);

                if(!ingredient.isEmpty() && ingredient.getItem() instanceof TravelersBackpackItem)
                {
                    final NbtCompound compound = ingredient.getNbt();
                    output.setNbt(compound);
                    break;
                }

                if(!ingredient.isEmpty() && ingredient.getItem() instanceof SleepingBagItem)
                {
                    output.getOrCreateNbt().putInt("SleepingBagColor", getProperColor((SleepingBagItem)ingredient.getItem()));
                }
            }
        }
        return output;
    }

    public static int getProperColor(SleepingBagItem item)
    {
        if(item.getBlock() instanceof SleepingBagBlock)
        {
            return ((SleepingBagBlock)item.getBlock()).getColor().getId();
        }
        return DyeColor.RED.getId();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ModCrafting.BACKPACK_SHAPED;
    }

    public static class Serializer implements RecipeSerializer<ShapedBackpackRecipe>
    {
        public static final Codec<ShapedBackpackRecipe> CODEC = RecordCodecBuilder.create((p_309256_) -> {
            return p_309256_.group(Codecs.createStrictOptionalFieldCodec(Codec.STRING, "group", "").forGetter((p_309251_) -> {
                return p_309251_.getGroup();
            }), CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter((p_309253_) -> {
                return p_309253_.getCategory();
            }), RawShapedRecipe.CODEC.forGetter((p_309254_) -> {
                return p_309254_.raw;
            }), ItemStack.RECIPE_RESULT_CODEC.fieldOf("result").forGetter((p_309252_) -> {
                return p_309252_.result;
            }), Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "show_notification", true).forGetter((p_309255_) -> {
                return p_309255_.showNotification();
            })).apply(p_309256_, ShapedBackpackRecipe::new);
        });

        @Override
        public Codec<ShapedBackpackRecipe> codec() {
            return CODEC;
        }

        @Override
        public ShapedBackpackRecipe read(PacketByteBuf packetByteBuf) {
            String string = packetByteBuf.readString();
            CraftingRecipeCategory craftingRecipeCategory = packetByteBuf.readEnumConstant(CraftingRecipeCategory.class);
            RawShapedRecipe rawShapedRecipe = RawShapedRecipe.readFromBuf(packetByteBuf);
            ItemStack itemStack = packetByteBuf.readItemStack();
            boolean bl = packetByteBuf.readBoolean();
            return new ShapedBackpackRecipe(string, craftingRecipeCategory, rawShapedRecipe, itemStack, bl);
        }

        @Override
        public void write(PacketByteBuf packetByteBuf, ShapedBackpackRecipe shapedBackpackRecipe) {
            packetByteBuf.writeString(shapedBackpackRecipe.getGroup());
            packetByteBuf.writeEnumConstant(shapedBackpackRecipe.getCategory());
            shapedBackpackRecipe.raw.writeToBuf(packetByteBuf);
            packetByteBuf.writeItemStack(shapedBackpackRecipe.result);
            packetByteBuf.writeBoolean(shapedBackpackRecipe.showNotification());
        }
    }
}