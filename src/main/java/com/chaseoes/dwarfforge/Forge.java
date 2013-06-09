package com.chaseoes.dwarfforge;

import java.util.HashMap;


import net.minecraft.server.v1_5_R3.BlockFurnace;
import org.bukkit.craftbukkit.v1_5_R3.CraftWorld;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.FurnaceAndDispenser;

class Forge implements Runnable {
	static final int RAW_SLOT = 0;
	static final int FUEL_SLOT = 1;
	static final int PRODUCT_SLOT = 2;
	private static final int INVALID_TASK = -1;
	// These durations must all be less than max short.
	// Additionally, TASK_DURATION + AVOID_STAMPEDE < BURN_DURATION.
	private static final short ZERO_DURATION = 0;
	private static final short AVOID_STAMPEDE = 2 * Utils.MINS;
	private static final short TASK_DURATION = 20 * Utils.MINS;
	private static final short BURN_DURATION = 25 * Utils.MINS;
	static HashMap<Location, Forge> active = new HashMap<Location, Forge>();
	private static java.util.Random rnd = new java.util.Random();

	private static short avoidStampedeDelay() {
		return (short) rnd.nextInt(AVOID_STAMPEDE);
	}

	private Location loc;
	private int task = INVALID_TASK;

	public Forge(Block block) {
		this.loc = block.getLocation();
	}

	public Forge(Location loc) {
		this.loc = loc;
	}

	public boolean equals(Object obj) {
		return loc.equals(((Forge) obj).loc);
	}

	public int hashCode() {
		return loc.hashCode();
	}

	Location getLocation() {
		return loc;
	}

	Block getBlock() {
		return loc.getBlock();
	}

	boolean isValid() {
		return Forge.isValid(getBlock());
	}

	static boolean isValid(Block block) {
		return isValid(block, Config.getMaxStackVertical());
	}

	static boolean isValid(Block block, int stack) {
		if (!Utils.isBlockOfType(block, Material.FURNACE, Material.BURNING_FURNACE)) {
			return false;
		}
		
		if (stack <= 0) {
			return false;
		}

		Block below = block.getRelative(BlockFace.DOWN);
		if (Config.isAllowLavaExploit()) {
			return Utils.isBlockOfType(below, Material.STATIONARY_LAVA, Material.LAVA) || isValid(below, stack - 1);
		}
		return Utils.isBlockOfType(below, Material.STATIONARY_LAVA) || isValid(below, stack - 1);
	}

	boolean isBurning() {
		Furnace state = (Furnace) getBlock().getState();
		return state.getBurnTime() > 0;
	}

