package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.screen.EnchantmentScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantmentScreenHandler.class)
public class EnchantmentScreenHandlerMixin
{
    @Redirect(
            method = "method_17411",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"
            )
    )
    private boolean isBookshelf(BlockState blockState, Block block)
    {
        return blockState.getBlock() == Blocks.BOOKSHELF || blockState.getBlock() == ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK;
    }
}