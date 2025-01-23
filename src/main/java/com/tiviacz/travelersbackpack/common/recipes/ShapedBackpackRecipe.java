package com.tiviacz.travelersbackpack.common.recipes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.compat.comforts.ComfortsCompat;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModRecipeSerializers;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.items.upgrades.TanksUpgradeItem;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShapedBackpackRecipe implements CraftingRecipe {
    final int width;
    final int height;
    final NonNullList<Ingredient> recipeItems;
    public final ItemStack result;
    private final ResourceLocation id;
    final String group;
    final CraftingBookCategory category;
    final boolean showNotification;

    public ShapedBackpackRecipe(ResourceLocation id, String group, CraftingBookCategory category, int width, int height, NonNullList<Ingredient> recipeItems, ItemStack result, boolean showNotification) {
        this.id = id;
        this.group = group;
        this.category = category;
        this.width = width;
        this.height = height;
        this.recipeItems = recipeItems;
        this.result = result;
        this.showNotification = showNotification;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BACKPACK_SHAPED;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public CraftingBookCategory category() {
        return this.category;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.recipeItems;
    }

    @Override
    public boolean showNotification() {
        return this.showNotification;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= this.width && height >= this.height;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        for(int i = 0; i <= inv.getWidth() - this.width; ++i) {
            for(int j = 0; j <= inv.getHeight() - this.height; ++j) {
                if (this.matches(inv, i, j, true)) {
                    return true;
                }

                if (this.matches(inv, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matches(CraftingContainer craftingInventory, int width, int height, boolean mirrored) {
        for(int i = 0; i < craftingInventory.getWidth(); ++i) {
            for(int j = 0; j < craftingInventory.getHeight(); ++j) {
                int k = i - width;
                int l = j - height;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < this.width && l < this.height) {
                    if (mirrored) {
                        ingredient = (Ingredient)this.recipeItems.get(this.width - k - 1 + l * this.width);
                    } else {
                        ingredient = (Ingredient)this.recipeItems.get(k + l * this.width);
                    }
                }

                if (!ingredient.test(craftingInventory.getItem(i + j * craftingInventory.getWidth()))) {
                    return false;
                }
            }
        }

        return true;
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

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    static NonNullList<Ingredient> dissolvePattern(String[] pattern, Map<String, Ingredient> keys, int patternWidth, int patternHeight) {
        NonNullList<Ingredient> nonNullList = NonNullList.withSize(patternWidth * patternHeight, Ingredient.EMPTY);
        Set<String> set = Sets.newHashSet(keys.keySet());
        set.remove(" ");

        for(int i = 0; i < pattern.length; ++i) {
            for(int j = 0; j < pattern[i].length(); ++j) {
                String string = pattern[i].substring(j, j + 1);
                Ingredient ingredient = (Ingredient)keys.get(string);
                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + string + "' but it's not defined in the key");
                }

                set.remove(string);
                nonNullList.set(j + patternWidth * i, ingredient);
            }
        }

        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        } else {
            return nonNullList;
        }
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
            if (n < 0) {
                if (k == m) {
                    ++k;
                }

                ++l;
            } else {
                l = 0;
            }
        }

        if (toShrink.length == l) {
            return new String[0];
        } else {
            String[] strings = new String[toShrink.length - l - k];

            for(int o = 0; o < strings.length; ++o) {
                strings[o] = toShrink[o + k].substring(i, j + 1);
            }

            return strings;
        }
    }

    public boolean isIncomplete() {
        NonNullList<Ingredient> nonNullList = this.getIngredients();
        return nonNullList.isEmpty() || nonNullList.stream().filter((ingredient) -> !ingredient.isEmpty()).anyMatch((ingredient) -> ingredient.getItems().length == 0);
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
        if (strings.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        } else if (strings.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for(int i = 0; i < strings.length; ++i) {
                String string = GsonHelper.convertToString(patternArray.get(i), "pattern[" + i + "]");
                if (string.length() > 3) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
                }

                if (i > 0 && strings[0].length() != string.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                strings[i] = string;
            }

            return strings;
        }
    }

    static Map<String, Ingredient> keyFromJson(JsonObject keyEntry) {
        Map<String, Ingredient> map = Maps.newHashMap();

        for(Map.Entry<String, JsonElement> entry : keyEntry.entrySet()) {
            if (((String)entry.getKey()).length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + (String)entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put((String)entry.getKey(), Ingredient.fromJson((JsonElement)entry.getValue(), false));
        }

        map.put(" ", Ingredient.EMPTY);
        return map;
    }

    public static ItemStack itemStackFromJson(JsonObject stackObject) {
        Item item = itemFromJson(stackObject);
        if (stackObject.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        } else {
            int i = GsonHelper.getAsInt(stackObject, "count", 1);
            if (i < 1) {
                throw new JsonSyntaxException("Invalid output count: " + i);
            } else {
                return new ItemStack(item, i);
            }
        }
    }

    public static Item itemFromJson(JsonObject itemObject) {
        String string = GsonHelper.getAsString(itemObject, "item");
        Item item = (Item)BuiltInRegistries.ITEM.getOptional(ResourceLocation.tryParse(string)).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + string + "'"));
        if (item == Items.AIR) {
            throw new JsonSyntaxException("Empty ingredient not allowed here");
        } else {
            return item;
        }
    }

    public static class Serializer implements RecipeSerializer<ShapedBackpackRecipe> {
        public Serializer() {}

        @Override
        public ShapedBackpackRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            String string = GsonHelper.getAsString(json, "group", "");
            CraftingBookCategory craftingBookCategory = (CraftingBookCategory)CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(json, "category", (String)null), CraftingBookCategory.MISC);
            Map<String, Ingredient> map = ShapedBackpackRecipe.keyFromJson(GsonHelper.getAsJsonObject(json, "key"));
            String[] strings = ShapedBackpackRecipe.shrink(ShapedBackpackRecipe.patternFromJson(GsonHelper.getAsJsonArray(json, "pattern")));
            int i = strings[0].length();
            int j = strings.length;
            NonNullList<Ingredient> nonNullList = ShapedBackpackRecipe.dissolvePattern(strings, map, i, j);
            ItemStack itemStack = ShapedBackpackRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            boolean bl = GsonHelper.getAsBoolean(json, "show_notification", true);
            return new ShapedBackpackRecipe(recipeId, string, craftingBookCategory, i, j, nonNullList, itemStack, bl);
        }

        @Override
        public ShapedBackpackRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int i = buffer.readVarInt();
            int j = buffer.readVarInt();
            String string = buffer.readUtf();
            CraftingBookCategory craftingBookCategory = (CraftingBookCategory)buffer.readEnum(CraftingBookCategory.class);
            NonNullList<Ingredient> nonNullList = NonNullList.withSize(i * j, Ingredient.EMPTY);

            for(int k = 0; k < nonNullList.size(); ++k) {
                nonNullList.set(k, Ingredient.fromNetwork(buffer));
            }

            ItemStack itemStack = buffer.readItem();
            boolean bl = buffer.readBoolean();
            return new ShapedBackpackRecipe(recipeId, string, craftingBookCategory, i, j, nonNullList, itemStack, bl);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ShapedBackpackRecipe recipe) {
            buffer.writeVarInt(recipe.width);
            buffer.writeVarInt(recipe.height);
            buffer.writeUtf(recipe.group);
            buffer.writeEnum(recipe.category);

            for(Ingredient ingredient : recipe.recipeItems) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.result);
            buffer.writeBoolean(recipe.showNotification);
        }
    }
}