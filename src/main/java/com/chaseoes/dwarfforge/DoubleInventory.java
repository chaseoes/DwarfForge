package com.chaseoes.dwarfforge;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class DoubleInventory implements Inventory {

	public int getSize() {
		return major.getSize() + minor.getSize();
	}

	public int getMaxStackSize() {
		return 0;
	}

	public void setMaxStackSize(int i) {
		
	}

	public String getName() {
		return major.getName() + ":" + minor.getName();
	}

	public ItemStack getItem(int index) {
		int majorSize = major.getSize();
		if (index < majorSize) {
			return major.getItem(index);
		} else {
			return minor.getItem(index - majorSize);
		}
	}

	public void setItem(int index, ItemStack item) {
		int majorSize = major.getSize();
		if (index < majorSize) {
			major.setItem(index, item);
		} else {
			minor.setItem(index - majorSize, item);
		}
	}

	public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
		HashMap<Integer, ItemStack> leftover = major.addItem(items);
		if (leftover != null && !leftover.isEmpty()) {
			ItemStack[] rest = {};
			rest = leftover.values().toArray(rest);
			leftover = minor.addItem(rest);
		}
		return leftover;
	}

	public HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
		HashMap<Integer, ItemStack> leftover = major.addItem(items);
		if (leftover != null && !leftover.isEmpty()) {
			ItemStack[] rest = {};
			rest = leftover.values().toArray(rest);
			leftover = minor.removeItem(rest);
		}
		return leftover;
	}

	public ItemStack[] getContents() {
		return concat(major.getContents(), minor.getContents());
	}

	public void setContents(ItemStack[] items) {
		int majorSize = major.getSize();
		if (items.length <= majorSize) {
			major.setContents(items);
		} else {
			major.setContents(Arrays.copyOfRange(items, 0, majorSize));
			minor.setContents(Arrays.copyOfRange(items, majorSize, items.length - majorSize));
		}
	}

	public boolean contains(int materialId) {
		return major.contains(materialId) || minor.contains(materialId);
	}

	public boolean contains(Material material) {
		return major.contains(material) || minor.contains(material);
	}

	public boolean contains(ItemStack item) {
		return major.contains(item) || minor.contains(item);
	}

	public boolean contains(int materialId, int amount) {
		return major.contains(materialId, amount) || minor.contains(materialId, amount);
	}

	public boolean contains(Material material, int amount) {
		return major.contains(material, amount) || minor.contains(material, amount);
	}

	public boolean contains(ItemStack item, int amount) {
		return major.contains(item, amount) || minor.contains(item, amount);
	}

	public HashMap<Integer, ? extends ItemStack> all(int materialId) {
		return combineWithOffset(major.all(materialId), minor.all(materialId), major.getSize());
	}

	public HashMap<Integer, ? extends ItemStack> all(Material material) {
		return combineWithOffset(major.all(material), minor.all(material), major.getSize());
	}

	public HashMap<Integer, ? extends ItemStack> all(ItemStack item) {
		return combineWithOffset(major.all(item), minor.all(item), major.getSize());
	}

	public int first(int materialId) {
		int majorSize = major.getSize();
		int index = major.first(materialId);
		if (index < 0) {
			index = minor.first(materialId);
			if (index >= 0) {
				index += majorSize;
			}
		}
		return index;
	}

	public int first(Material material) {
		int majorSize = major.getSize();
		int index = major.first(material);
		if (index < 0) {
			index = minor.first(material);
			if (index >= 0) {
				index += majorSize;
			}
		}
		return index;
	}

	public int first(ItemStack item) {
		int majorSize = major.getSize();
		int index = major.first(item);
		if (index < 0) {
			index = minor.first(item);
			if (index >= 0) {
				index += majorSize;
			}
		}
		return index;
	}

	public int firstEmpty() {
		int majorSize = major.getSize();
		int index = major.firstEmpty();
		if (index < 0) {
			index = minor.firstEmpty();
			if (index >= 0) {
				index += majorSize;
			}
		}
		return index;
	}

	public void remove(int materialId) {
		major.remove(materialId);
		minor.remove(materialId);
	}

	public void remove(Material material) {
		major.remove(material);
		minor.remove(material);
	}

	public void remove(ItemStack item) {
		major.remove(item);
		minor.remove(item);
	}

	public void clear(int index) {
		int majorSize = major.getSize();
		if (index < majorSize) {
			major.clear(index);
		} else {
			minor.clear(index - majorSize);
		}
	}

	public void clear() {
		major.clear();
		minor.clear();
	}

	public List<HumanEntity> getViewers() {
		List<HumanEntity> tempViewers = new ArrayList<HumanEntity>();
		tempViewers.addAll(major.getViewers());
		tempViewers.addAll(minor.getViewers());
		return tempViewers;
	}

	public String getTitle() {
		return major.getTitle();
	}

	public InventoryType getType() {
		return InventoryType.CHEST;
	}

	public InventoryHolder getHolder() {
		return major.getHolder();
	}

	public ListIterator<ItemStack> iterator() {
		return null;
	}

	public ListIterator<ItemStack> iterator(int i) {
		return null;
	}

	private Inventory major;
	private Inventory minor;

	public DoubleInventory(Inventory major, Inventory minor) {
		this.major = major;
		this.minor = minor;
	}

	private static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	private static <T> HashMap<Integer, ? extends T> combineWithOffset(HashMap<Integer, ? extends T> first, HashMap<Integer, ? extends T> second, int offset) {
		HashMap<Integer, T> result = new HashMap<Integer, T>(first);
		for (Integer key : second.keySet()) {
			result.put(new Integer(key.intValue() + offset), (T) second.get(key));
		}
		return result;
	}

	public boolean containsAtLeast(ItemStack arg0, int arg1) {
		return false;
	}
}

