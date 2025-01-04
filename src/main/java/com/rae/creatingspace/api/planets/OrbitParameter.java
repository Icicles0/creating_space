package com.rae.creatingspace.api.planets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

/**
 * @param orbitedBody  this is the dimension location for the dimension supposed to be bellow (for exemple in an orbit dimension)
 *                     used in both server and client (used when teleporting from an orbit to the planet bellow + rendering of dimension effect)
 * @param i            inclination of the orbit plane
 * @param omega        rotation of the apogee compared to the positive X
 * @param r            radius of the orbit (only circles are supported)
 * @param orbT         length of time to do a full orbit
 * @param rotationAxis axis of rotation for the  rotation of the planet
 * @param rotT         length of time to do a full rotation
 */
public record OrbitParameter(ResourceLocation orbitedBody, float i, float omega, float r, float orbT, Vec3 rotationAxis, float rotT) {
    public static final ResourceLocation BASE_BODY = new ResourceLocation("sun");
    public static final Codec<OrbitParameter> CODEC = RecordCodecBuilder.create(
            instance ->
                    instance.group(
                            ResourceLocation.CODEC.optionalFieldOf("orbitedBody", BASE_BODY).forGetter(i -> i.orbitedBody),
                            Codec.FLOAT.optionalFieldOf("i",0f).forGetter(OrbitParameter::i),
                            Codec.FLOAT.optionalFieldOf("omega",0f).forGetter(OrbitParameter::omega),
                            Codec.FLOAT.fieldOf("r").forGetter(i->i.r/100),
                            Codec.FLOAT.fieldOf("orbT").forGetter(OrbitParameter::orbT),
                            Vec3.CODEC.optionalFieldOf("rotationAxis", new Vec3(0,1,0)).forGetter(OrbitParameter::rotationAxis),
                            Codec.FLOAT.fieldOf("rotT").forGetter(OrbitParameter::rotT))
                    .apply(instance, OrbitParameter::new));

}
