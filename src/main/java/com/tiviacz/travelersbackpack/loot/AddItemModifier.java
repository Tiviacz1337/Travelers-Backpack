package com.tiviacz.travelersbackpack.loot;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddItemModifier extends LootModifier {
    public static final Supplier<Codec<AddItemModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder
            .create(inst -> codecStart(inst).and(ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(m -> m.item))
                    .and(Codec.FLOAT.fieldOf("weight").forGetter(m -> m.weight))
                    .apply(inst, AddItemModifier::new)));

    private final Item item;
    private final float weight;

    protected AddItemModifier(LootItemCondition[] conditionsIn, Item item, float weight) {
        super(conditionsIn);
        this.item = item;
        this.weight = weight;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if(!TravelersBackpackConfig.COMMON.enableLoot.get()) return generatedLoot;

        if(context.getQueriedLootTableId().equals(BuiltInLootTables.ABANDONED_MINESHAFT)) {
            if(this.item == ModItems.BAT_TRAVELERS_BACKPACK.get() && context.getRandom().nextFloat() <= this.weight) {
                generatedLoot.add(withTanksUpgrade(item));
            }

            if(this.item == ModItems.STANDARD_TRAVELERS_BACKPACK.get() && context.getRandom().nextFloat() <= this.weight) {
                generatedLoot.add(withTanksUpgrade(item));
            }

            if(this.item == ModItems.IRON_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= this.weight) {
                generatedLoot.add(new ItemStack(item));
            }

            if(this.item == ModItems.GOLD_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= this.weight) {
                generatedLoot.add(new ItemStack(item));
            }
        }

        if(context.getQueriedLootTableId().equals(BuiltInLootTables.VILLAGE_ARMORER)) {
            if(item == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get() && context.getRandom().nextFloat() <= this.weight) {
                generatedLoot.add(withTanksUpgrade(item));
            }
        }

        if(context.getQueriedLootTableId().equals(BuiltInLootTables.SIMPLE_DUNGEON)) {
            if(item == ModItems.STANDARD_TRAVELERS_BACKPACK.get() && context.getRandom().nextFloat() <= this.weight) {
                generatedLoot.add(withTanksUpgrade(item));
            }

            if(this.item == ModItems.IRON_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= this.weight) {
                generatedLoot.add(new ItemStack(item));
            }
        }

        if(context.getQueriedLootTableId().equals(BuiltInLootTables.DESERT_PYRAMID)) {
            if(item == ModItems.STANDARD_TRAVELERS_BACKPACK.get() && context.getRandom().nextFloat() <= this.weight) {
                generatedLoot.add(withTanksUpgrade(item));
            }

            if(this.item == ModItems.IRON_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= this.weight) {
                generatedLoot.add(new ItemStack(item));
            }

            if(this.item == ModItems.GOLD_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= this.weight) {
                generatedLoot.add(new ItemStack(item));
            }
        }

        if(context.getQueriedLootTableId().equals(BuiltInLootTables.SHIPWRECK_TREASURE)) {
            if(this.item == ModItems.IRON_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= this.weight) {
                generatedLoot.add(new ItemStack(item));
            }

            if(this.item == ModItems.GOLD_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= this.weight) {
                generatedLoot.add(new ItemStack(item));
            }
        }

        if(context.getQueriedLootTableId().equals(BuiltInLootTables.WOODLAND_MANSION)) {
            if(this.item == ModItems.IRON_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= this.weight) {
                generatedLoot.add(new ItemStack(item));
            }

            if(this.item == ModItems.GOLD_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= this.weight) {
                generatedLoot.add(new ItemStack(item));
            }
        }

        if(context.getQueriedLootTableId().equals(BuiltInLootTables.NETHER_BRIDGE)) {
            if(this.item == ModItems.IRON_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= this.weight) {
                generatedLoot.add(new ItemStack(item));
            }

            if(this.item == ModItems.GOLD_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= this.weight) {
                generatedLoot.add(new ItemStack(item));
            }
        }

        if(context.getQueriedLootTableId().equals(BuiltInLootTables.BASTION_TREASURE)) {
            if(this.item == ModItems.IRON_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= this.weight) {
                generatedLoot.add(new ItemStack(item));
            }

            if(this.item == ModItems.GOLD_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= this.weight) {
                generatedLoot.add(new ItemStack(item));
            }
        }

        if(context.getQueriedLootTableId().equals(BuiltInLootTables.END_CITY_TREASURE)) {
            if(this.item == ModItems.GOLD_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= this.weight) {
                generatedLoot.add(new ItemStack(item));
            }

            if(this.item == ModItems.DIAMOND_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= this.weight) {
                generatedLoot.add(new ItemStack(item));
            }
        }
        return generatedLoot;
    }

    public ItemStack withTanksUpgrade(Item item) {
        ItemStack stack = item.getDefaultInstance();
        NbtHelper.set(stack, ModDataHelper.STARTER_UPGRADES, List.of(ModItems.TANKS_UPGRADE.get().getDefaultInstance()));
        return stack;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}