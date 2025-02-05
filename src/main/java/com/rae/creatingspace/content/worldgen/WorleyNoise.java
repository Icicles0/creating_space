package com.rae.creatingspace.content.worldgen;

import net.minecraft.world.phys.Vec3;

public class WorleyNoise {
    private static final double K = 0.142857142857f;
    private static final double Ko = 0.428571428571f;
    private static final double K2 = 0.020408163265306f;
    private static final double Kz = 0.166666666667f;
    private static final double Kzo = 0.416666666667f;
    private static final double jitter = 0.8f;

    public double getXZSize() {
        return XZSize;
    }

    public double getYSize() {
        return YSize;
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    private final double XZSize;
    private final double YSize;
    private final double scaleFactor;
    public WorleyNoise(double XZSize, double YSize, double scaleFactor) {
        this.XZSize = XZSize;
        this.YSize = YSize;
        this.scaleFactor = scaleFactor;
    }

    private static double permute(double x) {
        return ((34.0d * x + 1.0d) * x) % 289.0d;
    }

    public static double fract(double x) {
        return x - Math.floor(x);
    }

    public static double cellular3x3x3(double px, double py, double pz) {
        double Pix = Math.floor(px), Piy = Math.floor(py), Piz = Math.floor(pz); // Integer part
        double Pfx = fract(px),Pfy = fract(py),Pfz = fract(pz); // Fractional part

        double minDist = Float.MAX_VALUE;

        for (int xi = -1; xi <= 1; xi++) {
            for (int yi = -1; yi <= 1; yi++) {
                for (int zi = -1; zi <= 1; zi++) {
                    // Compute cell coordinates
                    //Vec3 cell = Pi.add(new Vec3(xi, yi, zi));
                    double permuted = permute(permute(permute(Pix + xi)+Piy+yi)+Piz+zi);
                    // Pseudo-random offset inside cell
                    double jitterX = ((permuted*K-Math.floor(permuted*K))-Ko) * jitter;
                    double jitterY = ((Math.floor(permuted*K)%7.0) * K-Ko) * jitter;
                    double jitterZ = ((Math.floor(permuted*K2)) * Kz-Kzo) * jitter;


                    // Compute squared distance
                    double dist = Math.sqrt((jitterX+xi-Pfx)*(jitterX+xi-Pfx)+(jitterY+yi-Pfy)*(jitterY+yi-Pfy)+(jitterZ+zi-Pfz)*(jitterZ+zi-Pfz));

                    // Track minimum distance
                    minDist = Math.min(minDist, dist);
                }
            }
        }
        return Math.sqrt(minDist); // Return the actual distance
    }

    public double getValue(double x, double y, double z) {
        double F = cellular3x3x3((x) / XZSize,(y) / YSize,(z) / XZSize)*scaleFactor;
        return 1- (F * 2);  // Mapping to range [-1, 1]
    }
}

