package com.rae.creatingspace.content.planets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.rae.creatingspace.api.planets.OrbitParameter;
import com.rae.creatingspace.api.rendering.GeometryRendering;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.foundation.render.RenderTypes;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.NonnullDefault;

import java.nio.Buffer;
import java.util.List;

@NonnullDefault
public class SmartPlanetsEffect extends DimensionSpecialEffects {
    public SmartPlanetsEffect(float cloudLevel, boolean hasGround, DimensionSpecialEffects.SkyType skyType, boolean forceBrightLightmap, boolean constantAmbientLight) {
        super(cloudLevel, hasGround, skyType, forceBrightLightmap, constantAmbientLight);
    }
    private static final int dayLength = 24000;

    public SmartPlanetsEffect() {
        super(Float.NaN, false, SkyType.NONE, false, false);
    }
    private static final ResourceLocation SPACE_SKY_LOCATION = new ResourceLocation("creatingspace", "textures/environment/space_sky.png");

    @Override
    public @NotNull Vec3 getBrightnessDependentFogColor(@NotNull Vec3 vec, float brightness) {
        return vec;//.multiply(brightness * 0.9f, brightness *0.9f, brightness*0.9f);
    }

    @Override
    public boolean isFoggyAt(int p_108874_, int p_108875_) {
        return false;
    }
    @Override
    public boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix) {
        return true;
    }
    @Override
    public boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
        return true;
    }
    @Override
    public boolean tickRain(ClientLevel level, int ticks, Camera camera) {
        return true;
    }

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        float[] fogColor = RenderSystem.getShaderFogColor();
        float aspectRatio = (float) Minecraft.getInstance().getWindow().getWidth() /
                (float) Minecraft.getInstance().getWindow().getHeight();

        // Retrieve the dynamic FOV

        RenderSystem.setShaderFogColor(0,0,0,0);
        RenderSystem.disableDepthTest();
        RenderSystem.depthFunc(GL11.GL_ALWAYS); // Always pass depth test
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        //RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);


        ResourceLocation location = level.dimension().location();
        OrbitParameter orbitParameter = PlanetsPositionsHandler.getOrbitParam(location);
        float time = (level.getDayTime()+partialTick)/dayLength;
        //render space sky uses way too much pos stack calls
        Matrix4f customProjection = Matrix4f.perspective(FovHandler.getCurrentFov(), aspectRatio, 0.0001f, 1000.0f); // Near plane set to 0.01
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(customProjection);

        //renderSpaceSky(poseStack,new Quaternion(new Vector3f(orbitParameter.rotationAxis()), (float) (-2*time/orbitParameter.rotT()* Math.PI),false));



        // Set up Tesselator and MultiBufferSource
        //Tesselator tesselator = Tesselator.getInstance();
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(new BufferBuilder(256));
        poseStack.pushPose();
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));

        renderSpaceSky(poseStack,new Quaternion(new Vector3f(orbitParameter.rotationAxis()), (float) (-2*time/orbitParameter.rotT()* Math.PI),false),
                level.getBiome(camera.getBlockPosition()).get().getSkyColor());

        PlanetsPositionsHandler.renderForAll(time,poseStack,bufferSource,location, false);

        poseStack.popPose();

        //overlay
        /*poseStack.pushPose();
        //poseStack.mulPose(Vector3f.XN.rotationDegrees(camera.getXRot()));
        //poseStack.mulPose(Vector3f.YP.rotationDegrees(camera.getYRot()));
        GeometryRendering.renderPoly(List.of(new Vec3(1,1,0),new Vec3(1,-1,0),new Vec3(-1,-1,0),new Vec3(-1,1,0))
                ,MultiBufferSource.immediate(new BufferBuilder(255)).getBuffer(RenderTypes.getGlowingTranslucent(AllSpecialTextures.BLANK.getLocation())), poseStack.last(), LightTexture.FULL_BRIGHT,
                new Color( level.getBiome(camera.getBlockPosition()).get().getSkyColor())
                );
        poseStack.popPose();*/

        bufferSource.endBatch();
        RenderSystem.restoreProjectionMatrix();
        RenderSystem.setShaderFogColor(fogColor[0],fogColor[1],fogColor[2], fogColor[3]);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(GL11.GL_LEQUAL); // Standard depth function
        RenderSystem.disableBlend();
        return true;
    }
    private static void renderSpaceSky(PoseStack poseStack, Quaternion planetRotation, int skyColor) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, SPACE_SKY_LOCATION);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();

        poseStack.mulPose(planetRotation);

        for(int i = 0; i < 6; ++i) {
            //poseStack.pushPose();
            //poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
            //poseStack.mulPose(Vector3f.XP.rotation(90));
            //make all the face
            if (i == 1) {
                poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            }

            if (i == 2) {
                poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            }

            if (i == 3) {
                poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
            }

            if (i == 4) {
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
            }

            if (i == 5) {
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
            }

            int l = i % 3;
            int i1 = i / 4 % 2;
            float col_begin = (float)(l) / 3.0F;
            float l_begin = (float)(i1) / 2.0F;
            float col_end = (float)(l + 1) / 3.0F;
            float l_end = (float)(i1 + 1) / 2.0F;

            float size = 200.0F;
            float distance = 200.0F;
            Matrix4f matrix4f = poseStack.last().pose();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferbuilder.vertex(matrix4f, -size, -distance, -size).uv(col_end, l_end).color(skyColor).endVertex();
            bufferbuilder.vertex(matrix4f, -size, -distance, size).uv(col_begin, l_end).color(skyColor).endVertex();
            bufferbuilder.vertex(matrix4f, size, -distance, size).uv(col_begin, l_begin).color(skyColor).endVertex();
            bufferbuilder.vertex(matrix4f, size, -distance, -size).uv(col_end, l_begin).color(skyColor).endVertex();
            BufferUploader.drawWithShader(bufferbuilder.end());
            //tesselator.end();

            if (i == 1) {
                poseStack.mulPose(Vector3f.XN.rotationDegrees(90.0F));
            }

            if (i == 2) {
                poseStack.mulPose(Vector3f.XN.rotationDegrees(-90.0F));
            }

            if (i == 3) {
                poseStack.mulPose(Vector3f.XN.rotationDegrees(180.0F));
            }

            if (i == 4) {
                poseStack.mulPose(Vector3f.ZN.rotationDegrees(90.0F));
            }

            if (i == 5) {
                poseStack.mulPose(Vector3f.ZN.rotationDegrees(-90.0F));
            }
        }
        planetRotation.conj();
        poseStack.mulPose(planetRotation);
        planetRotation.conj();
    }
}
