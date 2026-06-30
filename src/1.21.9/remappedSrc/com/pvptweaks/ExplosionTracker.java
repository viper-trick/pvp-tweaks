package com.pvptweaks;

public final class ExplosionTracker {

    private static final long   WINDOW_MS = 800L;
    private static final double RADIUS_SQ = 10.0 * 10.0;

    /** Types of "Other Explosions" (non-crystal, non-anchor). */
    public enum OtherType { TNT, CREEPER, BED, GHAST, WIND_CHARGE, GENERIC }

    // Crystal / Anchor tracking (unchanged)
    private static double anchorX, anchorY, anchorZ;
    private static long   anchorTime = 0L;

    private static double crystalX, crystalY, crystalZ;
    private static long   crystalTime = 0L;

    // "Other" explosion — latest recorded position + type
    private static double   otherX, otherY, otherZ;
    private static long     otherTime = 0L;
    private static OtherType otherType = OtherType.GENERIC;

    private ExplosionTracker() {}

    public static void recordAnchor(double x, double y, double z) {
        anchorX = x; anchorY = y; anchorZ = z;
        anchorTime = System.currentTimeMillis();
    }

    public static void recordCrystal(double x, double y, double z) {
        crystalX = x; crystalY = y; crystalZ = z;
        crystalTime = System.currentTimeMillis();
    }

    /** Record a "other" explosion (TNT / Creeper / Bed / Ghast / Wind Charge). */
    public static void recordOther(double x, double y, double z, OtherType type) {
        otherX = x; otherY = y; otherZ = z;
        otherTime = System.currentTimeMillis();
        otherType = type;
    }

    public static boolean isNearAnchor(double x, double y, double z) {
        if (System.currentTimeMillis() - anchorTime > WINDOW_MS) return false;
        return distSq(x, y, z, anchorX, anchorY, anchorZ) < RADIUS_SQ;
    }

    public static boolean isNearCrystal(double x, double y, double z) {
        if (System.currentTimeMillis() - crystalTime > WINDOW_MS) return false;
        return distSq(x, y, z, crystalX, crystalY, crystalZ) < RADIUS_SQ;
    }

    /**
     * Returns the OtherType for an explosion at (x,y,z), or null if there is
     * no recent "other" explosion near that position.
     */
    public static OtherType getOtherType(double x, double y, double z) {
        if (System.currentTimeMillis() - otherTime > WINDOW_MS) return null;
        if (distSq(x, y, z, otherX, otherY, otherZ) >= RADIUS_SQ) return null;
        return otherType;
    }

    private static double distSq(double x1, double y1, double z1,
                                  double x2, double y2, double z2) {
        double dx = x1-x2, dy = y1-y2, dz = z1-z2;
        return dx*dx + dy*dy + dz*dz;
    }
}
