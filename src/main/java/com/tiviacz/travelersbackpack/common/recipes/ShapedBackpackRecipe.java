package com.tiviacz.travelersbackpack.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.items.SleepingBagItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

public class ShapedBackpackRecipe extends ShapedRecipe {
    public ShapedBackpackRecipe(String groupIn, CraftingBookCategory category, ShapedRecipePattern shapedRecipePattern, ItemStack recipeOutputIn, boolean pShowNotification) {
        super(groupIn, category, shapedRecipePattern, recipeOutputIn, pShowNotification);
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess access) {
        final ItemStack output = super.assemble(inv, access);

        if (!output.isEmpty()) {
            for (int i = 0; i < inv.getContainerSize(); i++) {
                final ItemStack ingredient = inv.getItem(i);

                if (!ingredient.isEmpty() && ingredient.getItem() instanceof TravelersBackpackItem) {
                    final CompoundTag compound = ingredient.getTag();
                    output.setTag(compound);
                    break;
                }

                if (!ingredient.isEmpty() && ingredient.getItem() instanceof SleepingBagItem) {
                    output.getOrCreateTag().putInt(ITravelersBackpackContainer.SLEEPING_BAG_COLOR, getProperColor((SleepingBagItem) ingredient.getItem()));
                }
            }
        }
        return output;
    }

    public static int getProperColor(SleepingBagItem item) {
        if (item.getBlock() instanceof SleepingBagBlock sleepingBag) {
            return sleepingBag.getColor().getId();
        }
        return DyeColor.RED.getId();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    public static class Serializer implements RecipeSerializer<ShapedBackpackRecipe>
    {
        public static final Serializer INSTANCE = new Serializer();

        public static final Codec<ShapedBackpackRecipe> CODEC = RecordCodecBuilder.create((p_309256_) -> {
            return p_309256_.group(ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter((p_309251_) -> {
                return p_309251_.getGroup();
            }), CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter((p_309253_) -> {
                return p_309253_.category();
            }), ShapedRecipePattern.MAP_CODEC.forGetter((p_309254_) -> {
                return p_309254_.pattern;
            }), ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter((p_309252_) -> {
                return p_309252_.result;
            }), ExtraCodecs.strictOptionalField(Codec.BOOL, "show_notification", true).forGetter((p_309255_) -> {
                return p_309255_.showNotification();
            })).apply(p_309256_, ShapedBackpackRecipe::new);
        });

        @Override
        public Codec<ShapedBackpackRecipe> codec() {
            return CODEC;
        }

        @Override
        public @org.jetbrains.annotations.Nullable ShapedBackpackRecipe fromNetwork(FriendlyByteBuf buffer)
        {
            String s = buffer.readUtf();
            CraftingBookCategory craftingbookcategory = buffer.readEnum(CraftingBookCategory.class);
            ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.fromNetwork(buffer);
            ItemStack itemstack = buffer.readItem();
            boolean flag = buffer.readBoolean();

            return new ShapedBackpackRecipe(s, craftingbookcategory, shapedrecipepattern, itemstack, flag);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ShapedBackpackRecipe recipe)
        {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeEnum(recipe.category());
            recipe.pattern.toNetwork(buffer);
            buffer.writeItem(recipe.result);
            buffer.writeBoolean(recipe.showNotification());
        }
    }
}