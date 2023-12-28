package com.tiviacz.travelersbackpack.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModCrafting;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingTransformRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.World;

public class BackpackUpgradeRecipe extends SmithingTransformRecipe
{
    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;
    final ItemStack result;

    public BackpackUpgradeRecipe(Ingredient template, Ingredient base, Ingredient addition, ItemStack result)
    {
        super(template, base, addition, result);

        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager)
    {
        ItemStack itemstack = this.result.copy();
        NbtCompound nbtCompound = inventory.getStack(1).getNbt();

        if(nbtCompound != null)
        {
            nbtCompound = nbtCompound.copy();

            if(nbtCompound.contains(Tiers.TIER))
            {
                Tiers.Tier tier = Tiers.of(nbtCompound.getInt(Tiers.TIER));
                if(this.addition.test(Tiers.of(nbtCompound.getInt(Tiers.TIER)).getTierUpgradeIngredient().getDefaultStack()))
                {
                    nbtCompound.putInt(Tiers.TIER, tier.getNextTier().getOrdinal());
                    itemstack.setNbt(nbtCompound.copy());
                    return itemstack;
                }
            }
            else
            {
                if(this.addition.test(Tiers.LEATHER.getTierUpgradeIngredient().getDefaultStack()))
                {
                    nbtCompound.putInt(Tiers.TIER, Tiers.LEATHER.getNextTier().getOrdinal());
                    itemstack.setNbt(nbtCompound.copy());
                    return itemstack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(Inventory inventory, World world)
    {
        return TravelersBackpackConfig.enableTierUpgrades && super.matches(inventory, world);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ModCrafting.BACKPACK_UPGRADE;
    }

    public static class Serializer implements RecipeSerializer<BackpackUpgradeRecipe> {
        private static final Codec<BackpackUpgradeRecipe> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Ingredient.ALLOW_EMPTY_CODEC.fieldOf("template").forGetter((recipe) -> {
                return recipe.template;
            }), Ingredient.ALLOW_EMPTY_CODEC.fieldOf("base").forGetter((recipe) -> {
                return recipe.base;
            }), Ingredient.ALLOW_EMPTY_CODEC.fieldOf("addition").forGetter((recipe) -> {
                return recipe.addition;
            }), ItemStack.RECIPE_RESULT_CODEC.fieldOf("result").forGetter((recipe) -> {
                return recipe.result;
            })).apply(instance, BackpackUpgradeRecipe::new);
        });

        public Serializer() {
        }

        public Codec<BackpackUpgradeRecipe> codec() {
            return CODEC;
        }

        public BackpackUpgradeRecipe read(PacketByteBuf packetByteBuf) {
            Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
            Ingredient ingredient2 = Ingredient.fromPacket(packetByteBuf);
            Ingredient ingredient3 = Ingredient.fromPacket(packetByteBuf);
            ItemStack itemStack = packetByteBuf.readItemStack();
            return new BackpackUpgradeRecipe(ingredient, ingredient2, ingredient3, itemStack);
        }

        public void write(PacketByteBuf packetByteBuf, BackpackUpgradeRecipe backpackUpgradeRecipe) {
            backpackUpgradeRecipe.template.write(packetByteBuf);
            backpackUpgradeRecipe.base.write(packetByteBuf);
            backpackUpgradeRecipe.addition.write(packetByteBuf);
            packetByteBuf.writeItemStack(backpackUpgradeRecipe.result);
        }
    }
}