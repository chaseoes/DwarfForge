package com.chaseoes.dwarfforge;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class BetterChest implements Chest {

	public Block getBlock() {
		return ref.getBlock();
	}

	public Chunk getChunk() {
		return ref.getChunk();
	}

	public MaterialData getData() {
		return ref.getData();
	}

	public byte getLightLevel() {
		return ref.getLightLevel();
	}

	public byte getRawData() {
		return ref.getRawData();
	}

	public void setRawData(byte b) {
		ref.setRawData(b);
	}

	public Material getType() {
		return ref.getType();
	}

	public int getTypeId() {
		return ref.getTypeId();
	}

	public World getWorld() {
		return ref.getWorld();
	}

	public int getX() {
		return ref.getX();
	}

	public int getY() {
		return ref.getY();
	}

	public int getZ() {
		return ref.getZ();
	}

	public Location getLocation() {
		return ref.getLocation();
	}
	
	public Location getLocation(Location loc) {
		return ref.getLocation(loc);
	}

	public void setData(MaterialData data) {
		ref.setData(data);
	}

	public void setType(Material type) {
		ref.setType(type);
	}

	public boolean setTypeId(int type) {
		return ref.setTypeId(type);
	}

	public boolean update() {
		return ref.update();
	}

	public boolean update(boolean force) {
		return ref.update(force);
	}
	
	public boolean update(boolean force, boolean applyPhysics) {
		return ref.update(force, applyPhysics);
	}

	public Inventory getInventory() {
		Chest other = findAttachedChest();
		if (other == null) {
			return ref.getInventory();
		} else {
			return new DoubleInventory(ref.getInventory(), other.getInventory());
		}
	}

	private static final BlockFace[] FACES = {
		BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST
	};

	private Chest ref;

	public BetterChest(Chest ref) {
		this.ref = ref;
	}

	private Chest findAttachedChest() {
		Block block = ref.getBlock();
		for (BlockFace face : FACES) {
			Block other = block.getRelative(face);
			if (other.getType() == Material.CHEST) {
				return (Chest) other.getState();
			}
		}
		return null;
	}

	public void setMetadata(String s, MetadataValue metadataValue) {
		ref.setMetadata(s, metadataValue);
	}

	public List<MetadataValue> getMetadata(String s) {
		return ref.getMetadata(s);
	}

	public boolean hasMetadata(String s) {
		return ref.hasMetadata(s);
	}

	public void removeMetadata(String s, Plugin plugin) {
		ref.removeMetadata(s, plugin);
	}

	public Inventory getBlockInventory() {
		return ref.getBlockInventory();
	}
	
}
