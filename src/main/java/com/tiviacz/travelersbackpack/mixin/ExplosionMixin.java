package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public class ExplosionMixin
{
    @Shadow @Final private ObjectArrayList<BlockPos> affectedBlocks;

    @Shadow @Final private World world;

    @Inject(at = @At(value = "TAIL"), method = "collectBlocksAndDamageEntities")
    public void collectBlocksAndDamageEntities(CallbackInfo ci)
    {
        this.affectedBlocks.removeIf(pos -> this.world.getBlockState(pos).getBlock() instanceof TravelersBackpackBlock);
    }
}