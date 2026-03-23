package com.pvptweaks;

public final class ExplosionTracker {

    private static final long   WINDOW_MS = 800L;
    private static final double RADIUS_SQ = 10.0 * 10.0;

    private static double anchorX, anchorY, anchorZ;
    private static long   anchorTime = 0L;

    private static double crystalX, crystalY, crystalZ;
    private static long   crystalTime = 0L;

    private ExplosionTracker() {}

    public static void recordAnchor(double x, double y, double z) {
        anchorX = x; anchorY = y; anchorZ = z;
        anchorTime = System.currentTimeMillis();
    }

    public static void recordCrystal(double x, double y, double z) {
        crystalX = x; crystalY = y; crystalZ = z;
        crystalTime = System.currentTimeMillis();
    }

    public static boolean isNearAnchor(double x, double y, double z) {
        if (System.currentTimeMillis() - anchorTime > WINDOW_MS) return false;
        return distSq(x, y, z, anchorX, anchorY, anchorZ) < RADIUS_SQ;
    }

    public static boolean isNearCrystal(double x, double y, double z) {
        if (System.currentTimeMillis() - crystalTime > WINDOW_MS) return false;
        return distSq(x, y, z, crystalX, crystalY, crystalZ) < RADIUS_SQ;
    }

    private static double distSq(double x1, double y1, double z1,
                                  double x2, double y2, double z2) {
        double dx = x1-x2, dy = y1-y2, dz = z1-z2;
        return dx*dx + dy*dy + dz*dz;
    }
}
