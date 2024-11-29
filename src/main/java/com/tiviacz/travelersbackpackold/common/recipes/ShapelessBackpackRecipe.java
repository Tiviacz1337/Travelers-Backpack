package com.tiviacz.travelersbackpackold.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpackneo.init.ModRecipeSerializers;
import com.tiviacz.travelersbackpackold.items.TravelersBackpackItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;

public class ShapelessBackpackRecipe extends ShapelessRecipe
{
    public ShapelessBackpackRecipe(String group, CraftingRecipeCategory category, ItemStack output, DefaultedList<Ingredient> input)
    {
        super(group, category, output, input);
    }

    @Override
    public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup)
    {
        final ItemStack output = this.result.copy();

        if(!output.isEmpty())
        {
            for(int i = 0; i < craftingRecipeInput.getSize(); i++)
            {
                final ItemStack ingredient = craftingRecipeInput.getStackInSlot(i);

                if(!ingredient.isEmpty() && ingredient.getItem() instanceof TravelersBackpackItem)
                {
                    output.applyUnvalidatedChanges(ingredient.getComponentChanges());
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
    public DefaultedList<ItemStack> getRemainder(CraftingRecipeInput craftingRecipeInput)
    {
        final DefaultedList<ItemStack> remainingItems = DefaultedList.ofSize(craftingRecipeInput.getSize(), ItemStack.EMPTY);

        for(int i = 0; i < remainingItems.size(); ++i)
        {
            final ItemStack itemstack = craftingRecipeInput.getStackInSlot(i);

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

    public static class Serializer implements RecipeSerializer<ShapelessBackpackRecipe>
    {
        private static final MapCodec<ShapelessBackpackRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.getGroup()),
                CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter(recipe -> recipe.getCategory()),
                ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(recipe -> recipe.result), Ingredient.DISALLOW_EMPTY_CODEC.listOf().fieldOf("ingredients").flatXmap(ingredients -> {
            Ingredient[] ingredients2 = ingredients.stream().filter(ingredient -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
            if (ingredients2.length == 0) {
                return DataResult.error(() -> "No ingredients for shapeless recipe");
            }
            if (ingredients2.length > 9) {
                return DataResult.error(() -> "Too many ingredients for shapeless recipe");
            }
            return DataResult.success(DefaultedList.copyOf(Ingredient.EMPTY, ingredients2));
        }, DataResult::success).forGetter(recipe -> recipe.getIngredients())).apply(instance, ShapelessBackpackRecipe::new));

        public static final PacketCodec<RegistryByteBuf, ShapelessBackpackRecipe> PACKET_CODEC = PacketCodec.ofStatic(ShapelessBackpackRecipe.Serializer::write, ShapelessBackpackRecipe.Serializer::read);

        @Override
        public MapCodec<ShapelessBackpackRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, ShapelessBackpackRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static ShapelessBackpackRecipe read(RegistryByteBuf buf) {
            String string = buf.readString();
            CraftingRecipeCategory craftingRecipeCategory = buf.readEnumConstant(CraftingRecipeCategory.class);
            int i = buf.readVarInt();
            DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i, Ingredient.EMPTY);
            defaultedList.replaceAll(empty -> Ingredient.PACKET_CODEC.decode(buf));
            ItemStack itemStack = ItemStack.PACKET_CODEC.decode(buf);
            return new ShapelessBackpackRecipe(string, craftingRecipeCategory, itemStack, defaultedList);
        }

        private static void write(RegistryByteBuf buf, ShapelessBackpackRecipe recipe) {
            buf.writeString(recipe.getGroup());
            buf.writeEnumConstant(recipe.getCategory());
            buf.writeVarInt(recipe.getIngredients().size());
            for (Ingredient ingredient : recipe.getIngredients()) {
                Ingredient.PACKET_CODEC.encode(buf, ingredient);
            }
            ItemStack.PACKET_CODEC.encode(buf, recipe.result);
        }
    }
}