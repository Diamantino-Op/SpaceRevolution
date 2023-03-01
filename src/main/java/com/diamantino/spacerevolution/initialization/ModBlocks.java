package com.diamantino.spacerevolution.initialization;

import com.diamantino.spacerevolution.blocks.BaseMachineBlock;
import com.diamantino.spacerevolution.blocks.CrusherBlock;
import com.diamantino.spacerevolution.variants.CableVariants;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks {
    //Machines
    public static final Block crusherBlock = registerBlock("crusher", new CrusherBlock(FabricBlockSettings.of(Material.METAL).strength(5).requiresTool().luminance(state -> state.get(BaseMachineBlock.ACTIVE) ? 15 : 0).nonOpaque()), ModItemGroups.machines);

    //Blocks
    public static final List<Block> genericBlocks = new ArrayList<>();

    //Mercury
    public static final Block mercurySurfaceDustBlock = registerGenericBlock("mercury_surface_dust", new Block(FabricBlockSettings.of(Material.AGGREGATE, MapColor.STONE_GRAY).strength(0.5f).sounds(BlockSoundGroup.SAND)), ModItemGroups.blocks);
    public static final Block mercuryStoneBlock = registerGenericBlock("mercury_stone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).strength(1.5f, 6.0f).requiresTool()), ModItemGroups.blocks);
    public static final Block mercuryCobblestoneBlock = registerGenericBlock("mercury_cobblestone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).strength(2.0f, 6.0f).requiresTool()), ModItemGroups.blocks);

    public static final Block venusSandstoneBlock = registerGenericBlock("venus_sandstone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.ORANGE).strength(0.8f).requiresTool()), ModItemGroups.blocks);
    public static final Block venusStoneBlock = registerGenericBlock("venus_stone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.ORANGE).strength(1.5f, 6.0f).requiresTool()), ModItemGroups.blocks);
    public static final Block venusCobblestoneBlock = registerGenericBlock("venus_cobblestone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.ORANGE).strength(2.0f, 6.0f).requiresTool()), ModItemGroups.blocks);

    public static final Block moonSurfaceDustBlock = registerGenericBlock("moon_surface_dust", new Block(FabricBlockSettings.of(Material.AGGREGATE, MapColor.STONE_GRAY).strength(0.5f).sounds(BlockSoundGroup.SAND)), ModItemGroups.blocks);
    public static final Block moonStoneBlock = registerGenericBlock("moon_stone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).strength(1.5f, 6.0f).requiresTool()), ModItemGroups.blocks);
    public static final Block moonCobblestoneBlock = registerGenericBlock("moon_cobblestone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).strength(2.0f, 6.0f).requiresTool()), ModItemGroups.blocks);

    public static final Block marsSurfaceDustBlock = registerGenericBlock("mars_surface_dust", new Block(FabricBlockSettings.of(Material.AGGREGATE, MapColor.BRIGHT_RED).strength(0.5f).sounds(BlockSoundGroup.SAND)), ModItemGroups.blocks);
    public static final Block marsStoneBlock = registerGenericBlock("mars_stone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.BRIGHT_RED).strength(1.5f, 6.0f).requiresTool()), ModItemGroups.blocks);
    public static final Block marsCobblestoneBlock = registerGenericBlock("mars_cobblestone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.BRIGHT_RED).strength(2.0f, 6.0f).requiresTool()), ModItemGroups.blocks);

    public static final Block asteroidBlock = registerGenericBlock("asteroid_block", new Block(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).strength(1.5f, 6.0f).requiresTool()), ModItemGroups.blocks);
    public static final Block resourcefulAsteroidBlock = registerGenericBlock("resourceful_asteroid_block", new Block(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).strength(1.5f, 6.0f).requiresTool()), ModItemGroups.blocks);

    public static final Block jupiterStoneBlock = registerGenericBlock("jupiter_stone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.YELLOW).strength(1.5f, 6.0f).requiresTool()), ModItemGroups.blocks);
    public static final Block jupiterCobblestoneBlock = registerGenericBlock("jupiter_cobblestone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.YELLOW).strength(2.0f, 6.0f).requiresTool()), ModItemGroups.blocks);

    public static final Block saturnStoneBlock = registerGenericBlock("saturn_stone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.PALE_YELLOW).strength(1.5f, 6.0f).requiresTool()), ModItemGroups.blocks);
    public static final Block saturnCobblestoneBlock = registerGenericBlock("saturn_cobblestone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.PALE_YELLOW).strength(2.0f, 6.0f).requiresTool()), ModItemGroups.blocks);

    public static final Block uranusStoneBlock = registerGenericBlock("uranus_stone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).strength(1.5f, 6.0f).requiresTool()), ModItemGroups.blocks);
    public static final Block uranusCobblestoneBlock = registerGenericBlock("uranus_cobblestone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).strength(2.0f, 6.0f).requiresTool()), ModItemGroups.blocks);

    public static final Block neptuneStoneBlock = registerGenericBlock("neptune_stone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).strength(1.5f, 6.0f).requiresTool()), ModItemGroups.blocks);
    public static final Block neptuneCobblestoneBlock = registerGenericBlock("neptune_cobblestone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).strength(2.0f, 6.0f).requiresTool()), ModItemGroups.blocks);

    public static final Block plutoWhiteStoneBlock = registerGenericBlock("pluto_white_stone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.WHITE).strength(1.5f, 6.0f).requiresTool()), ModItemGroups.blocks);
    public static final Block plutoWhiteCobblestoneBlock = registerGenericBlock("pluto_white_cobblestone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.WHITE).strength(2.0f, 6.0f).requiresTool()), ModItemGroups.blocks);
    public static final Block plutoRedStoneBlock = registerGenericBlock("pluto_red_stone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.RED).strength(1.5f, 6.0f).requiresTool()), ModItemGroups.blocks);
    public static final Block plutoRedCobblestoneBlock = registerGenericBlock("pluto_red_cobblestone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.RED).strength(2.0f, 6.0f).requiresTool()), ModItemGroups.blocks);

    //Cables
    public static final List<Block> electricCables = registerElectricCables();

    private static List<Block> registerElectricCables() {
        List<Block> eleCables = new ArrayList<>();

        for (CableVariants.Electric variant : CableVariants.Electric.values()) {
            eleCables.add(registerBlock(variant.name + "_cable", variant.block, ModItemGroups.cables));
        }

        return eleCables;
    }

    private static Block registerGenericBlockWithoutItem(String name, Block block) {
        genericBlocks.add(block);
        return Registry.register(Registries.BLOCK, new Identifier(ModReferences.modId, name), block);
    }

    private static Block registerBlockWithoutItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(ModReferences.modId, name), block);
    }

    private static Block registerGenericBlock(String name, Block block, ItemGroup tab) {
        genericBlocks.add(block);
        registerBlockItem(name, block, tab);
        return Registry.register(Registries.BLOCK, new Identifier(ModReferences.modId, name), block);
    }

    private static Block registerBlock(String name, Block block, ItemGroup tab) {
        registerBlockItem(name, block, tab);
        return Registry.register(Registries.BLOCK, new Identifier(ModReferences.modId, name), block);
    }

    private static Item registerBlockItem(String name, Block block, ItemGroup tab) {
        Item item = Registry.register(Registries.ITEM, new Identifier(ModReferences.modId, name), new BlockItem(block, new FabricItemSettings()));
        ItemGroupEvents.modifyEntriesEvent(tab).register(entries -> entries.add(item));

        return item;
    }

    public static void registerModBlocks() {
        ModReferences.logger.debug("Registering ModBlocks for " + ModReferences.modId);
    }
}
