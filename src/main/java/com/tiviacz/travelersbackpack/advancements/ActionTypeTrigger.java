package com.tiviacz.travelersbackpack.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class ActionTypeTrigger extends SimpleCriterionTrigger<ActionTypeTrigger.TriggerInstance> {
    public void trigger(ServerPlayer player, String type) {
        this.trigger(player, instance -> instance.test(type));
    }

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, String action) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        Codec.STRING.fieldOf("action").forGetter(TriggerInstance::action))
                .apply(instance, TriggerInstance::new));

        public boolean test(String type) {
            return this.action.equals(type);
        }

        @Override
        public Optional<ContextAwarePredicate> player() {
            return this.player;
        }
    }

    public static final String UNDYE_BACKPACK = "undye_backpack";
    public static final String CHANGE_SLEEPING_BAG = "change_sleeping_bag";
    public static final String USE_SLEEPING_BAG = "use_sleeping_bag";
    public static final String REVERT_CUSTOM_BACKPACK = "revert_custom_backpack";
    public static final String SWAP_TOOLS = "swap_tools";
    public static final String HOSE_SUCK = "hose_suck";
    public static final String HOSE_SPILL = "hose_spill";
    public static final String HOSE_DRINK = "hose_drink";
    public static final String HOSE_SPILL_POTION = "hose_spill_potion";
    public static final String HOSE_DRINK_POTION = "hose_drink_potion";
}