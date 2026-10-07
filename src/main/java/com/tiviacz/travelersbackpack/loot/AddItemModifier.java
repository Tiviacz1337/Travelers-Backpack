package com.tiviacz.travelersbackpack.loot;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.common.recipes.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
                    .apply(inst, AddItemModifier::new)));

    private final Item item;

    public AddItemModifier(LootItemCondition[] conditionsIn, Item item) {
        super(conditionsIn);
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
        NbtHelper.set(stack, ModDataHelper.STARTER_UPGRADES, List.of(ModItems.TANKS_UPGRADE.get().getDefaultInstance()));
        return stack;
    }

    public ItemStack randomDyedBackpackLoot(Item item, RandomSource random) {
        ItemStack stack = item.getDefaultInstance();
        float chance = random.nextFloat();
        //66% chance for a dyed backpack
        if(chance <= 0.66F) {
            int color = random.nextInt(16);
            BackpackDyeRecipe.setColor(stack, color);
        }
        NbtHelper.set(stack, ModDataHelper.RENDER_INFO, RenderInfo.EMPTY);
        return stack;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}