package com.tiviacz.travelersbackpack.common.recipes;

import com.google.gson.JsonObject;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.compat.comforts.ComfortsCompat;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.items.upgrades.TanksUpgradeItem;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import com.tiviacz.travelersbackpack.util.RecipeUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShapedBackpackRecipe extends ShapedRecipe {
    public ShapedBackpackRecipe(ResourceLocation idIn, String groupIn, CraftingBookCategory category, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn, boolean showNotification) {
        super(idIn, groupIn, category, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn, showNotification);
    }

    @Override
    public ItemStack assemble(CraftingContainer pInput, RegistryAccess pRegistries) {
        ItemStack output = this.getResultItem(pRegistries).copy();

        if(!output.isEmpty()) {
            boolean hasTanks = false;
            boolean customBackpack = false;
            for(int i = 0; i < pInput.getContainerSize(); i++) {
                ItemStack ingredient = pInput.getItem(i);
                if(ingredient.getItem() instanceof TravelersBackpackItem) {
                    output.setTag(ingredient.getOrCreateTag());
                    customBackpack = true;
                    //Only for custom backpacks so break here
                    break;
                }

                if(ingredient.is(ModTags.SLEEPING_BAGS)) {
                    int color = getProperColor(ingredient.getItem());
                    NbtHelper.set(output, ModDataHelper.SLEEPING_BAG_COLOR, color);
                }

                if(!hasTanks && ingredient.getItem() == ModItems.BACKPACK_TANK.get()) {
                    NbtHelper.set(output, ModDataHelper.STARTER_UPGRADES, List.of(ModItems.TANKS_UPGRADE.get().getDefaultInstance()));
                    hasTanks = true;
                }
            }
            if(!customBackpack) {
                NbtHelper.set(output, ModDataHelper.STORAGE_SLOTS, Tiers.LEATHER.getStorageSlots());
                if(hasTanks) {
                    NbtHelper.set(output, ModDataHelper.RENDER_INFO, TanksUpgradeItem.writeToRenderData());
                } else {
                    NbtHelper.set(output, ModDataHelper.RENDER_INFO, RenderInfo.EMPTY);
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

        @Override
        public ShapedBackpackRecipe fromJson(ResourceLocation recipeID, JsonObject json) {
            final String group = GsonHelper.getAsString(json, "group", "");
            CraftingBookCategory craftingbookcategory = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(json, "category", null), CraftingBookCategory.MISC);
            final RecipeUtils.ShapedPrimer primer = RecipeUtils.parseShaped(json);
            final ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
            boolean flag = GsonHelper.getAsBoolean(json, "show_notification", true);

            return new ShapedBackpackRecipe(recipeID, group, craftingbookcategory, primer.recipeWidth(), primer.recipeHeight(), primer.ingredients(), result, flag);
        }

        @Nullable
        @Override
        public ShapedBackpackRecipe fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buffer) {
            final int width = buffer.readVarInt();
            final int height = buffer.readVarInt();
            final String group = buffer.readUtf(Short.MAX_VALUE);
            CraftingBookCategory craftingbookcategory = buffer.readEnum(CraftingBookCategory.class);
            final NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);

            for(int i = 0; i < ingredients.size(); ++i) {
                ingredients.set(i, Ingredient.fromNetwork(buffer));
            }

            final ItemStack result = buffer.readItem();
            boolean flag = buffer.readBoolean();

            return new ShapedBackpackRecipe(recipeID, group, craftingbookcategory, width, height, ingredients, result, flag);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ShapedBackpackRecipe recipe) {
            buffer.writeVarInt(recipe.getRecipeWidth());
            buffer.writeVarInt(recipe.getRecipeHeight());
            buffer.writeUtf(recipe.getGroup());
            buffer.writeEnum(recipe.category());

            for(final Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItemStack(recipe.result, false);
            buffer.writeBoolean(recipe.showNotification());
        }
    }
}