package com.tiviacz.travelersbackpackold.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpackold.TravelersBackpack;
import com.tiviacz.travelersbackpackold.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpackold.compat.comforts.ComfortsCompat;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpackneo.init.ModRecipeSerializers;
import com.tiviacz.travelersbackpackneo.init.ModTags;
import com.tiviacz.travelersbackpackold.items.TravelersBackpackItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.DyeColor;

public class ShapedBackpackRecipe extends ShapedRecipe
{
    public ShapedBackpackRecipe(String group, CraftingRecipeCategory category, RawShapedRecipe raw, ItemStack result, boolean showNotification)
    {
        super(group, category, raw, result, showNotification);
    }

    @Override
    public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup)
    {
        final ItemStack output = this.getResult(wrapperLookup).copy();

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

                if(!ingredient.isEmpty() && ingredient.isIn(ModTags.SLEEPING_BAGS))
                {
                    int color = getProperColor(ingredient.getItem());

                    if(color != DyeColor.RED.getId())
                    {
                        output.set(ModDataComponents.SLEEPING_BAG_COLOR, color);
                    }
                }
            }
        }
        return output;
    }

    public static int getProperColor(Item item)
    {
        if(item instanceof BlockItem blockItem && blockItem.getBlock() instanceof SleepingBagBlock)
        {
            return ((SleepingBagBlock)blockItem.getBlock()).getColor().getId();
        }
        if(TravelersBackpack.comfortsLoaded)
        {
            return ComfortsCompat.getComfortsSleepingBagColor(item);
        }
        return DyeColor.RED.getId();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ModRecipeSerializers.BACKPACK_SHAPED;
    }

    public static class Serializer implements RecipeSerializer<ShapedBackpackRecipe>
    {
        public static final MapCodec<ShapedBackpackRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.getGroup()),
                        CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter(recipe -> recipe.getCategory()),
                        RawShapedRecipe.CODEC.forGetter(recipe -> recipe.raw), ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                        Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(recipe -> recipe.showNotification())).apply(instance, ShapedBackpackRecipe::new));

        public static final PacketCodec<RegistryByteBuf, ShapedBackpackRecipe> PACKET_CODEC = PacketCodec.ofStatic(ShapedBackpackRecipe.Serializer::write, ShapedBackpackRecipe.Serializer::read);

        @Override
        public MapCodec<ShapedBackpackRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, ShapedBackpackRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static ShapedBackpackRecipe read(RegistryByteBuf buf) {
            String string = buf.readString();
            CraftingRecipeCategory craftingRecipeCategory = buf.readEnumConstant(CraftingRecipeCategory.class);
            RawShapedRecipe rawShapedRecipe = RawShapedRecipe.PACKET_CODEC.decode(buf);
            ItemStack itemStack = ItemStack.PACKET_CODEC.decode(buf);
            boolean bl = buf.readBoolean();
            return new ShapedBackpackRecipe(string, craftingRecipeCategory, rawShapedRecipe, itemStack, bl);
        }

        private static void write(RegistryByteBuf buf, ShapedBackpackRecipe recipe) {
            buf.writeString(recipe.getGroup());
            buf.writeEnumConstant(recipe.getCategory());
            RawShapedRecipe.PACKET_CODEC.encode(buf, recipe.raw);
            ItemStack.PACKET_CODEC.encode(buf, recipe.result);
            buf.writeBoolean(recipe.showNotification());
        }
    }
}