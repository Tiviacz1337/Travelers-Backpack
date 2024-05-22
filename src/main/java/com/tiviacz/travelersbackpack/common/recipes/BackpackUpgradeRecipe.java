package com.tiviacz.travelersbackpack.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModRecipeSerializers;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.SettingsManager;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BackpackUpgradeRecipe extends SmithingTransformRecipe
{
    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;
    final ItemStack result;

    public BackpackUpgradeRecipe(Ingredient pTemplate, Ingredient pBase, Ingredient pAddition, ItemStack pResult)
    {
        super(pTemplate, pBase, pAddition, pResult);

        this.template = pTemplate;
        this.base = pBase;
        this.addition = pAddition;
        this.result = pResult;
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess)
    {
        ItemStack itemstack = this.result.copy();
        CompoundTag compoundtag = pContainer.getItem(1).getTag();

        if(compoundtag != null)
        {
            compoundtag = compoundtag.copy();

            if(pContainer.getItem(2).is(ModItems.CRAFTING_UPGRADE.get()))
            {
                if(compoundtag.contains(SettingsManager.CRAFTING_SETTINGS))
                {
                    if(compoundtag.getByteArray(SettingsManager.CRAFTING_SETTINGS)[0] == (byte)0)
                    {
                        byte[] newArray = new byte[]{(byte)1, (byte)0, (byte)1};
                        compoundtag.putByteArray(SettingsManager.CRAFTING_SETTINGS, newArray);

                        itemstack.setTag(compoundtag);
                        return itemstack;
                    }
                }
                else
                {
                    byte[] newArray = new byte[]{(byte)1, (byte)0, (byte)1};
                    compoundtag.putByteArray(SettingsManager.CRAFTING_SETTINGS, newArray);

                    itemstack.setTag(compoundtag);
                    return itemstack;
                }
            }

            if(compoundtag.contains(ITravelersBackpackContainer.TIER))
            {
                Tiers.Tier tier = Tiers.of(compoundtag.getInt(ITravelersBackpackContainer.TIER));

                if(pContainer.getItem(2).is(Tiers.of(compoundtag.getInt(ITravelersBackpackContainer.TIER)).getTierUpgradeIngredient()))
                {
                    upgradeInventory(compoundtag, tier);
                    itemstack.setTag(compoundtag.copy());
                    return itemstack;
                }
            }
            else
            {
                if(pContainer.getItem(2).is(Tiers.LEATHER.getTierUpgradeIngredient()))
                {
                    upgradeInventory(compoundtag, Tiers.LEATHER);
                    itemstack.setTag(compoundtag.copy());
                    return itemstack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public void upgradeInventory(CompoundTag compound, Tiers.Tier tier)
    {
        compound.putInt(ITravelersBackpackContainer.TIER, tier.getNextTier().getOrdinal());

        if(compound.contains(ITravelersBackpackContainer.TOOLS_INVENTORY))
        {
            if(compound.getCompound(ITravelersBackpackContainer.TOOLS_INVENTORY).contains("Size", Tag.TAG_INT))
            {
                compound.getCompound(ITravelersBackpackContainer.TOOLS_INVENTORY).putInt("Size", tier.getNextTier().getToolSlots());
            }
        }

        if(compound.contains(ITravelersBackpackContainer.INVENTORY))
        {
            if(compound.getCompound(ITravelersBackpackContainer.INVENTORY).contains("Size", Tag.TAG_INT))
            {
                compound.getCompound(ITravelersBackpackContainer.INVENTORY).putInt("Size", tier.getNextTier().getStorageSlots());
            }
        }

        if(compound.contains(ITravelersBackpackContainer.LEFT_TANK))
        {
            if(compound.getCompound(ITravelersBackpackContainer.LEFT_TANK).contains("Capacity", Tag.TAG_INT))
            {
                compound.getCompound(ITravelersBackpackContainer.LEFT_TANK).putInt("Capacity", tier.getNextTier().getTankCapacity());
            }
        }

        if(compound.contains(ITravelersBackpackContainer.RIGHT_TANK))
        {
            if(compound.getCompound(ITravelersBackpackContainer.RIGHT_TANK).contains("Capacity", Tag.TAG_INT))
            {
                compound.getCompound(ITravelersBackpackContainer.RIGHT_TANK).putInt("Capacity", tier.getNextTier().getTankCapacity());
            }
        }
    }

    @Override
    public boolean matches(Container container, Level level)
    {
        ItemStack addition = container.getItem(2);
        boolean flag = true;

        if(!TravelersBackpackConfig.SERVER.backpackSettings.craftingUpgrade.enableCraftingUpgrade.get())
        {
            flag = !addition.is(ModItems.CRAFTING_UPGRADE.get());
        }
        if(!TravelersBackpackConfig.SERVER.backpackSettings.enableTierUpgrades.get())
        {
            flag = !(addition.is(ModItems.IRON_TIER_UPGRADE.get()) || addition.is(ModItems.GOLD_TIER_UPGRADE.get())
                    || addition.is(ModItems.DIAMOND_TIER_UPGRADE.get()) || addition.is(ModItems.NETHERITE_TIER_UPGRADE.get()));
        }
        return matchesTier(container, level) && flag && super.matches(container, level);
    }

    public boolean matchesTier(Container container, Level level)
    {
        ItemStack base = container.getItem(1);
        ItemStack addition = container.getItem(2);

        if(addition.getItem() == ModItems.CRAFTING_UPGRADE.get())
        {
            return true;
        }

        if(!base.hasTag() || !base.getTag().contains(ITravelersBackpackContainer.TIER))
        {
            return addition.is(ModItems.IRON_TIER_UPGRADE.get());
        }

        if(base.getTag().contains(ITravelersBackpackContainer.TIER))
        {
            int tier = base.getTag().getInt(ITravelersBackpackContainer.TIER);

            return switch(tier)
            {
                case 0 -> addition.getItem() == ModItems.IRON_TIER_UPGRADE.get();
                case 1 -> addition.getItem() == ModItems.GOLD_TIER_UPGRADE.get();
                case 2 -> addition.getItem() == ModItems.DIAMOND_TIER_UPGRADE.get();
                case 3 -> addition.getItem() == ModItems.NETHERITE_TIER_UPGRADE.get();
                default -> false;
            };
        }
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ModRecipeSerializers.BACKPACK_UPGRADE.get();
    }

    public static class Serializer implements RecipeSerializer<BackpackUpgradeRecipe> {
        private static final Codec<BackpackUpgradeRecipe> backpackUpgradeRecipeCodec = RecordCodecBuilder.create((p_301330_) -> {
            return p_301330_.group(Ingredient.CODEC.fieldOf("template").forGetter((p_297231_) -> {
                return p_297231_.template;
            }), Ingredient.CODEC.fieldOf("base").forGetter((p_298250_) -> {
                return p_298250_.base;
            }), Ingredient.CODEC.fieldOf("addition").forGetter((p_299654_) -> {
                return p_299654_.addition;
            }), ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter((p_297480_) -> {
                return p_297480_.result;
            })).apply(p_301330_, BackpackUpgradeRecipe::new);
        });

        @Override
        public Codec<BackpackUpgradeRecipe> codec() {
            return backpackUpgradeRecipeCodec;
        }

        @Override
        public @Nullable BackpackUpgradeRecipe fromNetwork(FriendlyByteBuf pBuffer) {
            Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
            Ingredient ingredient1 = Ingredient.fromNetwork(pBuffer);
            Ingredient ingredient2 = Ingredient.fromNetwork(pBuffer);
            ItemStack itemstack = pBuffer.readItem();
            return new BackpackUpgradeRecipe(ingredient, ingredient1, ingredient2, itemstack);
        }

        public void toNetwork(FriendlyByteBuf p_266746_, BackpackUpgradeRecipe p_266927_) {
            p_266927_.template.toNetwork(p_266746_);
            p_266927_.base.toNetwork(p_266746_);
            p_266927_.addition.toNetwork(p_266746_);
            p_266746_.writeItem(p_266927_.result);
        }
    }
}