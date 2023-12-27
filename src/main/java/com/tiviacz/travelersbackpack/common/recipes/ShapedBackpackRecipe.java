package com.tiviacz.travelersbackpack.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
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
                    output.getOrCreateTag().putInt("SleepingBagColor", getProperColor((SleepingBagItem) ingredient.getItem()));
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

    /*    static final Codec<List<String>> f_291059_ = Codec.STRING.listOf().flatXmap((p_297814_) -> {
            if (p_297814_.size() > 3) {
                return DataResult.error(() -> {
                    return "Invalid pattern: too many rows, " + 3 + " is maximum";
                });
            } else if (p_297814_.isEmpty()) {
                return DataResult.error(() -> {
                    return "Invalid pattern: empty pattern not allowed";
                });
            } else {
                int i = p_297814_.get(0).length();

                for(String s : p_297814_) {
                    if (s.length() > 3) {
                        return DataResult.error(() -> {
                            return "Invalid pattern: too many columns, " + 3  + " is maximum";
                        });
                    }

                    if (i != s.length()) {
                        return DataResult.error(() -> {
                            return "Invalid pattern: each row must be the same width";
                        });
                    }
                }

                return DataResult.success(p_297814_);
            }
        }, DataResult::success);
        static final Codec<String> f_291502_ = Codec.STRING.flatXmap((p_301277_) -> {
            if (p_301277_.length() != 1) {
                return DataResult.error(() -> {
                    return "Invalid key entry: '" + p_301277_ + "' is an invalid symbol (must be 1 character only).";
                });
            } else {
                return " ".equals(p_301277_) ? DataResult.error(() -> {
                    return "Invalid key entry: ' ' is a reserved symbol.";
                }) : DataResult.success(p_301277_);
            }
        }, DataResult::success);
        private static final Codec<ShapedBackpackRecipe> f_291611_ = RawShapedRecipe.f_290916_.flatXmap((p_300056_) -> {
            String[] astring = shrink(p_300056_.f_291411_);
            int i = astring[0].length();
            int j = astring.length;
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);
            Set<String> set = Sets.newHashSet(p_300056_.f_291499_.keySet());

            for(int k = 0; k < astring.length; ++k) {
                String s = astring[k];

                for(int l = 0; l < s.length(); ++l) {
                    String s1 = s.substring(l, l + 1);
                    Ingredient ingredient = s1.equals(" ") ? Ingredient.EMPTY : p_300056_.f_291499_.get(s1);
                    if (ingredient == null) {
                        return DataResult.error(() -> {
                            return "Pattern references symbol '" + s1 + "' but it's not defined in the key";
                        });
                    }

                    set.remove(s1);
                    nonnulllist.set(l + i * k, ingredient);
                }
            }

            if (!set.isEmpty()) {
                return DataResult.error(() -> {
                    return "Key defines symbols that aren't used in pattern: " + set;
                });
            } else {
                ShapedBackpackRecipe shapedrecipe = new ShapedBackpackRecipe(p_300056_.f_291237_, p_300056_.f_291394_, i, j, nonnulllist, p_300056_.f_291431_, p_300056_.f_291143_);
                return DataResult.success(shapedrecipe);
            }
        }, (p_299463_) -> {
            throw new NotImplementedException("Serializing ShapedRecipe is not implemented yet.");
        });

        static record RawShapedRecipe(String f_291237_, CraftingBookCategory f_291394_, Map<String, Ingredient> f_291499_, List<String> f_291411_, ItemStack f_291431_, boolean f_291143_) {
            public static final Codec<RawShapedRecipe> f_290916_ = RecordCodecBuilder.create((p_298430_) -> {
                return p_298430_.group(ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter((p_300105_) -> {
                    return p_300105_.f_291237_;
                }), CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter((p_301213_) -> {
                    return p_301213_.f_291394_;
                }), ExtraCodecs.strictUnboundedMap(Serializer.f_291502_, Ingredient.CODEC_NONEMPTY).fieldOf("key").forGetter((p_297983_) -> {
                    return p_297983_.f_291499_;
                }), Serializer.f_291059_.fieldOf("pattern").forGetter((p_300956_) -> {
                    return p_300956_.f_291411_;
                }), ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter((p_299535_) -> {
                    return p_299535_.f_291431_;
                }), ExtraCodecs.strictOptionalField(Codec.BOOL, "show_notification", true).forGetter((p_297368_) -> {
                    return p_297368_.f_291143_;
                })).apply(p_298430_, Serializer.RawShapedRecipe::new);
            });
        }

        static String[] shrink(List<String> p_299210_) {
            int i = Integer.MAX_VALUE;
            int j = 0;
            int k = 0;
            int l = 0;

            for(int i1 = 0; i1 < p_299210_.size(); ++i1) {
                String s = p_299210_.get(i1);
                i = Math.min(i, firstNonSpace(s));
                int j1 = lastNonSpace(s);
                j = Math.max(j, j1);
                if (j1 < 0) {
                    if (k == i1) {
                        ++k;
                    }

                    ++l;
                } else {
                    l = 0;
                }
            }

            if (p_299210_.size() == l) {
                return new String[0];
            } else {
                String[] astring = new String[p_299210_.size() - l - k];

                for(int k1 = 0; k1 < astring.length; ++k1) {
                    astring[k1] = p_299210_.get(k1 + k).substring(i, j + 1);
                }

                return astring;
            }
        }

        private static int firstNonSpace(String pEntry) {
            int i;
            for(i = 0; i < pEntry.length() && pEntry.charAt(i) == ' '; ++i) {
            }

            return i;
        }

        private static int lastNonSpace(String pEntry) {
            int i;
            for(i = pEntry.length() - 1; i >= 0 && pEntry.charAt(i) == ' '; --i) {
            }

            return i;
        } */

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