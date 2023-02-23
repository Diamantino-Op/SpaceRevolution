package com.diamantino.spacerevolution.datagen;

import com.diamantino.spacerevolution.initialization.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;

public class ModLootTableGenerator extends FabricBlockLootTableProvider {
    public ModLootTableGenerator(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        generateOreDrops();
        generateDropSelfDrops();
    }

    private void generateOreDrops() {
        //oreDrops();
    }

    private void generateDropSelfDrops() {
        for (Block block : ModBlocks.electricCables) {
            addDrop(block);
        }

        addDrop(ModBlocks.asteroidBlock);
    }
}
