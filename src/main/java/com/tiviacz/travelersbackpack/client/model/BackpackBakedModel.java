package com.tiviacz.travelersbackpack.client.model;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class BackpackBakedModel implements BakedModel {
    private final BackpackBakedQuadCollector bakedQuads;
    private static final ItemTransforms ITEM_TRANSFORMS = createItemTransforms();

    public BackpackBakedModel(BakedModel backpack, BakedModel dyedBackpack) {
        this.bakedQuads = new BackpackBakedQuadCollector(backpack, dyedBackpack);
        RandomSource random = RandomSource.create(0);
        this.bakedQuads.collectBakedQuads(null, random);
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        BackpackBlockEntity.BackpackRenderData renderData = blockView.getBlockEntityRenderData(pos) instanceof BackpackBlockEntity.BackpackRenderData backpackRenderData
                ? backpackRenderData
                : new BackpackBlockEntity.BackpackRenderData(RenderInfo.EMPTY, -1, false, DyeColor.RED.getId());
        RenderInfo info = renderData.info() == null ? RenderInfo.EMPTY : renderData.info();

        Direction direction = Direction.NORTH;
        if(state.getValue(TravelersBackpackBlock.FACING) != null) {
            direction = state.getValue(TravelersBackpackBlock.FACING);
        }
        int index = direction.get2DDataValue();

        if(state.getBlock() == ModBlocks.STANDARD_TRAVELERS_BACKPACK && renderData.dyeColor() != -1) {
            emitDyedBaseQuads(context.getEmitter(), index);
        } else {
            emitBaseQuads(context.getEmitter());
        }
        emitTanksQuads(context.getEmitter(), info, index);
        if(!renderData.isSleepingBagDeployed()) {
            emitSleepingBagQuads(context.getEmitter(), renderData.sleepingBagColor());
        }
        emitExtras(context.getEmitter(), new ItemStack(state.getBlock()).getItem());
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        RenderInfo info = stack.get(ModDataComponents.RENDER_INFO);
        int color = stack.getOrDefault(ModDataComponents.SLEEPING_BAG_COLOR, DyeColor.RED.getId());
        int dyeColor = stack.getOrDefault(DataComponents.DYED_COLOR, new DyedItemColor(-1, false)).rgb();

        if(stack.getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK && dyeColor != -1) {
            emitDyedBaseQuads(context.getEmitter(), 0);
        } else {
            emitBaseQuads(context.getEmitter());
        }
        emitTanksQuads(context.getEmitter(), info, 0);
        emitSleepingBagQuads(context.getEmitter(), color);
        emitExtras(context.getEmitter(), stack.getItem());
    }

    private void emitBaseQuads(QuadEmitter emitter) {
        bakedQuads.getBaseQuads().forEach(quad -> emitter.fromVanilla(quad, emitter.material(), null).emit());
    }

    private void emitDyedBaseQuads(QuadEmitter emitter, int index) {
        bakedQuads.getDyedBaseQuads().forEach(quad -> {
            emitter.fromVanilla(quad, emitter.material(), null);

            // Center of rotation
            final float centerX = 0.5f;
            final float centerZ = 0.5f;

            // Rotation based on provided index
            float angleDegrees = index * 90f;
            float radians = (float)Math.toRadians(angleDegrees);
            float cos = (float)Math.cos(radians);
            float sin = (float)Math.sin(radians);

            for(int i = 0; i < 4; i++) {
                float x = emitter.x(i) - centerX;
                float y = emitter.y(i);
                float z = emitter.z(i) - centerZ;

                float newX = x * cos - z * sin;
                float newZ = x * sin + z * cos;

                emitter.pos(i, newX + centerX, y, newZ + centerZ);
            }

            emitter.emit();
        });
    }

    private void emitExtras(QuadEmitter emitter, Item item) {
        if(item == ModItems.FOX_TRAVELERS_BACKPACK) {
            bakedQuads.getFoxNose().forEach(quad -> emitter.fromVanilla(quad, emitter.material(), null).emit());
        }
        if(item == ModItems.WOLF_TRAVELERS_BACKPACK) {
            bakedQuads.getWolfNose().forEach(quad -> emitter.fromVanilla(quad, emitter.material(), null).emit());
        }
        if(item == ModItems.WARDEN_TRAVELERS_BACKPACK) {
            bakedQuads.getWardenHorns().forEach(quad -> emitter.fromVanilla(quad, emitter.material(), null).emit());
        }
        if(item == ModItems.OCELOT_TRAVELERS_BACKPACK) {
            bakedQuads.getOcelotNose().forEach(quad -> emitter.fromVanilla(quad, emitter.material(), null).emit());
        }
        if(item == ModItems.PIG_TRAVELERS_BACKPACK || item == ModItems.HORSE_TRAVELERS_BACKPACK) {
            bakedQuads.getPigNose().forEach(quad -> emitter.fromVanilla(quad, emitter.material(), null).emit());
        }
        if(item == ModItems.VILLAGER_TRAVELERS_BACKPACK || item == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK) {
            bakedQuads.getVillagerNose().forEach(quad -> emitter.fromVanilla(quad, emitter.material(), null).emit());
        }
    }

    private void emitTanksQuads(QuadEmitter emitter, RenderInfo info, int index) {
        if(info == null || info.hasTanks()) {
            bakedQuads.getTanksQuads().forEach(quad -> emitter.fromVanilla(quad, emitter.material(), null).emit());
            addFluids(emitter, info, index);
        }
    }

    private void emitSleepingBagQuads(QuadEmitter emitter, int color) {
        if(color == -1) {
            return;
        }
        bakedQuads.getSleepingBagExtrasQuads().forEach(quad -> emitter.fromVanilla(quad, emitter.material(), null).emit());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/bag/" + DyeColor.byId(color).getName().toLowerCase(Locale.ENGLISH) + "_sleeping_bag"));
        rebakeSleepingBag(emitter, sprite);
    }

    private void rebakeSleepingBag(QuadEmitter emitter, TextureAtlasSprite sprite) {
        bakedQuads.getSleepingBagQuads().forEach(quad -> {
            TextureAtlasSprite oldSprite = quad.getSprite();
            int[] oldData = quad.getVertices();
            int[] newData = Arrays.copyOf(oldData, oldData.length);

            for(int i = 0; i < 4; i++) {
                int index = i * 8;

                float oldU = Float.intBitsToFloat(oldData[index + 4]);
                float oldV = Float.intBitsToFloat(oldData[index + 5]);

                float uUn = oldSprite.getUOffset(oldU);
                float vUn = oldSprite.getVOffset(oldV);

                float newU = sprite.getU(uUn);
                float newV = sprite.getV(vUn);

                newData[index + 4] = Float.floatToRawIntBits(newU);
                newData[index + 5] = Float.floatToRawIntBits(newV);

                BakedQuad rebaked = new BakedQuad(newData, quad.getTintIndex(), quad.getDirection(), sprite, quad.isShade());
                emitter.fromVanilla(rebaked, emitter.material(), null).emit();
            }
        });
    }

    private void addFluids(QuadEmitter emitter, RenderInfo renderInfo, int index) {
        if(renderInfo != null && renderInfo.hasTanks()) {
            if(!renderInfo.getLeftFluidStack().isEmpty()) {
                addFluid(emitter, renderInfo.getLeftFluidStack(), (float)renderInfo.getLeftFluidStack().getAmount() / renderInfo.getCapacity(), 1.8F / 16D, index);
            }
            if(!renderInfo.getRightFluidStack().isEmpty()) {
                addFluid(emitter, renderInfo.getRightFluidStack(), (float)renderInfo.getRightFluidStack().getAmount() / renderInfo.getCapacity(), 12.7F / 16D, index);
            }
        }
    }

    private void addFluid(QuadEmitter emitter, FluidVariantWrapper fluidStack, float ratio, double xMin, int index) {
        if(fluidStack.isEmpty() || Mth.equal(ratio, 0.0f)) {
            return;
        }

        double yMin = 0.8 / 16d;
        double yMax = yMin + (ratio * 6.2) / 16d;
        AABB bounds = new AABB(xMin, yMin, 6.3 / 16d, xMin + 1.5 / 16d, yMax, 7.8 / 16d);

        int color = FluidVariantRendering.getColor(fluidStack.fluidVariant()) | -16777216;
        TextureAtlasSprite still = FluidVariantRendering.getSprite(fluidStack.fluidVariant());
        float bx1 = 0F;
        float bx2 = 3F;
        float by1 = 0F;
        float by2 = ratio * 12F;
        float bz1 = 0F;
        float bz2 = 3F;

        createQuad(List.of(getVector(bounds.minX, bounds.maxY, bounds.minZ, index), getVector(bounds.minX, bounds.maxY, bounds.maxZ, index), getVector(bounds.maxX, bounds.maxY, bounds.maxZ, index), getVector(bounds.maxX, bounds.maxY, bounds.minZ, index)), still, Direction.UP, false, color, bx1, 4, bz1, 4, emitter);
        createQuad(List.of(getVector(bounds.maxX, bounds.maxY, bounds.minZ, index), getVector(bounds.maxX, bounds.minY, bounds.minZ, index), getVector(bounds.minX, bounds.minY, bounds.minZ, index), getVector(bounds.minX, bounds.maxY, bounds.minZ, index)), still, Direction.NORTH, false, color, bx1, bx2, by1, by2, emitter);
        createQuad(List.of(getVector(bounds.minX, bounds.maxY, bounds.maxZ, index), getVector(bounds.minX, bounds.minY, bounds.maxZ, index), getVector(bounds.maxX, bounds.minY, bounds.maxZ, index), getVector(bounds.maxX, bounds.maxY, bounds.maxZ, index)), still, Direction.SOUTH, false, color, bx1, bx2, by1, by2, emitter);
        createQuad(List.of(getVector(bounds.minX, bounds.maxY, bounds.minZ, index), getVector(bounds.minX, bounds.minY, bounds.minZ, index), getVector(bounds.minX, bounds.minY, bounds.maxZ, index), getVector(bounds.minX, bounds.maxY, bounds.maxZ, index)), still, Direction.WEST, false, color, bz1, bz2, by1, by2, emitter);
        createQuad(List.of(getVector(bounds.maxX, bounds.maxY, bounds.maxZ, index), getVector(bounds.maxX, bounds.minY, bounds.maxZ, index), getVector(bounds.maxX, bounds.minY, bounds.minZ, index), getVector(bounds.maxX, bounds.maxY, bounds.minZ, index)), still, Direction.EAST, false, color, bz1, bz2, by1, by2, emitter);
    }

    private void addSleepingBag(QuadEmitter emitter, int color, int index) {
        float minX = 2.6F / 16;
        float minY = 0.8F / 16;
        float minZ = 8.9F / 16;
        float maxX = 13.5F / 16;
        float maxY = 2.4F / 16;
        float maxZ = 10.5F / 16;

        AABB bounds = new AABB(minX, minY, minZ, maxX, maxY, maxZ);

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/bags/" + DyeColor.byId(color).getName().toLowerCase(Locale.ENGLISH) + "_sleeping_bag"));
        createQuad(List.of(getVector(bounds.maxX, bounds.maxY, bounds.minZ, index), getVector(bounds.maxX, bounds.minY, bounds.minZ, index), getVector(bounds.minX, bounds.minY, bounds.minZ, index), getVector(bounds.minX, bounds.maxY, bounds.minZ, index)), sprite,
                Direction.NORTH, true, 0xFFFFFFFF, 11.5F, 15.25F, 0.5F, 1F, emitter);
        createQuad(List.of(getVector(bounds.minX, bounds.maxY, bounds.maxZ, index), getVector(bounds.minX, bounds.minY, bounds.maxZ, index), getVector(bounds.maxX, bounds.minY, bounds.maxZ, index), getVector(bounds.maxX, bounds.maxY, bounds.maxZ, index)), sprite,
                Direction.SOUTH, true, 0xFFFFFFFF, 8.25F, 12.25F, 0.5F, 1F, emitter);
        createQuad(List.of(getVector(bounds.minX, bounds.maxY, bounds.minZ, index), getVector(bounds.minX, bounds.minY, bounds.minZ, index), getVector(bounds.minX, bounds.minY, bounds.maxZ, index), getVector(bounds.minX, bounds.maxY, bounds.maxZ, index)), sprite,
                Direction.WEST, true, 0xFFFFFFFF, 7.75F, 8.25F, 0.5F, 1F, emitter);
        createQuad(List.of(getVector(bounds.maxX, bounds.maxY, bounds.maxZ, index), getVector(bounds.maxX, bounds.minY, bounds.maxZ, index), getVector(bounds.maxX, bounds.minY, bounds.minZ, index), getVector(bounds.maxX, bounds.maxY, bounds.minZ, index)), sprite,
                Direction.EAST, true, 0xFFFFFFFF, 15.25F, 15.75F, 0.5F, 1F, emitter);
        createQuad(List.of(getVector(bounds.minX, bounds.maxY, bounds.minZ, index), getVector(bounds.minX, bounds.maxY, bounds.maxZ, index), getVector(bounds.maxX, bounds.maxY, bounds.maxZ, index), getVector(bounds.maxX, bounds.maxY, bounds.minZ, index)), sprite,
                Direction.UP, true, 0xFFFFFFFF, 12F, 8.25F, 0.5F, 0F, emitter);
        createQuad(List.of(getVector(bounds.maxX, bounds.minY, bounds.minZ, index), getVector(bounds.maxX, bounds.minY, bounds.maxZ, index), getVector(bounds.minX, bounds.minY, bounds.maxZ, index), getVector(bounds.minX, bounds.minY, bounds.minZ, index)), sprite,
                Direction.DOWN, true, 0xFFFFFFFF, 15.25F, 11.5F, 0F, 0.5F, emitter);
    }

    private Vector3f getVector(double x, double y, double z, int index) {
        Vector3f ret = new Vector3f((float)x, (float)y, (float)z);
        rotate(ret, new Matrix4f().rotateY((float)Math.toRadians(-(index * 90))));
        return ret;
    }

    private void rotate(Vector3f posIn, Matrix4f transform) {
        Vector3f originIn = new Vector3f(0.5f, 0.5f, 0.5f);
        Vector4f vector4f = transform.transform(new Vector4f(posIn.x() - originIn.x(), posIn.y() - originIn.y(), posIn.z() - originIn.z(), 1.0F));
        posIn.set(vector4f.x() + originIn.x(), vector4f.y() + originIn.y(), vector4f.z() + originIn.z());
    }

    private void createQuad(List<Vector3f> vecs, TextureAtlasSprite sprite, Direction face, boolean hasAmbientOcclusion, int color, float u1x, float u2x, float v1x, float v2x, QuadEmitter emitter) {
        Vec3i dirVec = face.getNormal();

        u1x = u1x / 16F;
        u2x = u2x / 16F;
        v1x = v1x / 16F;
        v2x = v2x / 16F;

        float u1 = sprite.getU(u1x);
        float u2 = sprite.getU(u2x);
        float v1 = sprite.getV(v1x);
        float v2 = sprite.getV(v2x);

        emitter.cullFace(face);
        emitter.nominalFace(face);
        emitter.spriteBake(sprite, 0); // 0 = no bake flags

        if(!hasAmbientOcclusion) {
            if(RendererAccess.INSTANCE.hasRenderer()) {
                emitter.material(RendererAccess.INSTANCE.getRenderer().materialFinder().ambientOcclusion(TriState.FALSE).find());
            }
        }

        emitter.pos(0, vecs.get(0));
        emitter.color(0, color);
        emitter.uv(0, u1, v1);
        emitter.normal(0, dirVec.getX(), dirVec.getY(), dirVec.getZ());

        emitter.pos(1, vecs.get(1));
        emitter.color(1, color);
        emitter.uv(1, u1, v2);
        emitter.normal(1, dirVec.getX(), dirVec.getY(), dirVec.getZ());

        emitter.pos(2, vecs.get(2));
        emitter.color(2, color);
        emitter.uv(2, u2, v2);
        emitter.normal(2, dirVec.getX(), dirVec.getY(), dirVec.getZ());

        emitter.pos(3, vecs.get(3));
        emitter.color(3, color);
        emitter.uv(3, u2, v1);
        emitter.normal(3, dirVec.getX(), dirVec.getY(), dirVec.getZ());

        emitter.emit();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
        return bakedQuads.getBaseQuads();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return bakedQuads.getBackpackBakedModel().getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return ITEM_TRANSFORMS;
    }

    @Override
    public ItemOverrides getOverrides() {
        return this.bakedQuads.getBackpackBakedModel().getOverrides();
    }

    private static ItemTransforms createItemTransforms() {
        return new ItemTransforms(
                new ItemTransform(
                        new Vector3f(60, -180, 0),
                        new Vector3f(0, 1.5f / 16f, 0.5f / 16f),
                        new Vector3f(0.7f, 0.7f, 0.7f)
                ),
                new ItemTransform(
                        new Vector3f(60, -180, 0),
                        new Vector3f(0, 1.5f / 16f, 0.5f / 16f),
                        new Vector3f(0.7f, 0.7f, 0.7f)
                ),
                new ItemTransform(
                        new Vector3f(0, -90, 12.5f),
                        new Vector3f(1.13f / 16f, 6f / 16f, 2f / 16f),
                        new Vector3f(0.68f, 0.68f, 0.68f)
                ),
                new ItemTransform(
                        new Vector3f(0, -90, 12.5f),
                        new Vector3f(1.13f / 16f, 6f / 16f, 2f / 16f),
                        new Vector3f(0.68f, 0.68f, 0.68f)
                ),
                new ItemTransform(
                        new Vector3f(0, 180, 0),
                        new Vector3f(0, 14.5f / 16f, 0),
                        new Vector3f(1, 1, 1)
                ),
                new ItemTransform(
                        new Vector3f(30, -38, 0),
                        new Vector3f(-0.25f / 16f, 2.25f / 16f, 0),
                        new Vector3f(1, 1, 1)
                ),
                new ItemTransform(
                        new Vector3f(0, 0, 0),
                        new Vector3f(0, 2f / 16f, 0),
                        new Vector3f(0.5f, 0.5f, 0.5f)
                ),
                new ItemTransform(
                        new Vector3f(0, 180, 0),
                        new Vector3f(0, 2.25f / 16f, 0),
                        new Vector3f(1, 1, 1)
                )
        );
    }
}