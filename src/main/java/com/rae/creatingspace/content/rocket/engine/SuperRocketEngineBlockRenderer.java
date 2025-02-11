package com.rae.creatingspace.content.rocket.engine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;

public class SuperRocketEngineBlockRenderer extends SafeBlockEntityRenderer<RocketEngineBlockEntity.NbtDependent> {

    public SuperRocketEngineBlockRenderer(Context context) {
    }

    @Override
    protected void renderSafe(RocketEngineBlockEntity.NbtDependent be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        /*BlockState state = be.getBlockState();

        Direction direction = state.getValue(RocketEngineBlock.FACING);
        VertexConsumer vb = bufferSource.getBuffer(RenderType.cutoutMipped());
        try {
            SuperEngineBlock.Exhaust exhaust_pack = state.getValue(EXHAUST_PACK);
            //SuperEngineBlock.Power power_pack = state.getValue(POWER_PACK);

            PartialModel exhaust_model = switch (exhaust_pack){
                case AEROSPIKE -> PartialModelInit.SMALL_AEROSPIKE;//TODO switch to standard blockstates definition
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
        }*/
    }
}
