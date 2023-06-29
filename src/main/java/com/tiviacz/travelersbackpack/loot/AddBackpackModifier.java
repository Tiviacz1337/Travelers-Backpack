package com.tiviacz.travelersbackpack.loot;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class AddBackpackModifier extends LootModifier
{
    public static final Supplier<Codec<AddBackpackModifier>> CODEC = Suppliers.memoize(()
    -> RecordCodecBuilder.create(inst -> codecStart(inst).and(ForgeRegistries.ITEMS.getCodec()
            .fieldOf("item").forGetter(m -> m.item)).apply(inst, AddBackpackModifier::new)));

    private final Item item;

    protected AddBackpackModifier(LootItemCondition[] conditionsIn, Item item)
    {
        super(conditionsIn);
        this.item = item;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
    {
        if(!TravelersBackpackConfig.enableLoot) return generatedLoot;

        if(item == ModItems.BAT_TRAVELERS_BACKPACK.get() && context.getRandom().nextFloat() >= 0.05F)
        {
            generatedLoot.add(new ItemStack(item));
        }
        if(item == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get() && context.getRandom().nextFloat() >= 0.1F)
        {
            generatedLoot.add(new ItemStack(item));
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec()
    {
        return CODEC.get();
    }
}