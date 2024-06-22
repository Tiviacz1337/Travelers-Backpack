package com.tiviacz.travelersbackpack.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.init.ModRecipeSerializers;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;

import java.util.Iterator;

public class ShapelessBackpackRecipe extends ShapelessRecipe
{
    public ShapelessBackpackRecipe(String group, CraftingRecipeCategory category, ItemStack output, DefaultedList<Ingredient> input)
    {
        super(group, category, output, input);
    }

    @Override
    public ItemStack craft(final RecipeInputInventory inv, DynamicRegistryManager manager)
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
            }
        }
        return output;
    }

    private ItemStack damageShears(final ItemStack stack)
    {
        if(stack.getDamage() + 1 <= stack.getMaxDamage())
        {
            stack.setDamage(stack.getDamage() + 1);
            return stack;
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inventoryCrafting)
    {
        final DefaultedList<ItemStack> remainingItems = DefaultedList.ofSize(inventoryCrafting.size(), ItemStack.EMPTY);

        for(int i = 0; i < remainingItems.size(); ++i)
        {
            final ItemStack itemstack = inventoryCrafting.getStack(i);

            if(!itemstack.isEmpty() && itemstack.getItem() instanceof ShearsItem)
            {
                remainingItems.set(i, damageShears(itemstack.copy()));
            }
            if(itemstack.getItem().hasRecipeRemainder())
            {
                remainingItems.set(i, new ItemStack(itemstack.getItem().getRecipeRemainder()));
            }
        }
        return remainingItems;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ModRecipeSerializers.BACKPACK_SHAPELESS;
    }

    public static class Serializer implements RecipeSerializer<ShapelessBackpackRecipe> {
        private static final Codec<ShapelessBackpackRecipe> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Codecs.createStrictOptionalFieldCodec(Codec.STRING, "group", "").forGetter((recipe) -> {
                return recipe.getGroup();
            }), CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter((recipe) -> {
                return recipe.getCategory();
            }), ItemStack.RECIPE_RESULT_CODEC.fieldOf("result").forGetter((recipe) -> {
                return recipe.result;
            }), Ingredient.DISALLOW_EMPTY_CODEC.listOf().fieldOf("ingredients").flatXmap((ingredients) -> {
                Ingredient[] ingredients2 = (Ingredient[])ingredients.stream().filter((ingredient) -> {
                    return !ingredient.isEmpty();
                }).toArray((i) -> {
                    return new Ingredient[i];
                });
                if (ingredients2.length == 0) {
                    return DataResult.error(() -> {
                        return "No ingredients for shapeless recipe";
                    });
                } else {
                    return ingredients2.length > 9 ? DataResult.error(() -> {
                        return "Too many ingredients for shapeless recipe";
                    }) : DataResult.success(DefaultedList.copyOf(Ingredient.EMPTY, ingredients2));
                }
            }, DataResult::success).forGetter((recipe) -> {
                return recipe.getIngredients();
            })).apply(instance, ShapelessBackpackRecipe::new);
        });

        public Serializer() {
        }

        public Codec<ShapelessBackpackRecipe> codec() {
            return CODEC;
        }

        public ShapelessBackpackRecipe read(PacketByteBuf packetByteBuf) {
            String string = packetByteBuf.readString();
            CraftingRecipeCategory craftingRecipeCategory = (CraftingRecipeCategory)packetByteBuf.readEnumConstant(CraftingRecipeCategory.class);
            int i = packetByteBuf.readVarInt();
            DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i, Ingredient.EMPTY);

            for(int j = 0; j < defaultedList.size(); ++j) {
                defaultedList.set(j, Ingredient.fromPacket(packetByteBuf));
            }

            ItemStack itemStack = packetByteBuf.readItemStack();
            return new ShapelessBackpackRecipe(string, craftingRecipeCategory, itemStack, defaultedList);
        }

        public void write(PacketByteBuf packetByteBuf, ShapelessBackpackRecipe shapelessRecipe) {
            packetByteBuf.writeString(shapelessRecipe.getGroup());
            packetByteBuf.writeEnumConstant(shapelessRecipe.getCategory());
            packetByteBuf.writeVarInt(shapelessRecipe.getIngredients().size());
            Iterator var3 = shapelessRecipe.getIngredients().iterator();

            while(var3.hasNext()) {
                Ingredient ingredient = (Ingredient)var3.next();
                ingredient.write(packetByteBuf);
            }

            packetByteBuf.writeItemStack(shapelessRecipe.result);
        }
    }
}