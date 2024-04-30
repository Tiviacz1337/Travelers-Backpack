package com.tiviacz.travelersbackpack.common.recipes;

import com.google.gson.JsonObject;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModRecipeSerializers;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.SettingsManager;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BackpackUpgradeRecipe extends UpgradeRecipe
{
    final Ingredient base;
    final Ingredient addition;
    final ItemStack result;

    public BackpackUpgradeRecipe(ResourceLocation id, Ingredient base, Ingredient addition, ItemStack result)
    {
        super(id, base, addition, result);

        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public ItemStack assemble(Container container)
    {
        ItemStack itemstack = this.result.copy();
        CompoundTag compoundtag = container.getItem(0).getTag();
        if(compoundtag != null)
        {
            compoundtag = compoundtag.copy();

            if(container.getItem(1).is(ModItems.CRAFTING_UPGRADE.get()))
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

                if(container.getItem(1).is(Tiers.of(compoundtag.getInt(ITravelersBackpackContainer.TIER)).getTierUpgradeIngredient()))
                {
                    upgradeInventory(compoundtag, tier);
                    itemstack.setTag(compoundtag.copy());
                    return itemstack;
                }
            }
            else
            {
                if(container.getItem(1).is(Tiers.LEATHER.getTierUpgradeIngredient()))
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
        ItemStack addition = container.getItem(1);
        boolean flag = true;

        if(!TravelersBackpackConfig.enableCraftingUpgrade)
        {
            flag = !addition.is(ModItems.CRAFTING_UPGRADE.get());
        }
        if(!TravelersBackpackConfig.enableTierUpgrades)
        {
            flag = !(addition.is(ModItems.IRON_TIER_UPGRADE.get()) || addition.is(ModItems.GOLD_TIER_UPGRADE.get())
                    || addition.is(ModItems.DIAMOND_TIER_UPGRADE.get()) || addition.is(ModItems.NETHERITE_TIER_UPGRADE.get()));
        }
        return matchesTier(container, level) && flag && super.matches(container, level);
    }

    public boolean matchesTier(Container container, Level level)
    {
        ItemStack base = container.getItem(0);
        ItemStack addition = container.getItem(1);

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

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<BackpackUpgradeRecipe>
    {
        public BackpackUpgradeRecipe fromJson(ResourceLocation p_267011_, JsonObject p_267297_) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(p_267297_, "base"));
            Ingredient ingredient1 = Ingredient.fromJson(GsonHelper.getAsJsonObject(p_267297_, "addition"));
            ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(p_267297_, "result"));
            return new BackpackUpgradeRecipe(p_267011_, ingredient, ingredient1, itemstack);
        }

        public BackpackUpgradeRecipe fromNetwork(ResourceLocation p_266671_, FriendlyByteBuf p_266826_) {
            Ingredient ingredient = Ingredient.fromNetwork(p_266826_);
            Ingredient ingredient1 = Ingredient.fromNetwork(p_266826_);
            ItemStack itemstack = p_266826_.readItem();
            return new BackpackUpgradeRecipe(p_266671_, ingredient, ingredient1, itemstack);
        }

        public void toNetwork(FriendlyByteBuf p_266918_, BackpackUpgradeRecipe p_266728_) {
            p_266728_.base.toNetwork(p_266918_);
            p_266728_.addition.toNetwork(p_266918_);
            p_266918_.writeItem(p_266728_.result);
        }
    }
}