package com.tiviacz.travelersbackpackold.common.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpackold.components.BackpackContainerComponent;
import com.tiviacz.travelersbackpackold.components.FluidTanks;
import com.tiviacz.travelersbackpackold.components.Settings;
import com.tiviacz.travelersbackpackold.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpackneo.init.ModItems;
import com.tiviacz.travelersbackpackneo.init.ModRecipeSerializers;
import com.tiviacz.travelersbackpackold.inventory.Tiers;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingTransformRecipe;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

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
    public ItemStack craft(SmithingRecipeInput smithingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup)
    {
        ItemStack result = smithingRecipeInput.getStackInSlot(1).copyComponentsToNewStack(this.result.getItem(), this.result.getCount());
        result.applyUnvalidatedChanges(this.result.getComponentChanges());

        ItemStack base = smithingRecipeInput.getStackInSlot(1);
        ItemStack addition = smithingRecipeInput.getStackInSlot(2);

        int tier = base.getOrDefault(ModDataComponents.TIER, 0);

        if(addition.isOf(Tiers.of(tier).getTierUpgradeIngredient()))
        {
            upgradeInventory(result, Tiers.of(tier).getNextTier());
            return result;
        }

        if(addition.isOf(ModItems.CRAFTING_UPGRADE))
        {
            if(base.contains(ModDataComponents.SETTINGS))
            {
                List<List<Byte>> oldSettings = base.get(ModDataComponents.SETTINGS);
                List<Byte> craftingSettings = oldSettings.get(0);
                if(craftingSettings.get(0) == (byte)0)
                {
                    List<Byte> newCraftingSettings = Arrays.asList((byte)1, craftingSettings.get(1), craftingSettings.get(2));
                    List<List<Byte>> newSettings = Arrays.asList(newCraftingSettings, oldSettings.get(1));
                    result.set(ModDataComponents.SETTINGS, newSettings);
                    return result;
                }
            }
            else
            {
                List<Byte> newCraftingSettings = Arrays.asList((byte)1, (byte)0, (byte)1);
                List<List<Byte>> newSettings = Settings.createSettings(newCraftingSettings, Settings.createDefaultToolSettings());
                result.set(ModDataComponents.SETTINGS, newSettings);
                return result;
            }
        }
        return ItemStack.EMPTY;
      /* ItemStack itemstack = this.result.copy();
        NbtCompound nbtCompound = inventory.getStack(1).getNbt();

        if(nbtCompound != null)
        {
            nbtCompound = nbtCompound.copy();

            if(inventory.getStack(2).isOf(ModItems.CRAFTING_UPGRADE))
            {
                if(nbtCompound.contains(SettingsManager.CRAFTING_SETTINGS))
                {
                    if(nbtCompound.getByteArray(SettingsManager.CRAFTING_SETTINGS)[0] == (byte)0)
                    {
                        byte[] newArray = new byte[]{(byte)1, (byte)0, (byte)1};
                        nbtCompound.putByteArray(SettingsManager.CRAFTING_SETTINGS, newArray);

                        itemstack.setNbt(nbtCompound);
                        return itemstack;
                    }
                }
                else
                {
                    byte[] newArray = new byte[]{(byte)1, (byte)0, (byte)1};
                    nbtCompound.putByteArray(SettingsManager.CRAFTING_SETTINGS, newArray);

                    itemstack.setNbt(nbtCompound);
                    return itemstack;
                }
            }

            if(nbtCompound.contains(ITravelersBackpackInventory.TIER))
            {
                Tiers.Tier tier = Tiers.of(nbtCompound.getInt(ITravelersBackpackInventory.TIER));

                if(inventory.getStack(2).isOf(Tiers.of(nbtCompound.getInt(ITravelersBackpackInventory.TIER)).getTierUpgradeIngredient()))
                {
                    upgradeInventory(nbtCompound, tier);
                    itemstack.setNbt(nbtCompound.copy());
                    return itemstack;
                }
            }
            else
            {
                if(inventory.getStack(2).isOf(Tiers.LEATHER.getTierUpgradeIngredient()))
                {
                    upgradeInventory(nbtCompound, Tiers.LEATHER);
                    itemstack.setNbt(nbtCompound.copy());
                    return itemstack;
                }
            }
        }
        return ItemStack.EMPTY; */
    }

    public void upgradeInventory(ItemStack stack, Tiers.Tier nextTier)
    {
        //Tier
        stack.set(ModDataComponents.TIER, nextTier.getOrdinal());

        //Inventory
        DefaultedList<ItemStack> oldContents = stack.getOrDefault(ModDataComponents.BACKPACK_CONTAINER, BackpackContainerComponent.fromStacks(nextTier.getStorageSlots(), DefaultedList.ofSize(nextTier.getStorageSlots(), ItemStack.EMPTY))).getStacks();
        BackpackContainerComponent newContents = BackpackContainerComponent.upgradeContents(nextTier.getStorageSlots(), oldContents);
        stack.set(ModDataComponents.BACKPACK_CONTAINER, newContents);

        //Tools
        DefaultedList<ItemStack> oldTools = stack.getOrDefault(ModDataComponents.TOOLS_CONTAINER, BackpackContainerComponent.fromStacks(nextTier.getToolSlots(), DefaultedList.ofSize(nextTier.getToolSlots(), ItemStack.EMPTY))).getStacks();
        BackpackContainerComponent newTools = BackpackContainerComponent.upgradeContents(nextTier.getToolSlots(), oldTools);
        stack.set(ModDataComponents.TOOLS_CONTAINER, newTools);

        //Tanks
        FluidTanks oldTanks = stack.getOrDefault(ModDataComponents.FLUID_TANKS, FluidTanks.createTanks(nextTier.getTankCapacity()));
        FluidTanks newTanks = new FluidTanks(nextTier.getTankCapacity(), new FluidTanks.Tank(oldTanks.leftTank().fluidVariant(), oldTanks.leftTank().amount()), new FluidTanks.Tank(oldTanks.rightTank().fluidVariant(), oldTanks.rightTank().amount()));
        stack.set(ModDataComponents.FLUID_TANKS, newTanks);
    }

   /* public void upgradeInventory(NbtCompound compound, Tiers.Tier tier)
    {
        compound.putInt(ITravelersBackpackInventory.TIER, tier.getNextTier().getOrdinal());

        if(compound.contains(ITravelersBackpackInventory.TOOLS_INVENTORY))
        {
            if(compound.getCompound(ITravelersBackpackInventory.TOOLS_INVENTORY).contains("Size", NbtElement.INT_TYPE))
            {
                compound.getCompound(ITravelersBackpackInventory.TOOLS_INVENTORY).putInt("Size", tier.getNextTier().getToolSlots());
            }
        }

        if(compound.contains(ITravelersBackpackInventory.INVENTORY))
        {
            if(compound.getCompound(ITravelersBackpackInventory.INVENTORY).contains("Size", NbtElement.INT_TYPE))
            {
                compound.getCompound(ITravelersBackpackInventory.INVENTORY).putInt("Size", tier.getNextTier().getStorageSlots());
            }
        }

        if(compound.contains(ITravelersBackpackInventory.LEFT_TANK))
        {
            if(compound.getCompound(ITravelersBackpackInventory.LEFT_TANK).contains("capacity", NbtElement.LONG_TYPE))
            {
                compound.getCompound(ITravelersBackpackInventory.LEFT_TANK).putLong("capacity", tier.getNextTier().getTankCapacity());
            }
        }

        if(compound.contains(ITravelersBackpackInventory.RIGHT_TANK))
        {
            if(compound.getCompound(ITravelersBackpackInventory.RIGHT_TANK).contains("capacity", NbtElement.LONG_TYPE))
            {
                compound.getCompound(ITravelersBackpackInventory.RIGHT_TANK).putLong("capacity", tier.getNextTier().getTankCapacity());
            }
        }
    } */

    @Override
    public boolean matches(SmithingRecipeInput smithingRecipeInput, World world)
    {
        ItemStack addition = smithingRecipeInput.getStackInSlot(2);
        boolean flag = true;

        if(!TravelersBackpackConfig.getConfig().backpackSettings.crafting.enableUpgrade)
        {
            flag = !addition.isOf(ModItems.CRAFTING_UPGRADE);
        }

        if(!TravelersBackpackConfig.getConfig().backpackSettings.enableTierUpgrades)
        {
            flag = !(addition.isOf(ModItems.IRON_TIER_UPGRADE) || addition.isOf(ModItems.GOLD_TIER_UPGRADE)
                    || addition.isOf(ModItems.DIAMOND_TIER_UPGRADE) || addition.isOf(ModItems.NETHERITE_TIER_UPGRADE));
        }
        return /*matchesTier(inventory, world) && */flag && super.matches(smithingRecipeInput, world);
    }

 /*   public boolean matchesTier(Inventory inventory, World world)
    {
        ItemStack base = inventory.getStack(1);
        ItemStack addition = inventory.getStack(2);

        if(addition.getItem() == ModItems.CRAFTING_UPGRADE)
        {
            return true;
        }

        if(!base.hasNbt() || !base.getNbt().contains(ITravelersBackpackInventory.TIER))
        {
            return addition.isOf(ModItems.IRON_TIER_UPGRADE);
        }

        if(base.getNbt().contains(ITravelersBackpackInventory.TIER))
        {
            int tier = base.getNbt().getInt(ITravelersBackpackInventory.TIER);

            return switch(tier)
            {
                case 0 -> addition.getItem() == ModItems.IRON_TIER_UPGRADE;
                case 1 -> addition.getItem() == ModItems.GOLD_TIER_UPGRADE;
                case 2 -> addition.getItem() == ModItems.DIAMOND_TIER_UPGRADE;
                case 3 -> addition.getItem() == ModItems.NETHERITE_TIER_UPGRADE;
                default -> false;
            };
        }
        return false;
    } */

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ModRecipeSerializers.BACKPACK_UPGRADE;
    }

    public static class Serializer implements RecipeSerializer<BackpackUpgradeRecipe>
    {
        private static final MapCodec<BackpackUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(Ingredient.ALLOW_EMPTY_CODEC.fieldOf("template").forGetter(recipe -> recipe.template),
                        Ingredient.ALLOW_EMPTY_CODEC.fieldOf("base").forGetter(recipe -> recipe.base),
                        Ingredient.ALLOW_EMPTY_CODEC.fieldOf("addition").forGetter(recipe -> recipe.addition),
                        ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(recipe -> recipe.result)).apply(instance, BackpackUpgradeRecipe::new));

        public static final PacketCodec<RegistryByteBuf, BackpackUpgradeRecipe> PACKET_CODEC = PacketCodec.ofStatic(BackpackUpgradeRecipe.Serializer::write, BackpackUpgradeRecipe.Serializer::read);

        @Override
        public MapCodec<BackpackUpgradeRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, BackpackUpgradeRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static BackpackUpgradeRecipe read(RegistryByteBuf buf) {
            Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
            Ingredient ingredient2 = Ingredient.PACKET_CODEC.decode(buf);
            Ingredient ingredient3 = Ingredient.PACKET_CODEC.decode(buf);
            ItemStack itemStack = ItemStack.PACKET_CODEC.decode(buf);
            return new BackpackUpgradeRecipe(ingredient, ingredient2, ingredient3, itemStack);
        }

        private static void write(RegistryByteBuf buf, BackpackUpgradeRecipe recipe) {
            Ingredient.PACKET_CODEC.encode(buf, recipe.template);
            Ingredient.PACKET_CODEC.encode(buf, recipe.base);
            Ingredient.PACKET_CODEC.encode(buf, recipe.addition);
            ItemStack.PACKET_CODEC.encode(buf, recipe.result);
        }
    }
}