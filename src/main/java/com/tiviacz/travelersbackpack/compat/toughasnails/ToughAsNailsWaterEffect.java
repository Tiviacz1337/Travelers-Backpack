package com.tiviacz.travelersbackpack.compat.toughasnails;

/*public class ToughAsNailsWaterEffect extends EffectFluid {
    public ToughAsNailsWaterEffect() {
        super("toughasnails:water", Fluids.WATER, FluidConstants.BUCKET);
    }

    @Override
    public void affectDrinker(FluidVariantWrapper fluidStack, Level level, Entity entity) {
        if(entity instanceof Player player) {
            if(ModConfig.thirst.enableThirst && !level.isClientSide()) {
                //Data for bucket
                IThirst thirst = ThirstHelper.getThirst(player);
                int drink_thirst = 3 * 5;
                float drink_hydration = 0.4F;
                float drink_poison_chance = 0.25F;
                thirst.drink(drink_thirst, drink_hydration);

                if(level.getRandom().nextFloat() < drink_poison_chance) {
                    player.addEffect(new MobEffectInstance(TANEffects.THIRST, 600));
                }
            }
        }
    }

    @Override
    public boolean canExecuteEffect(FluidVariantWrapper stack, Level level, Entity entity) {
        return stack.getAmount() >= amountRequired;
    }
}*/
