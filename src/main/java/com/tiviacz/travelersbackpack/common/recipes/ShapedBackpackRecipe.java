package com.tiviacz.travelersbackpack.common.recipes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
                    //output.set(ModDataComponents.SLEEPING_BAG_COLOR.get(), color);
                }

                if(!hasTanks && ingredient.getItem() == ModItems.BACKPACK_TANK) {
                    NbtHelper.set(output, ModDataHelper.STARTER_UPGRADES, List.of(ModItems.TANKS_UPGRADE.getDefaultInstance()));
                    // output.set(ModDataComponents.STARTER_UPGRADES.get(), List.of(ModItems.TANKS_UPGRADE.get().getDefaultInstance()));
                    hasTanks = true;
                }
            }
            if(!customBackpack) {
                NbtHelper.set(output, ModDataHelper.STORAGE_SLOTS, Tiers.LEATHER.getStorageSlots());
                //output.set(ModDataComponents.STORAGE_SLOTS.get(), Tiers.LEATHER.getStorageSlots());
                if(hasTanks) {
                    NbtHelper.set(output, ModDataHelper.RENDER_INFO, TanksUpgradeItem.writeToRenderData());
                    //output.set(ModDataComponents.RENDER_INFO.get(), TanksUpgradeItem.writeToRenderData());
                } else {
                    NbtHelper.set(output, ModDataHelper.RENDER_INFO, RenderInfo.EMPTY);
                    //  output.set(ModDataComponents.RENDER_INFO.get(), RenderInfo.EMPTY);
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
            CraftingBookCategory craftingbookcategory = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(json, "category", (String)null), CraftingBookCategory.MISC);
            Map<String, Ingredient> map = keyFromJson(GsonHelper.getAsJsonObject(json, "key"));
            String[] strings = shrink(patternFromJson(GsonHelper.getAsJsonArray(json, "pattern")));
            int i = strings[0].length();
            int j = strings.length;
            NonNullList<Ingredient> nonNullList = dissolvePattern(strings, map, i, j);
            ItemStack itemStack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            boolean flag = GsonHelper.getAsBoolean(json, "show_notification", true);

            return new ShapedBackpackRecipe(recipeID, group, craftingbookcategory, i, j, nonNullList, itemStack, flag);
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
            buffer.writeVarInt(recipe.getWidth());
            buffer.writeVarInt(recipe.getHeight());
            buffer.writeUtf(recipe.getGroup());
            buffer.writeEnum(recipe.category());

            for(final Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.result);
            buffer.writeBoolean(recipe.showNotification());
        }

        static NonNullList<Ingredient> dissolvePattern(String[] pattern, Map<String, Ingredient> keys, int patternWidth, int patternHeight) {
            NonNullList<Ingredient> nonNullList = NonNullList.withSize(patternWidth * patternHeight, Ingredient.EMPTY);
            Set<String> set = Sets.newHashSet(keys.keySet());
            set.remove(" ");

            for(int i = 0; i < pattern.length; ++i) {
                for(int j = 0; j < pattern[i].length(); ++j) {
                    String string = pattern[i].substring(j, j + 1);
                    Ingredient ingredient = (Ingredient)keys.get(string);
                    if(ingredient == null) {
                        throw new JsonSyntaxException("Pattern references symbol '" + string + "' but it's not defined in the key");
                    }

                    set.remove(string);
                    nonNullList.set(j + patternWidth * i, ingredient);
                }
            }

            if(!set.isEmpty()) {
                throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
            } else {
                return nonNullList;
            }
        }

        static Map<String, Ingredient> keyFromJson(JsonObject keyEntry) {
            Map<String, Ingredient> map = Maps.newHashMap();

            for(Map.Entry<String, JsonElement> entry : keyEntry.entrySet()) {
                if(((String)entry.getKey()).length() != 1) {
                    throw new JsonSyntaxException("Invalid key entry: '" + (String)entry.getKey() + "' is an invalid symbol (must be 1 character only).");
                }

                if(" ".equals(entry.getKey())) {
                    throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
                }

                map.put((String)entry.getKey(), Ingredient.fromJson((JsonElement)entry.getValue(), false));
            }

            map.put(" ", Ingredient.EMPTY);
            return map;
        }

        @VisibleForTesting
        static String[] shrink(String... toShrink) {
            int i = Integer.MAX_VALUE;
            int j = 0;
            int k = 0;
            int l = 0;

            for(int m = 0; m < toShrink.length; ++m) {
                String string = toShrink[m];
                i = Math.min(i, firstNonSpace(string));
                int n = lastNonSpace(string);
                j = Math.max(j, n);
                if(n < 0) {
                    if(k == m) {
                        ++k;
                    }

                    ++l;
                } else {
                    l = 0;
                }
            }

            if(toShrink.length == l) {
                return new String[0];
            } else {
                String[] strings = new String[toShrink.length - l - k];

                for(int o = 0; o < strings.length; ++o) {
                    strings[o] = toShrink[o + k].substring(i, j + 1);
                }

                return strings;
            }
        }

        private static int firstNonSpace(String entry) {
            int i;
            for(i = 0; i < entry.length() && entry.charAt(i) == ' '; ++i) {
            }

            return i;
        }

        private static int lastNonSpace(String entry) {
            int i;
            for(i = entry.length() - 1; i >= 0 && entry.charAt(i) == ' '; --i) {
            }

            return i;
        }

        static String[] patternFromJson(JsonArray patternArray) {
            String[] strings = new String[patternArray.size()];
            if(strings.length > 3) {
                throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
            } else if(strings.length == 0) {
                throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
            } else {
                for(int i = 0; i < strings.length; ++i) {
                    String string = GsonHelper.convertToString(patternArray.get(i), "pattern[" + i + "]");
                    if(string.length() > 3) {
                        throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
                    }

                    if(i > 0 && strings[0].length() != string.length()) {
                        throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                    }

                    strings[i] = string;
                }

                return strings;
            }
        }
    }

}