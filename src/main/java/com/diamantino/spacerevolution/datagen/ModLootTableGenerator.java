package com.diamantino.spacerevolution.datagen;

import com.diamantino.spacerevolution.initialization.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;

import java.util.List;

public class ModLootTableGenerator extends FabricBlockLootTableProvider {
    public ModLootTableGenerator(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        generateOreDrops();
        generateDropSelfDrops();
        generateCobbleDrops();
    }

    private void generateCobbleDrops() {
        addDrop(ModBlocks.mercuryStoneBlock, ModBlocks.mercuryCobblestoneBlock);
        addDropWithSilkTouch(ModBlocks.mercuryStoneBlock);

        addDrop(ModBlocks.venusStoneBlock, ModBlocks.venusCobblestoneBlock);
        addDropWithSilkTouch(ModBlocks.venusStoneBlock);

        addDrop(ModBlocks.moonStoneBlock, ModBlocks.moonCobblestoneBlock);
        addDropWithSilkTouch(ModBlocks.moonStoneBlock);

        addDrop(ModBlocks.marsStoneBlock, ModBlocks.marsCobblestoneBlock);
        addDropWithSilkTouch(ModBlocks.marsStoneBlock);

        addDrop(ModBlocks.jupiterStoneBlock, ModBlocks.jupiterCobblestoneBlock);
        addDropWithSilkTouch(ModBlocks.jupiterStoneBlock);

        addDrop(ModBlocks.saturnStoneBlock, ModBlocks.saturnCobblestoneBlock);
        addDropWithSilkTouch(ModBlocks.saturnStoneBlock);

        addDrop(ModBlocks.uranusStoneBlock, ModBlocks.uranusCobblestoneBlock);
        addDropWithSilkTouch(ModBlocks.uranusStoneBlock);

        addDrop(ModBlocks.neptuneStoneBlock, ModBlocks.neptuneCobblestoneBlock);
        addDropWithSilkTouch(ModBlocks.neptuneStoneBlock);

        addDrop(ModBlocks.plutoRedStoneBlock, ModBlocks.plutoRedCobblestoneBlock);
        addDropWithSilkTouch(ModBlocks.plutoRedStoneBlock);

        addDrop(ModBlocks.plutoWhiteStoneBlock, ModBlocks.plutoWhiteCobblestoneBlock);
        addDropWithSilkTouch(ModBlocks.plutoWhiteStoneBlock);
    }

    private void generateOreDrops() {
        //oreDrops();
    }

    private void generateDropSelfDrops() {
        for (Block block : ModBlocks.electricCables) {
            addDrop(block);
        }

        addDrop(ModBlocks.venusSandstoneBlock);

        List<Block> genericBlocks = new java.util.ArrayList<>(ModBlocks.genericBlocks.stream().toList());

        for (Block block : genericBlocks) {
            addDrop(block);
        }
    }
}
