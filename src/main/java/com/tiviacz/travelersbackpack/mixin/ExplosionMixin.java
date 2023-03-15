package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Explosion.class)
public class ExplosionMixin
{
    @Shadow @Final private List<BlockPos> affectedBlocks;

    @Shadow @Final private World world;

    @Inject(at = @At(value = "TAIL"), method = "collectBlocksAndDamageEntities")
    public void collectBlocksAndDamageEntities(CallbackInfo ci)
    {
        this.affectedBlocks.removeIf(pos -> this.world.getBlockState(pos).getBlock() instanceof TravelersBackpackBlock);
    }

  /*  @Redirect(
            method = "collectBlocksAndDamageEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;isImmuneToExplosion()Z"
            )
    )
    public boolean isImmuneToExplosion(Entity instance)
    {
        if(instance instanceof ItemEntity && ((ItemEntity) instance).getStack().getItem() instanceof TravelersBackpackItem)
        {
            return true;
        }
        return instance.isImmuneToExplosion();
    } */

    @Redirect(
            method = "collectBlocksAndDamageEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Ljava/util/List;"
            )
    )
    public List<Entity> getOtherEntities(World instance, Entity entity, Box box)
    {
        List<Entity> list =  instance.getOtherEntities(entity, box);

        list.removeIf(ob -> ob instanceof ItemEntity && ((ItemEntity)ob).getStack().getItem() instanceof TravelersBackpackItem);

        return list;
    }
}