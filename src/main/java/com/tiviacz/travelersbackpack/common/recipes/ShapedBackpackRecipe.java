package com.tiviacz.travelersbackpack.common.recipes;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.block.SleepingBagBlock;
import com.tiviacz.travelersbackpack.compat.comforts.ComfortsCompat;
import com.tiviacz.travelersbackpack.component.RenderInfo;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.item.upgrade.TanksUpgradeItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class ShapedBackpackRecipe extends NormalCraftingRecipe {
    public static final MapCodec<ShapedBackpackRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                            Recipe.CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
                            CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(o -> o.bookInfo),
                            ShapedRecipePattern.MAP_CODEC.forGetter(o -> o.pattern),
                            ItemStackTemplate.CODEC.fieldOf("result").forGetter(o -> o.result)
                    )
                    .apply(i, ShapedBackpackRecipe::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ShapedBackpackRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC,
            o -> o.commonInfo,
            CraftingRecipe.CraftingBookInfo.STREAM_CODEC,
            o -> o.bookInfo,
            ShapedRecipePattern.STREAM_CODEC,
            o -> o.pattern,
            ItemStackTemplate.STREAM_CODEC,
            o -> o.result,
            ShapedBackpackRecipe::new
    );
    public static final RecipeSerializer<ShapedBackpackRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);
    public final ShapedRecipePattern pattern;
    public final ItemStackTemplate result;

    public ShapedBackpackRecipe(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo bookInfo, ShapedRecipePattern pattern, ItemStackTemplate result) {
        super(commonInfo, bookInfo);
        this.pattern = pattern;
        this.result = result;
    }

    @Override
    public ItemStack assemble(CraftingInput pInput) {
        ItemStack output = this.result.create();

        if(!output.isEmpty()) {
            boolean hasTanks = false;
            boolean hasSleepingBag = false;
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
                    hasSleepingBag = true;
                }

                if(!hasTanks && ingredient.getItem() == ModItems.BACKPACK_TANK.get()) {
                    output.set(ModDataComponents.STARTER_UPGRADES, ItemContainerContents.fromItems(List.of(ModItems.TANKS_UPGRADE.toStack())));
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
                if(!hasSleepingBag) {
                    output.set(ModDataComponents.SLEEPING_BAG_COLOR, -1);
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

    @VisibleForTesting
    public List<Optional<Ingredient>> getIngredients() {
        return this.pattern.ingredients();
    }

    @Override
    protected PlacementInfo createPlacementInfo() {
        return PlacementInfo.createFromOptionals(this.pattern.ingredients());
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return this.pattern.matches(input);
    }

    public int getWidth() {
        return this.pattern.width();
    }

    public int getHeight() {
        return this.pattern.height();
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(
                new ShapedCraftingRecipeDisplay(
                        this.pattern.width(),
                        this.pattern.height(),
                        this.pattern.ingredients().stream().map(e -> e.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE)).toList(),
                        new SlotDisplay.ItemStackSlotDisplay(this.result),
                        new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
                )
        );
    }

    @Override
    public RecipeSerializer<ShapedBackpackRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<CraftingRecipe> getType() {
        return RecipeType.CRAFTING;
    }
}