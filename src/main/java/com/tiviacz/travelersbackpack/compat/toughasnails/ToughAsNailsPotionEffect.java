package com.tiviacz.travelersbackpack.compat.toughasnails;

import com.tiviacz.travelersbackpack.api.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import toughasnails.api.potion.TANEffects;
import toughasnails.api.thirst.IThirst;
import toughasnails.api.thirst.ThirstHelper;

public class ToughAsNailsPotionEffect extends EffectFluid {
    public ToughAsNailsPotionEffect() {
        super("toughasnails:potion", ModFluids.POTION_FLUID.get(), Reference.POTION);
    }

    @Override
    public void affectDrinker(FluidStack fluidStack, Level level, Entity entity) {
        if(entity instanceof Player player) {
            if(!level.isClientSide) {
                //Data for potion
                IThirst thirst = ThirstHelper.getThirst(player);
                int drink_thirst = 3;
                float drink_hydration = 0.4F;
                float drink_poison_chance = 0.25F;
                thirst.addThirst(drink_thirst);
                thirst.addHydration(drink_hydration);

                if(level.random.nextFloat() < drink_poison_chance) {
                    player.addEffect(new MobEffectInstance(TANEffects.THIRST, 600));
                }
            }
        }
    }

    @Override
    public boolean canExecuteEffect(FluidStack stack, Level level, Entity entity) {
        return stack.getAmount() >= amountRequired;
    }
}
