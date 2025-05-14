package com.tiviacz.travelersbackpack.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BackpackBakedQuadCollector {
    private final BakedModel backpack;
    private final BakedModel dyedBackpack;

    //Base backpack
    private final List<BakedQuad> baseQuads;
    //Base dyed backpack
    private final List<BakedQuad> dyedBaseQuads;

    //Sleeping Bag, Tanks
    private final List<BakedQuad> sleepingBagExtrasQuads;
    private final List<BakedQuad> sleepingBagQuads;
    private final List<BakedQuad> tanksQuads;

    //Noses, Horns
    private final List<BakedQuad> foxNose;
    private final List<BakedQuad> wolfNose;
    private final List<BakedQuad> wardenHorns;
    private final List<BakedQuad> ocelotNose;
    private final List<BakedQuad> villagerNose;
    private final List<BakedQuad> pigNose;

    public BackpackBakedQuadCollector(BakedModel backpack, BakedModel dyedBackpack) {
        this.backpack = backpack;
        this.dyedBackpack = dyedBackpack;

        this.baseQuads = new ArrayList<>();
        this.dyedBaseQuads = new ArrayList<>();
        this.sleepingBagExtrasQuads = new ArrayList<>();
        this.sleepingBagQuads = new ArrayList<>();
        this.tanksQuads = new ArrayList<>();

        //Noses, Horns
        this.foxNose = new ArrayList<>();
        this.wolfNose = new ArrayList<>();
        this.wardenHorns = new ArrayList<>();
        this.ocelotNose = new ArrayList<>();
        this.villagerNose = new ArrayList<>();
        this.pigNose = new ArrayList<>();
    }

    public void collectBakedQuads(@Nullable BlockState state, RandomSource random) {
        this.baseQuads.clear();
        this.dyedBaseQuads.clear();
        this.tanksQuads.clear();
        this.sleepingBagExtrasQuads.clear();
        this.sleepingBagQuads.clear();

        //Noses, Horns
        this.foxNose.clear();
        this.wolfNose.clear();
        this.wardenHorns.clear();
        this.ocelotNose.clear();
        this.villagerNose.clear();
        this.pigNose.clear();

        for(BakedQuad quad : backpack.getQuads(state, null, random)) {
            int tintIndex = quad.getTintIndex();
            if(tintIndex < 100) {
                baseQuads.add(quad);
            } else if(tintIndex == 100) {
                tanksQuads.add(quad);
            } else if(tintIndex == 101) {
                sleepingBagExtrasQuads.add(quad);
            } else if(tintIndex == 102) {
                foxNose.add(quad);
            } else if(tintIndex == 103) {
                wolfNose.add(quad);
            } else if(tintIndex == 104) {
                wardenHorns.add(quad);
            } else if(tintIndex == 105) {
                ocelotNose.add(quad);
            } else if(tintIndex == 106) {
                pigNose.add(quad);
            } else if(tintIndex == 107) {
                villagerNose.add(quad);
            } else if(tintIndex == 108) {
                sleepingBagQuads.add(quad);
            }
        }

        if(dyedBaseQuads.isEmpty()) {
            for(BakedQuad quad : dyedBackpack.getQuads(state, null, random)) {
                if(quad.getTintIndex() < 100) {
                    dyedBaseQuads.add(quad);
                }
            }
        }
    }

    public BakedModel getBackpackBakedModel() {
        return backpack;
    }

    public BakedModel getDyedBackpackBakedModel() {
        return dyedBackpack;
    }

    public List<BakedQuad> getBaseQuads() {
        return baseQuads;
    }

    public List<BakedQuad> getDyedBaseQuads() {
        return dyedBaseQuads;
    }

    public List<BakedQuad> getSleepingBagExtrasQuads() {
        return sleepingBagExtrasQuads;
    }

    public List<BakedQuad> getSleepingBagQuads() {
        return sleepingBagQuads;
    }

    public List<BakedQuad> getTanksQuads() {
        return tanksQuads;
    }

    public List<BakedQuad> getFoxNose() {
        return foxNose;
    }

    public List<BakedQuad> getWolfNose() {
        return wolfNose;
    }

    public List<BakedQuad> getWardenHorns() {
        return wardenHorns;
    }

    public List<BakedQuad> getOcelotNose() {
        return ocelotNose;
    }

    public List<BakedQuad> getPigNose() {
        return pigNose;
    }

    public List<BakedQuad> getVillagerNose() {
        return villagerNose;
    }
}