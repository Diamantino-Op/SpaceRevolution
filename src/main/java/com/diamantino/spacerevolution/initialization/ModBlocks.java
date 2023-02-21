package com.diamantino.spacerevolution.initialization;

import com.diamantino.spacerevolution.blocks.BaseMachineBlock;
import com.diamantino.spacerevolution.blocks.CrusherBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    //Machines
    public static final Block crusherBlock = registerBlock("crusher", new CrusherBlock(FabricBlockSettings.of(Material.METAL).strength(5).requiresTool().luminance(state -> state.get(BaseMachineBlock.ACTIVE) ? 15 : 0).nonOpaque()), ModItemGroups.machines);

    private static Block registerBlockWithoutItem(String name, Block block) {
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
