package com.rae.creatingspace.content.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

public class CustomDensityFunctions {

    // TODO maybe do an abstraction for the simplex noise directly ??

    //direct copy of endIsland noise to understand what it does
    protected static final class AsteroidNoise implements DensityFunction.SimpleFunction {
        public static final KeyDispatchDataCodec<AsteroidNoise> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(new AsteroidNoise(0L)));
        private static final float ISLAND_THRESHOLD = -0.9F;
        private final SimplexNoise islandNoise;

        public AsteroidNoise(long p_208630_) {
            RandomSource randomsource = new LegacyRandomSource(p_208630_);
            randomsource.consumeCount(17292);
            this.islandNoise = new SimplexNoise(randomsource);
        }

        private static float getHeightValue(SimplexNoise p_224063_, int p_224064_, int p_224065_) {
            int i = p_224064_ / 2;
            int j = p_224065_ / 2;
            int k = p_224064_ % 2;
            int l = p_224065_ % 2;
            float f = 100.0F - Mth.sqrt((float)(p_224064_ * p_224064_ + p_224065_ * p_224065_)) * 8.0F;
            f = Mth.clamp(f, -100.0F, 80.0F);

            for(int i1 = -12; i1 <= 12; ++i1) {
                for(int j1 = -12; j1 <= 12; ++j1) {
                    long k1 = (long)(i + i1);
                    long l1 = (long)(j + j1);
                    if (k1 * k1 + l1 * l1 > 4096L && p_224063_.getValue((double)k1, (double)l1) < (double)-0.9F) {
                        float f1 = (Mth.abs((float)k1) * 3439.0F + Mth.abs((float)l1) * 147.0F) % 13.0F + 9.0F;
                        float f2 = (float)(k - i1 * 2);
                        float f3 = (float)(l - j1 * 2);
                        float f4 = 100.0F - Mth.sqrt(f2 * f2 + f3 * f3) * f1;
                        f4 = Mth.clamp(f4, -100.0F, 80.0F);
                        f = Math.max(f, f4);
                    }
                }
            }

            return f;
        }

        public double compute(DensityFunction.FunctionContext p_208633_) {
            return ((double)getHeightValue(this.islandNoise, p_208633_.blockX() / 8, p_208633_.blockZ() / 8) - 8.0D) / 128.0D;
        }

        public double minValue() {
            return -0.84375D;
        }

        public double maxValue() {
            return 0.5625D;
        }

        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }
}
