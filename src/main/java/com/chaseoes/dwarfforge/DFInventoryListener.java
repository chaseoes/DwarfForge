package com.chaseoes.dwarfforge;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DFInventoryListener implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFurnaceBurn(FurnaceBurnEvent event) {
		final Block block = event.getBlock();
		final Forge forge = Forge.find(block);

		if (forge == null) {
			return;
		}

		if (event.getFuel().getType() == Material.LAVA_BUCKET) {
			final ItemStack bucket = new ItemStack(Material.BUCKET, 1);
			DwarfForge.getInstance().queueTask(new Runnable() {
				public void run() {
					ItemStack item = bucket;
					boolean savedBucket = false;
					Block inputChest = forge.getInputChest();
					Block outputChest = forge.getOutputChest();

					if (item != null && outputChest != null) {
						item = forge.addTo(item, outputChest, false);
					}

					if (item != null && inputChest != null) {
						item = forge.addTo(item, inputChest, false);
					}

					if (item == null) {
						savedBucket = true;
					}

					Inventory inv = ((Furnace) block.getState()).getInventory();
					ItemStack curr = inv.getItem(Forge.FUEL_SLOT);
					if (item == null) {
						if (savedBucket && curr.getType() == Material.BUCKET) {
							inv.setItem(Forge.FUEL_SLOT, new ItemStack(Material.AIR));
						}
					} else {
						if (curr == null || curr.getType() == Material.AIR) {
							inv.setItem(Forge.FUEL_SLOT, item);
						} else {
							if (curr.getType() != Material.BUCKET) {
								block.getWorld().dropItemNaturally(block.getLocation(), item);
							}
						}
					}
				}
			});
		}

		if (Config.isRequireFuel()) {
			DwarfForge.getInstance().queueTask(new Runnable() {
				public void run() {
					forge.burnUpdate();
				}
			});
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFurnaceSmelt(FurnaceSmeltEvent event) {
		Block block = event.getBlock();
		if (!Forge.isValid(block)) {
			return;
		}

		final Forge forge = Forge.find(block);
		DwarfForge.getInstance().queueTask(new Runnable() {
			public void run() {
				forge.smeltUpdate();
			}
		});
	}
	
}
