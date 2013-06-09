package com.chaseoes.dwarfforge;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class DwarfForge extends JavaPlugin {
	
	static DwarfForge instance;
	
	public static DwarfForge getInstance() {
		return instance;
	}

	interface Listener {
		void onEnable(DwarfForge main);

		void onDisable();
	}
	
	public void onEnable() {
		instance = this;
		getServer().getPluginManager().registerEvents(new DFInventoryListener(), this);
		getServer().getPluginManager().registerEvents(new DFBlockListener(), this);
		restoreActiveForges(Forge.active);

		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
			// Failed to submit metrics. :(
		}
	}

	public void onDisable() {
		saveActiveForges(Forge.active);
		instance = null;
	}

	int queueTask(Runnable task) {
		return getServer().getScheduler().scheduleSyncDelayedTask(this, task);
	}

	int queueDelayedTask(long delay, Runnable task) {
		return getServer().getScheduler().scheduleSyncDelayedTask(this, task, delay);
	}

	int queueRepeatingTask(long delay, long period, Runnable task) {
		return getServer().getScheduler().scheduleSyncRepeatingTask(this, task, delay, period);
	}

	void cancelTask(int id) {
		getServer().getScheduler().cancelTask(id);
	}

	public void saveActiveForges(HashMap<Location, Forge> activeForges) {
		// TODO: Clean up this stupidity.
		saveActive(activeForges);
	}

	void saveActive(HashMap<Location, Forge> activeForges) {
		File fout = new File(getDataFolder(), "active_forges");
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(fout));
			for (Forge forge : activeForges.values()) {
				Location loc = forge.getLocation();
				out.writeUTF(loc.getWorld().getName());
				out.writeDouble(loc.getX());
				out.writeDouble(loc.getY());
				out.writeDouble(loc.getZ());
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void restoreActiveForges(HashMap<Location, Forge> activeForges) {
		// TODO: Clean up this stupidity.
		restoreActive(activeForges);
	}

	void restoreActive(HashMap<Location, Forge> activeForges) {
		activeForges.clear();
		File fin = new File(getDataFolder(), "active_forges");
		if (fin.exists()) {
			try {
				DataInputStream in = new DataInputStream(new FileInputStream(fin));
				while (true) {
					try {
						String name = in.readUTF();
						double x = in.readDouble();
						double y = in.readDouble();
						double z = in.readDouble();
						Location loc = new Location(getServer().getWorld(name), x, y, z);
						activeForges.put(loc, new Forge(loc));
					} catch (EOFException e) {
						break;
					}
				}
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
