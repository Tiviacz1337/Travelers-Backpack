package com.tiviacz.travelersbackpack.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.EventHooks;

public class ShapelessBackpackRecipe extends ShapelessRecipe
{
    public ShapelessBackpackRecipe(String groupIn, CraftingBookCategory category, ItemStack recipeOutputIn, NonNullList<Ingredient> recipeItemsIn)
    {
        super(groupIn, category, recipeOutputIn, recipeItemsIn);
    }

    @Override
    public ItemStack assemble(final CraftingContainer inv, RegistryAccess registryAccess)
    {
        final ItemStack output = super.assemble(inv, registryAccess);

        if(!output.isEmpty())
        {
            for(int i = 0; i < inv.getContainerSize(); i++)
            {
                final ItemStack ingredient = inv.getItem(i);

                if(!ingredient.isEmpty() && ingredient.getItem() instanceof TravelersBackpackItem)
                {
                    final CompoundTag compound = ingredient.getTag();
                    output.setTag(compound);
                    break;
                }
            }
        }
        return output;
    }

    private ItemStack damageShears(final ItemStack stack)
    {
        final Player craftingPlayer = CommonHooks.getCraftingPlayer();

        if(stack.hurt(1, craftingPlayer == null ? RandomSource.create() : craftingPlayer.level().random, craftingPlayer instanceof ServerPlayer ? (ServerPlayer) craftingPlayer : null))
        {
            EventHooks.onPlayerDestroyItem(craftingPlayer, stack, null);
            return ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(final CraftingContainer inventoryCrafting)
    {
        final NonNullList<ItemStack> remainingItems = NonNullList.withSize(inventoryCrafting.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < remainingItems.size(); ++i)
        {
            final ItemStack itemstack = inventoryCrafting.getItem(i);

            if(!itemstack.isEmpty() && itemstack.getItem() instanceof ShearsItem)
            {
                remainingItems.set(i, damageShears(itemstack.copy()));
            }
            else
            {
                remainingItems.set(i, CommonHooks.getCraftingRemainingItem(itemstack));
            }
        }
        return remainingItems;
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

    public static class Serializer implements RecipeSerializer<ShapelessBackpackRecipe>
    {
        public static final Serializer INSTANCE = new Serializer();

        private static final Codec<ShapelessBackpackRecipe> f_290581_ = RecordCodecBuilder.create((p_300970_) -> {
            return p_300970_.group(ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter((p_299460_) -> {
                return p_299460_.getGroup();
            }), CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter((p_297437_) -> {
                return p_297437_.category();
            }), CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.fieldOf("result").forGetter((p_300770_) -> {
                return p_300770_.result;
            }), Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap((p_297969_) -> {
                Ingredient[] aingredient = p_297969_.stream().filter((p_298915_) -> {
                    return !p_298915_.isEmpty();
                }).toArray((p_298774_) -> {
                    return new Ingredient[p_298774_];
                });
                if (aingredient.length == 0) {
                    return DataResult.error(() -> {
                        return "No ingredients for shapeless recipe";
                    });
                } else {
                    return aingredient.length > 3 * 3 ? DataResult.error(() -> {
                        return "Too many ingredients for shapeless recipe";
                    }) : DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                }
            }, DataResult::success).forGetter((p_298509_) -> {
                return p_298509_.getIngredients();
            })).apply(p_300970_, ShapelessBackpackRecipe::new);
        });

        @Override
        public Codec<ShapelessBackpackRecipe> codec()
        {
            return f_290581_;
        }

        @Override
        public @org.jetbrains.annotations.Nullable ShapelessBackpackRecipe fromNetwork(FriendlyByteBuf pBuffer)
        {
            String s = pBuffer.readUtf();
            CraftingBookCategory craftingbookcategory = pBuffer.readEnum(CraftingBookCategory.class);
            int i = pBuffer.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

            for(int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, Ingredient.fromNetwork(pBuffer));
            }

            ItemStack itemstack = pBuffer.readItem();
            return new ShapelessBackpackRecipe(s, craftingbookcategory, itemstack, nonnulllist);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ShapelessBackpackRecipe recipe)
        {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeEnum(recipe.category());
            buffer.writeVarInt(recipe.getIngredients().size());

            for(Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.result);
        }
    }
}