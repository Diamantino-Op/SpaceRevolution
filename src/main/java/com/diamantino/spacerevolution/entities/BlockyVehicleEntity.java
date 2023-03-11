package com.diamantino.spacerevolution.entities;

import com.diamantino.spacerevolution.config.ModCommonConfigs;
import com.diamantino.spacerevolution.storage.StructureStorage;
import com.diamantino.spacerevolution.utils.OxygenUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class BlockyVehicleEntity extends Entity {
    private static final TrackedData<Float> SPEED = DataTracker.registerData(BlockyVehicleEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public double clientYaw;
    public double clientPitch;
    public float previousYaw;
    protected double clientX;
    protected double clientY;
    protected double clientZ;
    protected double clientXVelocity;
    protected double clientYVelocity;
    protected double clientZVelocity;
    private int clientInterpolationSteps;

    private final StructureStorage structureStorage;

    public BlockyVehicleEntity(EntityType<?> type, World world) {
        super(type, world);

        this.structureStorage = new StructureStorage(world);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(SPEED, 0.0f);
    }

    private void updatePositionAndRotation() {
        if (this.isLogicalSideForUpdatingMovement()) {
            this.clientInterpolationSteps = 0;
            this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
        }
        if (this.clientInterpolationSteps <= 0) {
            return;
        }
        double d = this.getX() + (this.clientX - this.getX()) / (double) this.clientInterpolationSteps;
        double e = this.getY() + (this.clientY - this.getY()) / (double) this.clientInterpolationSteps;
        double f = this.getZ() + (this.clientZ - this.getZ()) / (double) this.clientInterpolationSteps;
        double g = MathHelper.wrapDegrees(this.clientYaw - (double) this.getYaw());
        this.setYaw(this.getYaw() + (float) g / (float) this.clientInterpolationSteps);
        this.setPitch(this.getPitch() + (float) (this.clientPitch - (double) this.getPitch()) / (float) this.clientInterpolationSteps);
        --this.clientInterpolationSteps;
        this.setPos(d, e, f);
        this.setRotation(this.getYaw(), this.getPitch());
    }

    @Override
    public void tick() {
        this.previousYaw = this.getYaw();

        super.tick();
        this.updatePositionAndRotation();
        this.doMovement();
        this.slowDown();
        this.doGravity();
        this.move(MovementType.SELF, this.getVelocity());
        this.checkBlockCollision();
    }

    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.clientX = x;
        this.clientY = y;
        this.clientZ = z;
        this.clientYaw = yaw;
        this.clientPitch = pitch;
        this.clientInterpolationSteps = 10;
        this.setVelocity(this.clientXVelocity, this.clientYVelocity, this.clientZVelocity);
    }

    @Override
    public void setVelocityClient(double x, double y, double z) {
        this.clientXVelocity = x;
        this.clientYVelocity = y;
        this.clientZVelocity = z;
        this.setVelocity(this.clientXVelocity, this.clientYVelocity, this.clientZVelocity);
    }

    public void doMovement() {
        this.setPitch(0);
        Vec3d movement = this.getRotationVector(this.getPitch(), this.getYaw());

        double yVelocity = this.getVelocity().getY();

        this.setVelocity(this.getVelocity().add(movement.getX(), 0.0, movement.getZ()).multiply(this.getSpeed()));

        this.setVelocity(new Vec3d(this.getVelocity().getX(), yVelocity, this.getVelocity().getZ()));
    }

    // Slow down the vehicle until a full stop is reached
    public void slowDown() {
        this.setSpeed(this.getSpeed() / 1.05f);
        if (this.getSpeed() < 0.001 && this.getSpeed() > -0.001) {
            this.setSpeed(0.0f);
        }
        this.setSpeed(MathHelper.clamp(this.getSpeed(), this.getMinSpeed(), this.getMaxSpeed()));
    }

    public float getMinSpeed() {
        return -0.2f;
    }

    public float getMaxSpeed() {
        return 0.4f;
    }

    public void doGravity() {

        if (!world.isChunkLoaded(this.getBlockPos())) {
            return;
        }

        if (!this.hasNoGravity()) {
            if (this.isSubmergedInWater()) {
                this.setVelocity(this.getVelocity().add(0, -0.0001, 0));
            } else {
                this.setVelocity(this.getVelocity().add(0, -0.03, 0));
            }
            if (this.getVelocity().getY() < ModCommonConfigs.vehicleGravity.getValue()) {
                this.setVelocity(new Vec3d(this.getVelocity().getX(), ModCommonConfigs.vehicleGravity.getValue(), this.getVelocity().getZ()));
            }
        }
    }

    public void explode(float powerMultiplier) {
        if (!this.world.isClient()) {
            world.createExplosion(this, this.getX(), this.getY() + 0.5, this.getZ(), 7.0f * powerMultiplier, OxygenUtils.levelHasOxygen(this.world), World.ExplosionSourceType.TNT);
        }
        this.discard();
    }

    public float getSpeed() {
        return this.dataTracker.get(SPEED);
    }

    public void setSpeed(float value) {
        this.dataTracker.set(SPEED, value);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.shouldCancelInteraction()) {
            return ActionResult.PASS;
        }
        if (!this.world.isClient()) {
            if (this.getPassengerList().size() > this.getMaxPassengers()) {
                return ActionResult.PASS;
            }
            player.setYaw(this.getYaw());
            player.setPitch(this.getPitch());
            return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (amount > 0) {
            if (source.getSource() instanceof PlayerEntity player) {
                if (!(player.getVehicle() instanceof BlockyVehicleEntity)) {
                    //this.drop();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        if (this.getVelocity().getY() < ModCommonConfigs.explosionSpeedThreshold.getValue()) {
            if (this.isOnGround()) {
                this.explode(ModCommonConfigs.vehicleExplosionMultiplier.getValue());
                return true;
            }
        }
        return false;
    }

    @Override
    @Nullable
    public Entity getPrimaryPassenger() {
        return this.getFirstPassenger();
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().size() < this.getMaxPassengers();
    }

    public int getMaxPassengers() {
        return 1;
    }

    @Override
    public double getMountedHeightOffset() {
        return 0.0;
    }
}