	private void internalsSetFurnaceBurning(boolean flag) {
		CraftWorld world = (CraftWorld) loc.getWorld();
		BlockFurnace.a(flag, world.getHandle(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	private void ignite() {
		Furnace state = (Furnace) getBlock().getState();
		internalsSetFurnaceBurning(true);
		state.setBurnTime(BURN_DURATION);
		state.update();
	}

	private void douse() {
		Furnace state = (Furnace) getBlock().getState();
		internalsSetFurnaceBurning(false);
		state.setBurnTime(ZERO_DURATION);
		state.update();
	}

	boolean updateProduct() {
		Furnace state = (Furnace) getBlock().getState();
		Inventory blockInv = state.getInventory();

		ItemStack item = blockInv.getItem(PRODUCT_SLOT);
		if (item != null && item.getType() != Material.AIR) {
			blockInv.clear(PRODUCT_SLOT);

			Block dest = getOutputChest();
			if (Config.isRequireFuel() && item.getType() == Material.COAL) {
				dest = getInputChest();
			}

			ItemStack remains = addTo(item, dest, false);
			if (remains != null) {
				blockInv.setItem(PRODUCT_SLOT, remains);
				ItemStack raw = blockInv.getItem(RAW_SLOT);
				if (raw != null && raw.getType() != Material.AIR) {
					if (Utils.resultOfCooking(raw.getType()) != remains.getType()) {
						return false;
					}
				}
			}
		}

		return true;
	}

	boolean updateRawMaterial() {
		Furnace state = (Furnace) getBlock().getState();
		Inventory blockInv = state.getInventory();

		ItemStack raw = blockInv.getItem(RAW_SLOT);
		if (raw == null || raw.getType() == Material.AIR) {
			Block input = getInputChest();
			if (input != null) {

				BetterChest chest = new BetterChest((Chest) input.getState());
				Inventory chestInv = chest.getInventory();

				boolean itemFound = false;

				ItemStack[] allItems = chestInv.getContents();
				for (ItemStack items : allItems) {
					if (items != null && Utils.canCook(items.getType())) {

						// TODO This probably needs to be elsewhere (and here?)
						// updateRawMaterial is ALWAYS called after updateProduct
						// If product remains and is NOT the same as what the
						// current item will cook to, skip it.
						ItemStack prod = blockInv.getItem(PRODUCT_SLOT);
						if (prod != null && prod.getType() != Material.AIR) {
							if (Utils.resultOfCooking(items.getType())
									!= prod.getType()) {
								continue;
							}
						}

						ItemStack single = items.clone();
						single.setAmount(1);
						blockInv.setItem(RAW_SLOT, single);

						if (items.getAmount() == 1) {
							chestInv.clear(chestInv.first(items));
						} else {
							items.setAmount(items.getAmount() - 1);
						}

						((Furnace) getBlock().getState()).setCookTime(Config.cookTime());

						itemFound = true;
						break;
					}
				}

				if (!itemFound) {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return Utils.canCook(raw.getType());
		}

		return true;
	}

	boolean updateFuel() {
		// TODO assert DFConfig.requireFuel()
		Furnace state = (Furnace) getBlock().getState();
		Inventory blockInv = state.getInventory();
		ItemStack fuel = blockInv.getItem(FUEL_SLOT);
		if (fuel == null || fuel.getType() == Material.AIR) {
			Block input = getInputChest();
			if (input != null) {
				BetterChest chest = new BetterChest((Chest) input.getState());
				Inventory chestInv = chest.getInventory();
				boolean itemFound = false;
				ItemStack[] allItems = chestInv.getContents();
				for (ItemStack items : allItems) {
					if (items != null && Utils.canBurn(items.getType())) {
						ItemStack single = items.clone();
						single.setAmount(1);
						blockInv.setItem(FUEL_SLOT, single);

						if (items.getAmount() == 1) {
							chestInv.clear(chestInv.first(items));
						} else {
							items.setAmount(items.getAmount() - 1);
						}

						itemFound = true;
						break;
					}
				}

				if (!itemFound) {
					return false;
				}
			}
		}

		return true;
	}

	void update() {
		if (isValid()) {
			if (Config.isRequireFuel()) {
				if (!updateProduct() || !updateRawMaterial() || !updateFuel()) {
					deactivate();
					unloadFuel();
				}
			} else {
				updateProduct();
				updateRawMaterial();
				ignite();
			}
		} else {
			deactivate();
			if (!Config.isRequireFuel()) {
				douse();
			}
		}
	}

	void burnUpdate() {
		update();
	}
	
	void smeltUpdate() {
		update();
		if (isActive()) {
			((Furnace) getBlock().getState()).setCookTime(Config.cookTime());
		}
	}

	public void run() {
		update();
	}

	private void activate() {
		if (!isActive()) {
			active.put(loc, this);
			task = DwarfForge.getInstance().queueRepeatingTask(0, TASK_DURATION + avoidStampedeDelay(), this);
		}
	}

	private void deactivate() {
		if (isActive()) {
			active.remove(loc);
		}
		
		if (task != INVALID_TASK) {
			DwarfForge.getInstance().cancelTask(task);
			task = INVALID_TASK;
		}
	}

	boolean isActive() {
		return active.containsKey(loc);
	}

	void toggle() {
		if (isActive()) {
			if (Config.isRequireFuel()) {
				unloadFuel();
			}
			deactivate();
			douse();
		} else {
			activate();
			((Furnace) getBlock().getState()).setCookTime(Config.cookTime());
		}
	}

	private static BlockFace getForward(Block block) {
		Furnace state = (Furnace) block.getState();
		return ((FurnaceAndDispenser) state.getData()).getFacing();
	}

	private static Block getForgeChest(Block block, BlockFace dir) {
		return getForgeChest(block, dir, Config.getMaxStackHorizontal());
	}

	private static Block getForgeChest(Block block, BlockFace dir, int stack) {
		if (stack <= 0) {
			return null;
		}

		Block adjacent = block.getRelative(dir);
		if (Utils.isBlockOfType(adjacent, Material.CHEST)) {
			return adjacent;
		}

		Block below = block.getRelative(BlockFace.DOWN);
		if (Forge.isValid(below)) {
			return getForgeChest(below, dir, stack);
		}

		if (Forge.isValid(adjacent)) {
			return getForgeChest(adjacent, dir, stack - 1);
		}

		return null;
	}

	Block getInputChest() {
		Block block = getBlock();
		return getForgeChest(block, Utils.nextCardinalFace(getForward(block)));
	}

	Block getOutputChest() {
		Block block = getBlock();
		return getForgeChest(block, Utils.prevCardinalFace(getForward(block)));
	}

	void unloadFuel() {
		Furnace state = (Furnace) getBlock().getState();
		Inventory blockInv = state.getInventory();
		ItemStack fuel = blockInv.getItem(FUEL_SLOT);
		if (fuel == null || fuel.getType() == Material.AIR) {
			return;
		}

		blockInv.clear(FUEL_SLOT);

		Block input = getInputChest();
		if (input != null) {
			BetterChest chest = new BetterChest((Chest) input.getState());
			Inventory chestInv = chest.getInventory();

			HashMap<Integer, ItemStack> remains = chestInv.addItem(fuel);
			fuel = remains.isEmpty() ? null : remains.get(0);
		}

		if (fuel != null) {
			loc.getWorld().dropItemNaturally(loc, fuel);
		}
	}

	ItemStack addTo(ItemStack item, Block chest, boolean dropRemains) {
		if (item == null) {
			return null;
		}

		if (chest == null) {
			if (dropRemains) {
				loc.getWorld().dropItemNaturally(loc, item);
				return null;
			} else {
				return item;
			}
		} else {
			BetterChest bchest = new BetterChest((Chest) chest.getState());
			Inventory chestInv = bchest.getInventory();

			HashMap<Integer, ItemStack> remains = chestInv.addItem(item);
			if (remains.isEmpty()) {
				return null;
			} else {
				if (dropRemains) {
					loc.getWorld().dropItemNaturally(loc, remains.get(0));
					return null;
				} else {
					return remains.get(0);
				}
			}
		}
	}

	ItemStack addToOutput(ItemStack item, boolean dropRemains) {
		return addTo(item, getOutputChest(), dropRemains);
	}

	static Forge find(Block block) {
		return find(block.getLocation());
	}

	static Forge find(Location loc) {
		if (active.containsKey(loc)) {
			return active.get(loc);
		}

		if (isValid(loc.getBlock())) {
			return new Forge(loc);
		}
		return null;
	}
}
