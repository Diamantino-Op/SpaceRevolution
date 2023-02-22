package com.diamantino.spacerevolution.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

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
        //addDrop();
    }
}
