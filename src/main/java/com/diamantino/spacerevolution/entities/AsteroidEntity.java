package com.diamantino.spacerevolution.entities;

import com.diamantino.spacerevolution.initialization.ModDamageSources;
import com.diamantino.spacerevolution.initialization.ModEntityTypes;
import com.diamantino.spacerevolution.initialization.ModReferences;
import com.diamantino.spacerevolution.world.LevelSeed;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;

public class AsteroidEntity extends LivingEntity {
    StructureTemplate asteroidStructure;
    Vec3d endPos;

    public float yaw = 0;
    public float pitch = 0;

    public AsteroidEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);

        asteroidStructure = loadStructure();
        this.endPos = new Vec3d(0, 0, 0);
    }

    public AsteroidEntity(World world, Vec3d spawnPos, Vec3d endPos) {
        super(ModEntityTypes.asteroidEntityType, world);

        asteroidStructure = loadStructure();
        this.endPos = endPos;

        this.setPosition(spawnPos);
    }

    @Override
    public boolean hasNoDrag() {
        return true;
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (Objects.equals(source.getName(), ModDamageSources.antiAsteroidDamageSource.getName()))
            return super.damage(source, amount);

        return false;
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        if (!state.isOf(Blocks.AIR)) {
            if (asteroidStructure != null && !world.isClient())
                this.place(asteroidStructure);

            this.discard();
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        player.damage(ModDamageSources.asteroidImpactDamageSource, 500);
    }

    @Override
    public void tick() {
        super.tick();

        // Get the entity's current position and calculate the direction to the endPos
        Vec3d currentPos = new Vec3d(this.getX(), this.getY(), this.getZ());
        Vec3d endPos = new Vec3d(this.endPos.getX() + 0.5, this.endPos.getY() + 0.5, this.endPos.getZ() + 0.5);
        Vec3d direction = endPos.subtract(currentPos).normalize();

        // Calculate the next position based on the current position and the direction to the endPos
        Vec3d nextPos = currentPos.add(direction.multiply(0.1));

        // Move the entity to the next position
        this.setPosition(nextPos.getX(), nextPos.getY(), nextPos.getZ());

        // Rotate the entity to face the direction of movement
        yaw = (float) Math.toDegrees(Math.atan2(direction.x, direction.z));
        pitch = (float) Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return LivingEntity.createLivingAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 300);
    }

    public StructureTemplate loadStructure() {
        if (!world.isClient()) {
            StructureTemplateManager structureTemplateManager = ((ServerWorld) world).getStructureTemplateManager();

            Optional<StructureTemplate> optional;
            try {
                optional = structureTemplateManager.getTemplate(new Identifier(ModReferences.modId, "asteroid_crater"));
            } catch (InvalidIdentifierException var6) {
                return null;
            }

            return optional.orElse(null);
        }

        return null;
    }

    public void place(StructureTemplate template) {
        try {
            StructurePlacementData structurePlacementData = (new StructurePlacementData()).setIgnoreEntities(true);

            BlockPos blockPos2 = this.getBlockPos().add(-18, -19, -18);
            template.place((ServerWorld) world, blockPos2, blockPos2, structurePlacementData, Random.create(LevelSeed.getSeed()), 2);
        } catch (Exception ignored) {}
    }

    @Override
    protected int getBurningDuration() {
        return 0;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return DefaultedList.ofSize(4, ItemStack.EMPTY);
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {

    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }
}
