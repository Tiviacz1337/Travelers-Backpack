package com.tiviacz.travelersbackpack.loot;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.component.RenderInfo;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddItemModifier extends LootModifier {
    public static final Supplier<MapCodec<AddItemModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder
            .mapCodec(inst -> codecStart(inst).and(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item))
                    .apply(inst, AddItemModifier::new)));

    private final Item item;

    public AddItemModifier(LootItemCondition[] condition, int priority, Item item) {
        super(condition, priority);
        this.item = item;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if(!TravelersBackpackConfig.COMMON.enableLoot.get()) return generatedLoot;

        if(this.item == ModItems.STANDARD_TRAVELERS_BACKPACK.get()) { //Standard
            generatedLoot.add(randomDyedBackpackLoot(this.item, context.getRandom()));
        } else if(this.item instanceof TravelersBackpackItem) { //Custom
            generatedLoot.add(withTanksUpgrade(this.item));
        } else { //Tier Upgrades
            generatedLoot.add(new ItemStack(this.item));
        }
        return generatedLoot;
    }

    public ItemStack withTanksUpgrade(Item item) {
        ItemStack stack = item.getDefaultInstance();
        stack.set(ModDataComponents.STARTER_UPGRADES, ItemContainerContents.fromItems(List.of(ModItems.TANKS_UPGRADE.toStack())));
        return stack;
    }

    public ItemStack randomDyedBackpackLoot(Item item, RandomSource random) {
        ItemStack stack = item.getDefaultInstance();
        float chance = random.nextFloat();
        //66% chance for a dyed backpack
        if(chance <= 0.66F) {
            int color = random.nextInt(16);
            stack.set(DataComponents.DYED_COLOR, new DyedItemColor(DyeColor.byId(color).getTextureDiffuseColor()));
        }
        stack.set(ModDataComponents.RENDER_INFO, RenderInfo.EMPTY);
        return stack;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}