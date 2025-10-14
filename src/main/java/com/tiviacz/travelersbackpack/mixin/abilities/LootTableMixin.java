package com.tiviacz.travelersbackpack.mixin.abilities;

import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.init.ModItems;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LootTable.class)
public class LootTableMixin {
    @Inject(at = @At(value = "TAIL"), method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", cancellable = true)
    public void setTarget(LootContext context, CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir) {
        ObjectArrayList<ItemStack> generatedLoot = cir.getReturnValue();

        BlockState blockState = context.getOptionalParameter(LootContextParams.BLOCK_STATE);
        boolean grassVariant = false;

        if(blockState == null) {
            return;
        }

        if(blockState.getBlock() instanceof TallGrassBlock) {
            grassVariant = true;
        }

        if(!grassVariant) {
            if(!(blockState.getBlock() instanceof CropBlock) || !blockState.hasProperty(CropBlock.AGE) || blockState.getValue(CropBlock.AGE) != CropBlock.MAX_AGE) {
                return;
            }
        }

        boolean modifiedLoot = false;
        Entity entity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if(entity instanceof Player player && BackpackAbilities.ABILITIES.checkBackpack(player, ModItems.HAY_TRAVELERS_BACKPACK)) {
            if(grassVariant) {
                if(context.getRandom().nextFloat() < 0.15F) {
                    List<ItemStack> possibleCrops = List.of(new ItemStack(Items.CARROT), new ItemStack(Items.POTATO), new ItemStack(Items.BEETROOT));
                    if(!possibleCrops.isEmpty()) {
                        ItemStack randomCrop = possibleCrops.get(context.getRandom().nextInt(possibleCrops.size()));
                        generatedLoot.add(randomCrop);
                        modifiedLoot = true;
                    }
                }
            } else {
                for(ItemStack stack : generatedLoot) {
                    if(context.getRandom().nextFloat() < 0.4F) {
                        int count = stack.getCount();
                        stack.setCount(count * 2);
                        modifiedLoot = true;
                    }
                }
            }
        }
        if(modifiedLoot) {
            cir.setReturnValue(generatedLoot);
        }
    }
}