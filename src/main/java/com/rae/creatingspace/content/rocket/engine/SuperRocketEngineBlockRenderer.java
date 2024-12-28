package com.rae.creatingspace.content.rocket.engine;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rae.creatingspace.init.graphics.PartialModelInit;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import static com.rae.creatingspace.content.rocket.engine.SuperEngineBlock.EXHAUST_PACK;
import static com.rae.creatingspace.content.rocket.engine.SuperEngineBlock.POWER_PACK;

public class SuperRocketEngineBlockRenderer extends SafeBlockEntityRenderer<RocketEngineBlockEntity.NbtDependent> {

    public SuperRocketEngineBlockRenderer(Context context) {
    }

    @Override
    protected void renderSafe(RocketEngineBlockEntity.NbtDependent be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        BlockState state = be.getBlockState();

        Direction direction = state.getValue(RocketEngineBlock.FACING);
        VertexConsumer vb = bufferSource.getBuffer(RenderType.cutoutMipped());
        try {
            SuperEngineBlock.Exhaust exhaust_pack = state.getValue(EXHAUST_PACK);
            //SuperEngineBlock.Power power_pack = state.getValue(POWER_PACK);

            PartialModel exhaust_model = switch (exhaust_pack){
                case AEROSPIKE -> PartialModelInit.SMALL_AEROSPIKE;
                case BELL_NOZZLE -> PartialModelInit.SMALL_BELL_NOZZLE;
            };
            PartialModel power_model = PartialModelInit.SMALL_POWER_HEAD;

            ms.pushPose();
            CachedBufferer.partialFacing(exhaust_model, be.getBlockState(), direction)
                            .light(light).overlay(overlay).renderInto(ms, vb);
            CachedBufferer.partialFacing(power_model, be.getBlockState(), direction)
                    .light(light).overlay(overlay).renderInto(ms, vb);
            ms.popPose();

        } catch (Exception ignored){
        }
    }
}
