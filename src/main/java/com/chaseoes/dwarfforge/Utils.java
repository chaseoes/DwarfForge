package com.chaseoes.dwarfforge;

import net.minecraft.server.v1_5_R3.ItemStack;
import net.minecraft.server.v1_5_R3.RecipesFurnace;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftItemStack;

class Utils {

	static final short SECS = 20;
	static final short MINS = 60 * SECS;
	static private boolean isTypicalFuel(Material m) {
		switch (m) {
			case COAL:
			case WOOD:
			case SAPLING:
			case STICK:
			case LAVA_BUCKET:
			case BLAZE_ROD:
				return true;
			default:
				return false;
		}
	}

	static private boolean isCraftedFuel(Material m) {
		switch (m) {
			case FENCE:
			case WOOD_STAIRS:
			case TRAP_DOOR:
			case CHEST:
			case LOCKED_CHEST:
			case NOTE_BLOCK:
			case JUKEBOX:
			case BOOKSHELF:
				return true;
			default:
				return false;
		}
	}

	static Material resultOfCooking(Material mat) {
		ItemStack item = RecipesFurnace.getInstance().getResult(mat.getId());
		return (item != null) ? CraftItemStack.asCraftMirror(item).getType() : null;
	}

	static boolean canCook(Material m) {
		return resultOfCooking(m) != null;
	}

	static boolean canBurn(Material m) {
		return isTypicalFuel(m) || (isCraftedFuel(m) && DwarfForge.getInstance().getConfig().getBoolean("allow-crafted-items"));
	}

	static BlockFace nextCardinalFace(BlockFace dir) {
		switch (dir) {
			case NORTH:
				return BlockFace.EAST;
			case EAST:
				return BlockFace.SOUTH;
			case SOUTH:
				return BlockFace.WEST;
			case WEST:
				return BlockFace.NORTH;
			default:
				throw new IllegalArgumentException(
						"Only cardinal directions permitted: received " + dir);
		}
	}

	static BlockFace prevCardinalFace(BlockFace dir) {
		return nextCardinalFace(dir).getOppositeFace();
	}

	static boolean isBlockOfType(Block block, Material... types) {
		for (Material type : types) {
			if (block.getType() == type) {
				return true;
			}
		}
		return false;
	}

}
