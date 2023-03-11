package com.diamantino.spacerevolution.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RocketEntity extends BlockyVehicleEntity {
    private PlayerEntity lastRider;

    public RocketEntity(EntityType<?> type, World world) {
        super(type, world, new BlockPos(0, 0, 0), new BlockPos(0, 0, 0));
    }

    @Override
    public void tick() {
        super.tick();

        // Rotate the rocket when the player strafes left or right
        if (this.getFirstPassenger() instanceof PlayerEntity player) {
            this.lastRider = player;
            if (ModKeyBindings.leftKeyDown(player)) {
                this.rotateRocketAndPassengers(-1.0f);
            }
            if (ModKeyBindings.rightKeyDown(player)) {
                this.rotateRocketAndPassengers(1.0f);
            }
        }

        if (this.getY() >= VehiclesConfig.RocketConfig.atmosphereLeave || this.getTicksFrozen() > 1000) {
            this.setFlying(true);
        }
        if (!this.isFlying()) {
            if (this.hasLaunchPad()) {
                BlockState below = level.getBlockState(this.blockPosition());
                if (!(below.getBlock() instanceof LaunchPad)) {
                    this.drop();
                } else if (below.getBlock() instanceof LaunchPad) {
                    if (!(below.getValue(LaunchPad.LOCATION) == LocationState.CENTER)) {
                        this.drop();
                    }
                }
            }
        } else {
            this.setCountdownTicks(this.getCountdownTicks() - 1);

            // Phase one: the rocket is counting down, about to launch
            if (this.getCountdownTicks() > 0) {
                this.spawnSmokeParticles();
                this.setPhase(1);
                // Phase two: the rocket has launched
            } else if (this.getY() < VehiclesConfig.RocketConfig.atmosphereLeave) {
                this.spawnAfterburnerParticles();
                this.burnEntitiesUnderRocket();
                this.travel();
                if (this.getCountdownTicks() < -30) {
                    this.explodeIfStopped();
                }
                this.setPhase(2);
                // Phase three: the rocket has reached the required height
            } else if (this.getY() >= VehiclesConfig.RocketConfig.atmosphereLeave) {
                openPlanetSelectionGui();
                this.setPhase(3);
            }
        }
        if (this.isEyeInFluid(FluidTags.LAVA)) {
            this.explode(0.45f);
        }
    }


    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }
}
