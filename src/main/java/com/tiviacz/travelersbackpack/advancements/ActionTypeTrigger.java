package com.tiviacz.travelersbackpack.advancements;

import com.google.gson.JsonObject;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ActionTypeTrigger extends SimpleCriterionTrigger<ActionTypeTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(TravelersBackpack.MODID, "action_type");
    public static final ActionTypeTrigger INSTANCE = new ActionTypeTrigger();

    private ActionTypeTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayer player, String type) {
        this.trigger(player, instance -> instance.test(type));
    }

    @Override
    protected Instance createInstance(JsonObject json, ContextAwarePredicate predicate, DeserializationContext conditionsParser) {
        return new Instance(predicate, json.get("action").getAsString());
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        private final String action;

        public Instance(ContextAwarePredicate predicate, String action) {
            super(ID, predicate);
            this.action = action;
        }

        @Override
        public ResourceLocation getCriterion() {
            return ID;
        }

        public boolean test(String type) {
            return this.action.equals(type);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject json = super.serializeToJson(context);
            if(action != null) {
                json.addProperty("action", action);
            }
            return json;
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

    public static void register() {
        CriteriaTriggers.register(INSTANCE);
    }
}