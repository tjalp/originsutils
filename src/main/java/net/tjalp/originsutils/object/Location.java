package net.tjalp.originsutils.object;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.tjalp.originsutils.OriginsUtils;

import java.util.EnumSet;
import java.util.UUID;

public class Location {

    public static Location getFromJsonObject(JsonObject jsonObject) {
        String worldName = jsonObject.get("world").getAsString();
        double x = jsonObject.get("x").getAsDouble();
        double y = jsonObject.get("y").getAsDouble();
        double z = jsonObject.get("z").getAsDouble();
        float pitch = jsonObject.get("pitch").getAsFloat();
        float yaw = jsonObject.get("yaw").getAsFloat();
        return new Location(OriginsUtils.INSTANCE.getServer().getWorld(RegistryKey.of(Registry.DIMENSION, new Identifier(worldName))), x, y, z, pitch, yaw);
    }

    private ServerWorld world;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;

    public Location(ServerWorld world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = 0;
        this.yaw = 0;
    }

    public Location(ServerWorld world, double x, double y, double z, float pitch, float yaw) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public ServerWorld getWorld() {
        return this.world;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setWorld(ServerWorld world) {
        this.world = world;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("world", world.getRegistryKey().getValue().toString());
        jsonObject.addProperty("x", x);
        jsonObject.addProperty("y", y);
        jsonObject.addProperty("z", z);
        jsonObject.addProperty("pitch", pitch);
        jsonObject.addProperty("yaw", yaw);
        return jsonObject;
    }

    public void teleport(Entity target) {
        BlockPos blockPos = new BlockPos(x, y, z);
        if (!World.isValid(blockPos)) {
            target.sendSystemMessage(new LiteralText("The world you're trying to teleport to does not exist."), UUID.randomUUID());
        } else {
            if (target instanceof ServerPlayerEntity) {
                ChunkPos chunkPos = new ChunkPos(new BlockPos(x, y, z));
                world.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, target.getEntityId());
                target.stopRiding();
                if (((ServerPlayerEntity) target).isSleeping()) {
                    ((ServerPlayerEntity) target).wakeUp(true, true);
                }

                if (world == target.world) {
                    ((ServerPlayerEntity) target).networkHandler.teleportRequest(x, y, z, yaw, pitch, EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class));
                } else {
                    ((ServerPlayerEntity) target).teleport(world, x, y, z, yaw, pitch);
                }

                target.setHeadYaw(yaw);
            } else {
                float f = MathHelper.wrapDegrees(yaw);
                float g = MathHelper.wrapDegrees(pitch);
                g = MathHelper.clamp(g, -90.0F, 90.0F);
                if (world == target.world) {
                    target.refreshPositionAndAngles(x, y, z, f, g);
                    target.setHeadYaw(f);
                } else {
                    target.detach();
                    Entity entity = target;
                    entity = entity.getType().create(world);
                    if (entity == null) {
                        return;
                    }

                    entity.copyFrom(entity);
                    entity.refreshPositionAndAngles(x, y, z, f, g);
                    entity.setHeadYaw(f);
                    world.onDimensionChanged(entity);
                    entity.removed = true;
                }
            }

            if (!(target instanceof LivingEntity) || !((LivingEntity) target).isFallFlying()) {
                target.setVelocity(target.getVelocity().multiply(1.0D, 0.0D, 1.0D));
                target.setOnGround(true);
            }

            if (target instanceof PathAwareEntity) {
                ((PathAwareEntity) target).getNavigation().stop();
            }
        }
    }
}
