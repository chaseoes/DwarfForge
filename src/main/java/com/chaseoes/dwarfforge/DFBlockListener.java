package com.chaseoes.dwarfforge;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;

class DFBlockListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlockPlaced();
		boolean attemptToBuildForge = false;

		if (Utils.isBlockOfType(block, Material.FURNACE, Material.BURNING_FURNACE)) {
			attemptToBuildForge = Forge.isValid(block);
		} else if (Utils.isBlockOfType(block, Material.LAVA, Material.STATIONARY_LAVA)) {
			attemptToBuildForge = Forge.isValid(block.getRelative(BlockFace.UP));
		}

		if (!attemptToBuildForge) {
			return;
		}

		Player player = event.getPlayer();
		if (!player.hasPermission("dwarfforge.create")) {
			event.setCancelled(true);
			player.sendMessage("Ye have not the strength of the Dwarfs to create such a forge.");
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if (!Forge.isValid(block)) {
			return;
		}

		Player player = event.getPlayer();
		if (!player.hasPermission("dwarfforge.destroy")) {
			event.setCancelled(true);
			player.sendMessage("Ye have not the might of the Dwarfs to destroy such a forge.");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBlockDamage(BlockDamageEvent event) {
		Block block = event.getBlock();
		if (!Forge.isValid(block)) {
			return;
		}

		Player player = event.getPlayer();
		if (!player.hasPermission("dwarfforge.use")) {
			player.sendMessage("Ye have not the will of the Dwarfs to use such a forge.");
			return;
		}

		final Forge forge = Forge.find(block);
		DwarfForge.getInstance().queueTask(new Runnable() {
			public void run() {
				forge.toggle();
			}
		});
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockIgnite(BlockIgniteEvent event) {
		if (event.getCause() != IgniteCause.LAVA) {
			return;
		}

		Block block = event.getBlock();
		for (int dx = -3; dx <= 3; ++dx) {
			for (int dy = -3; dy <= 3; ++dy) {
				for (int dz = -3; dz <= 3; ++dz) {
					Block check = block.getRelative(dx, dy, dz);
					if (Forge.isValid(check)) {
						event.setCancelled(true);
						return;
					}
				}
			}
		}
	}
	
}
