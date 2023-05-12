package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.common.recipes.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CauldronBlock.class)
public class CauldronMixin
{
    @Inject(at = @At(value = "HEAD"), method = "onUse(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;")
    private void onRightClick(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir)
    {
        if(world.isClient || player.isSneaking()) return;

        ItemStack stack = player.getMainHandStack();

        if(stack.getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK)
        {
            BlockState blockState = world.getBlockState(pos);

            if(BackpackDyeRecipe.hasColor(stack) && blockState.getBlock() instanceof CauldronBlock)
            {
                if(blockState.get(CauldronBlock.LEVEL) > 0)
                {
                    stack.getTag().remove("Color");
                    ((CauldronBlock)blockState.getBlock()).setLevel(world, pos, blockState, blockState.get(CauldronBlock.LEVEL) - 1);
                    world.playSound(null, pos.getX(), pos.getY(), pos.getY(), SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
            }
        }
    }
}