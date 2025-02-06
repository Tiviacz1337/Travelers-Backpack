package com.tiviacz.travelersbackpack.loot;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.init.ModItems;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HayBackpackLootModifier extends LootModifier {
    public static final Supplier<MapCodec<HayBackpackLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder
            .mapCodec(inst -> codecStart(inst)
                    .and(ItemStack.CODEC.listOf().fieldOf("possibleCropItems").forGetter(m -> m.possibleCropItems))
                    .and(Codec.FLOAT.fieldOf("cropFromGrassChance").forGetter(m -> m.cropFromGrassChance))
                    .and(Codec.FLOAT.fieldOf("multiplierChance").forGetter(m -> m.multiplierChance))
                    .and(Codec.INT.fieldOf("multiplierAmount").forGetter(m -> m.multiplierAmount))
                    .apply(inst, HayBackpackLootModifier::new)));
    private final List<ItemStack> possibleCropItems;
    private final float cropFromGrassChance;
    private final float multiplierChance;
    private final int multiplierAmount;

    protected HayBackpackLootModifier(LootItemCondition[] conditionsIn, List<ItemStack> possibleCropItems, float cropFromGrassChance, float multiplierChance, int multiplierAmount) {
        super(conditionsIn);
        this.possibleCropItems = possibleCropItems;
        this.cropFromGrassChance = cropFromGrassChance;
        this.multiplierChance = multiplierChance;
        this.multiplierAmount = multiplierAmount;

    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        BlockState blockState = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        boolean grassVariant = false;

        if(blockState == null) {
            return generatedLoot;
        }

        if(blockState.getBlock() instanceof TallGrassBlock) {
            grassVariant = true;
        }

        if(!grassVariant) {
            if(!(blockState.getBlock() instanceof CropBlock) || !blockState.hasProperty(CropBlock.AGE) || blockState.getValue(CropBlock.AGE) != CropBlock.MAX_AGE) {
                return generatedLoot;
            }
        }

        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if(entity instanceof Player player && BackpackAbilities.ABILITIES.checkBackpack(player, ModItems.HAY_TRAVELERS_BACKPACK.get())) {
            if(grassVariant) {
                if(context.getRandom().nextFloat() < this.cropFromGrassChance) {
                    if(!this.possibleCropItems.isEmpty()) {
                        ItemStack randomCrop = possibleCropItems.get(context.getRandom().nextInt(possibleCropItems.size()));
                        generatedLoot.add(randomCrop);
                    }
                }
            } else {
                for(ItemStack stack : generatedLoot) {
                    if(context.getRandom().nextFloat() < this.multiplierChance) {
                        int count = stack.getCount();
                        stack.setCount(count * this.multiplierAmount);
                    }
                }
            }
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}