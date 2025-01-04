package com.rae.creatingspace.content.planets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.api.planets.OrbitParameter;
import com.rae.creatingspace.api.planets.RocketAccessibleDimension;
import com.rae.creatingspace.api.rendering.PlanetsRendering;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlanetsPositionsHandler {
    //to put into the rocket accessible dim later
    private static final Map<ResourceLocation, OrbitParameter> positions = new HashMap<>();

    public static void setOrbitParam(@Nonnull ResourceLocation planet,OrbitParameter param){
        positions.put(planet, param);
    }
    public static SkyPos getSkyPos(ResourceLocation planet,ResourceLocation toRender, float time) {
        if (!planet.equals(toRender)) {
            ArrayList<OrbitParameter> planetParams = new ArrayList<>();
            planetParams.add(positions.getOrDefault(planet, null));
            ArrayList<OrbitParameter> toRenderParams = new ArrayList<>();
            toRenderParams.add(positions.getOrDefault(toRender, null));
            int depth = 0;//depth
            while (toRenderParams.get(0) != null && depth < 10) {
                toRenderParams.add(0, positions.get(toRenderParams.get(0).orbitedBody()));//is this really necessary ?
                depth++;

            }
            depth = 0;//depth
            while (planetParams.get(0) != null && depth < 10) {
                planetParams.add(0, positions.get(planetParams.get(0).orbitedBody()));//is this really necessary ?
                depth++;

            }
            Vec3 planetCartCoord = new Vec3(0,0,0);
            Vec3 toRenderCartCoord = new Vec3(0,0,0);
            for (OrbitParameter temp: planetParams){
                if (temp !=null) {
                    float d = temp.r();
                    float theta = (float) (time / temp.orbT() * Math.PI);
                    planetCartCoord = planetCartCoord.add(Math.sin(theta) * d, 0, Math.cos(theta) * d);
                }
            }
            for (OrbitParameter temp: toRenderParams){
                if (temp !=null) {
                    float d = temp.r();
                    float theta = (float) (time / temp.orbT() * Math.PI);
                    toRenderCartCoord = toRenderCartCoord.add(Math.sin(theta) * d, 0, Math.cos(theta) * d);
                }
            }
            return SkyPos.fromXYZ(toRenderCartCoord, planetCartCoord);
        }
        return SkyPos.ZERO;
    }
    public static void renderForAll(float time, PoseStack ms, MultiBufferSource bufferSource, ResourceLocation center){
        positions.forEach((location, orbitParameter) -> {
                        if(CSDimensionUtil.shouldRenderAsPlanet(location)){
                            PlanetsPositionsHandler.SkyPos pos = getSkyPos(center, location, time);
                            PlanetsRendering.renderPlanet(new ResourceLocation(location.getNamespace(),"textures/environment/"+location.getPath()+".png"), bufferSource, ms, LightTexture.FULL_BRIGHT, 1F,
                                    pos, Quaternion.ONE);
                        }
                    }
                );
    }
    /**
     * this class is a representation of cylindrical coordinates with math convention and y pointed upward
     */
    public static class SkyPos {
        float radius;
        float theta;
        float phi;
        public static SkyPos ZERO = new SkyPos(0, 0,0);
        public SkyPos(float distance, float theta, float phi) {
            this.radius = distance;
            this.theta = theta;
            this.phi = phi;
        }
        public static SkyPos fromXYZ(@Nonnull Vec3 satellite,@Nonnull Vec3 center){
            Vec3 diff = satellite.subtract(center);
            double distance = diff.length();
            double phi = Math.PI/2 - Math.acos(diff.y/distance);
            double theta = Math.signum(diff.x)*Math.acos(diff.z/diff.horizontalDistance());
            return new SkyPos((float) distance, (float) theta, (float) phi);
        }
        public static Vec3 toXYZ(@Nonnull SkyPos pos,@Nonnull Vec3 center){
            return center.add(
                    pos.radius * Math.cos(pos.phi)*Math.cos(pos.theta),
                    pos.radius * Math.sin(pos.phi),
                    pos.radius * Math.cos(pos.phi)*Math.sin(pos.theta));
        }
        public float getRadius() {
            return radius;
        }

        public float getTheta() {
            return theta;
        }

        public float getPhi() {
            return phi;
        }
        /*public static Quaternion toQuaternion(@Nonnull SkyPos pos){
            return Quaternion.
                    pos.radius * Math.sin(pos.theta)*Math.cos(pos.phi),
                    pos.radius * Math.cos(pos.theta),
                    pos.radius * Math.sin(pos.theta)*Math.sin(pos.phi));
        }*/

    }
}
