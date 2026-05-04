package dev.fweigel.happyghastutils.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public final class GhastAutopilot {

    private static boolean active;
    private static double targetX, targetY, targetZ;
    private static double startX, startY, startZ;
    private static double totalDistance;
    private static long arrivedTimestamp;
    private static double lastTickSpeed;

    private static final double ARRIVAL_DISTANCE = 3.0;
    private static final double SLOWDOWN_DISTANCE = 10.0;
    private static final long ARRIVED_DISPLAY_MS = 3000;

    private GhastAutopilot() {}

    public static void start(double x, double y, double z, Entity ghast) {
        targetX = x;
        targetY = y;
        targetZ = z;
        startX = ghast.getX();
        startY = ghast.getY();
        startZ = ghast.getZ();
        totalDistance = ghast.position().distanceTo(new Vec3(x, y, z));
        active = true;
        arrivedTimestamp = 0;
        lastTickSpeed = 0;
    }

    public static void cancel() {
        active = false;
    }

    public static boolean isActive() {
        return active;
    }

    public static boolean isArrivedRecently() {
        return !active && arrivedTimestamp != 0
                && System.currentTimeMillis() - arrivedTimestamp < ARRIVED_DISPLAY_MS;
    }

    public static double getTargetX() { return targetX; }
    public static double getTargetY() { return targetY; }
    public static double getTargetZ() { return targetZ; }

    public static void tick(Minecraft client) {
        if (!active) return;

        Player player = client.player;
        if (player == null || !(player.getVehicle() instanceof HappyGhast)) {
            cancel();
            return;
        }

        Entity ghast = player.getVehicle();
        Vec3 pos = ghast.position();
        double dist = pos.distanceTo(new Vec3(targetX, targetY, targetZ));

        // Track speed (blocks per tick) from entity movement delta
        double dx = pos.x - ghast.xo;
        double dy = pos.y - ghast.yo;
        double dz = pos.z - ghast.zo;
        lastTickSpeed = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (dist <= ARRIVAL_DISTANCE) {
            active = false;
            arrivedTimestamp = System.currentTimeMillis();
        }
    }

    public static double getRemainingDistance(Entity ghast) {
        return ghast.position().distanceTo(new Vec3(targetX, targetY, targetZ));
    }

    public static double getTotalDistance() {
        return totalDistance;
    }

    public static double getTraveledDistance(Entity ghast) {
        return totalDistance - getRemainingDistance(ghast);
    }

    /** Estimated seconds remaining based on current speed. Returns -1 if no speed data. */
    public static int getEstimatedSecondsRemaining(Entity ghast) {
        if (lastTickSpeed <= 0.01) return -1;
        double remaining = getRemainingDistance(ghast);
        double blocksPerSecond = lastTickSpeed * 20.0;
        return (int) Math.ceil(remaining / blocksPerSecond);
    }

    public static Vec2 computeRotation(Entity ghast) {
        double dx = targetX - ghast.getX();
        double dy = targetY - ghast.getY();
        double dz = targetZ - ghast.getZ();
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;
        float pitch = (float) -(Mth.atan2(dy, horizontalDist) * (180.0 / Math.PI));
        pitch = Mth.clamp(pitch, -45.0f, 45.0f);

        return new Vec2(pitch, yaw);
    }

    public static Vec3 computeInput(Entity ghast) {
        double dx = targetX - ghast.getX();
        double dy = targetY - ghast.getY();
        double dz = targetZ - ghast.getZ();
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        // Speed factor: slow down near target
        double speedFactor = Math.min(1.0, dist / SLOWDOWN_DISTANCE);

        // Anti-wrong-direction: reduce speed if facing away from target
        float targetYaw = (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;
        float angleDiff = Mth.degreesDifference(ghast.getYRot(), targetYaw);
        speedFactor *= Math.max(0.1, Math.cos(Math.toRadians(angleDiff)));

        // Vertical component
        double vertical = 0;
        if (horizontalDist > 0.5) {
            vertical = Mth.clamp(dy / horizontalDist, -1.0, 1.0);
        } else if (Math.abs(dy) > 0.5) {
            vertical = dy > 0 ? 1.0 : -1.0;
        }

        double forward = speedFactor;

        return new Vec3(0, vertical * speedFactor, forward);
    }

    public static boolean hasPlayerInput(Player player) {
        return player.xxa != 0 || player.zza != 0 || player.isJumping();
    }
}
