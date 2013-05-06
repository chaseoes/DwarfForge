/*
    Copyright (C) 2011 by Matthew D Moss

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
*/

package org.simiancage.bukkit.DwarfForge;


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

	// Methods inherited from BlockState
	// At the moment, these all act upon the reference Chest only.
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

	@Override
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

	@Override
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

	// Methods inherited from ContainerBlock
	public Inventory getInventory() {
		Chest other = findAttachedChest();
		if (other == null) {
			return ref.getInventory();
		} else {
			return new DoubleInventory(ref.getInventory(), other.getInventory());
		}
	}


	// BetterChest internals
	private static final BlockFace[] FACES = {
			BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

	private Chest ref;

	public BetterChest(Chest ref) {
		this.ref = ref;
	}

	private Chest findAttachedChest() {
		// Find the first adjacent chest. Note: hacking of various sorts/degrees and/or
		// other plugins might allow multiple chests to be adjacent. Deal with that later
		// if it really becomes necessary (and at all possible to detect).

		Block block = ref.getBlock();
		for (BlockFace face : FACES) {
			Block other = block.getRelative(face);
			if (other.getType() == Material.CHEST) {
				return (Chest) other.getState();    // Found it.
			}
		}
		return null;    // No other adjacent chest.
	}

	@Override
	public void setMetadata(String s, MetadataValue metadataValue) {
		ref.setMetadata(s, metadataValue);
	}

	@Override
	public List<MetadataValue> getMetadata(String s) {
		return ref.getMetadata(s);
	}

	@Override
	public boolean hasMetadata(String s) {
		return ref.hasMetadata(s);
	}

	@Override
	public void removeMetadata(String s, Plugin plugin) {
		ref.removeMetadata(s, plugin);
	}

	@Override
	public Inventory getBlockInventory() {
		return ref.getBlockInventory();
	}
}

