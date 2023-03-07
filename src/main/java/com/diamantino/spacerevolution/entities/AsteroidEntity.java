package com.diamantino.spacerevolution.entities;

import com.diamantino.spacerevolution.initialization.ModEntities;
import com.diamantino.spacerevolution.initialization.ModReferences;
import com.diamantino.spacerevolution.world.LevelSeed;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Optional;

public class AsteroidEntity extends LivingEntity {
    StructureTemplate asteroidStructure;

    protected AsteroidEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);

        asteroidStructure = loadStructure();
    }

    protected AsteroidEntity(World world, Vec3d spawnPos) {
        super(ModEntities.asteroidEntityType, world);

        asteroidStructure = loadStructure();

        this.setPosition(spawnPos);
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        if (asteroidStructure != null && !world.isClient())
            this.place(asteroidStructure);
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        super.onPlayerCollision(player);
    }

    @Override
    public void tick() {
        super.tick();
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return LivingEntity.createLivingAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 300);
    }

    public StructureTemplate loadStructure() {
        if (!world.isClient()) {
            StructureTemplateManager structureTemplateManager = ((ServerWorld) world).getStructureTemplateManager();

            Optional<StructureTemplate> optional = Optional.empty();
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

            BlockPos blockPos2 = this.getBlockPos().add(-18, 0, -18);
            template.place((ServerWorld) world, blockPos2, blockPos2, structurePlacementData, Random.create(LevelSeed.getSeed()), 2);
        } catch (Exception ignored) {}
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return null;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return null;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {

    }

    @Override
    public Arm getMainArm() {
        return null;
    }
}
