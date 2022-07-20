package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnchantingTableBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantingTableBlock.class)
public class EnchantingTableBlockMixin
{
    @Redirect(
            method = "canAccessBookshelf",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"
            )
    )
    private static boolean canAccessBookshelf(BlockState blockState, Block block)
    {
        return blockState.getBlock() == Blocks.BOOKSHELF || blockState.getBlock() == ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK;
    }
}