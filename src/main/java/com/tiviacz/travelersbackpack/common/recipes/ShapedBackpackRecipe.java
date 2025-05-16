package com.tiviacz.travelersbackpack.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.compat.comforts.ComfortsCompat;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.components.StarterUpgrades;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.items.upgrades.TanksUpgradeItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.List;

public class ShapedBackpackRecipe extends ShapedRecipe {
    public ShapedBackpackRecipe(String groupIn, CraftingBookCategory category, ShapedRecipePattern shapedRecipePattern, ItemStack recipeOutputIn, boolean pShowNotification) {
        super(groupIn, category, shapedRecipePattern, recipeOutputIn, pShowNotification);
    }

    @Override
    public ItemStack assemble(CraftingInput pInput, HolderLookup.Provider pRegistries) {
        ItemStack output = this.getResultItem(pRegistries).copy();

        if(!output.isEmpty()) {
            boolean hasTanks = false;
            boolean customBackpack = false;
            for(int i = 0; i < pInput.size(); i++) {
                ItemStack ingredient = pInput.getItem(i);
                if(ingredient.getItem() instanceof TravelersBackpackItem) {
                    output.applyComponents(ingredient.getComponentsPatch());
                    customBackpack = true;
                    //Only for custom backpacks so break here
                    break;
                }

                if(ingredient.is(ModTags.SLEEPING_BAGS)) {
                    int color = getProperColor(ingredient.getItem());
                    output.set(ModDataComponents.SLEEPING_BAG_COLOR, color);
                }

                if(!hasTanks && ingredient.getItem() == ModItems.BACKPACK_TANK.get()) {
                    output.set(ModDataComponents.STARTER_UPGRADES, new StarterUpgrades(List.of(ModItems.TANKS_UPGRADE.toStack())));
                    hasTanks = true;
                }
            }
            if(!customBackpack) {
                output.set(ModDataComponents.STORAGE_SLOTS, Tiers.LEATHER.getStorageSlots());
                if(hasTanks) {
                    output.set(ModDataComponents.RENDER_INFO, TanksUpgradeItem.writeToRenderData());
                } else {
                    output.set(ModDataComponents.RENDER_INFO, RenderInfo.EMPTY);
                }
            }
        }
        return output;
    }

    public static int getProperColor(Item item) {
        if(item instanceof BlockItem blockItem && blockItem.getBlock() instanceof SleepingBagBlock sleepingBagBlock) {
            return sleepingBagBlock.getColor().getId();
        }
        if(TravelersBackpack.comfortsLoaded) {
            return ComfortsCompat.getComfortsSleepingBagColor(item);
        }
        return DyeColor.RED.getId();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    public static class Serializer implements RecipeSerializer<ShapedBackpackRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        public static final MapCodec<ShapedBackpackRecipe> CODEC = RecordCodecBuilder.mapCodec(
                p_340778_ -> p_340778_.group(
                                Codec.STRING.optionalFieldOf("group", "").forGetter(p_311729_ -> p_311729_.getGroup()),
                                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(p_311732_ -> p_311732_.category()),
                                ShapedRecipePattern.MAP_CODEC.forGetter(p_311733_ -> p_311733_.pattern),
                                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(p_311730_ -> p_311730_.result),
                                Codec.BOOL.optionalFieldOf("show_notification", Boolean.valueOf(true)).forGetter(p_311731_ -> p_311731_.showNotification())
                        )
                        .apply(p_340778_, ShapedBackpackRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, ShapedBackpackRecipe> STREAM_CODEC = StreamCodec.of(
                ShapedBackpackRecipe.Serializer::toNetwork, ShapedBackpackRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<ShapedBackpackRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ShapedBackpackRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static ShapedBackpackRecipe fromNetwork(RegistryFriendlyByteBuf p_319998_) {
            String s = p_319998_.readUtf();
            CraftingBookCategory craftingbookcategory = p_319998_.readEnum(CraftingBookCategory.class);
            ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.STREAM_CODEC.decode(p_319998_);
            ItemStack itemstack = ItemStack.STREAM_CODEC.decode(p_319998_);
            boolean flag = p_319998_.readBoolean();
            return new ShapedBackpackRecipe(s, craftingbookcategory, shapedrecipepattern, itemstack, flag);
        }

        private static void toNetwork(RegistryFriendlyByteBuf p_320738_, ShapedBackpackRecipe p_320586_) {
            p_320738_.writeUtf(p_320586_.getGroup());
            p_320738_.writeEnum(p_320586_.category());
            ShapedRecipePattern.STREAM_CODEC.encode(p_320738_, p_320586_.pattern);
            ItemStack.STREAM_CODEC.encode(p_320738_, p_320586_.result);
            p_320738_.writeBoolean(p_320586_.showNotification());
        }
    }
}