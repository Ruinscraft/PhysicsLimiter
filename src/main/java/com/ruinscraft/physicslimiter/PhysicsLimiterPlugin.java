package com.ruinscraft.physicslimiter;

import java.util.*;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.ruinscraft.physicslimiter.listeners.BukkitListener;
import com.ruinscraft.physicslimiter.listeners.PaperListener;

public class PhysicsLimiterPlugin extends JavaPlugin implements Listener {

	private static PhysicsLimiterPlugin instance;
	
	public static PhysicsLimiterPlugin getInstance() {
		return instance;
	}
	
	private static Map<Chunk, Integer> chunkUpdates;

	private static boolean paper = false;

	public long interval;
	public long physicsLimit;
	public long armorStandGravityLimit;

	@Override
	public void onEnable() {
		instance = this;
		
		saveDefaultConfig();

		chunkUpdates = new HashMap<>();

		if (hasClass("com.destroystokyo.paper.event.entity.EntityAddToWorldEvent")) {
			getLogger().info("Using optimized Paper events.");
			
			getServer().getPluginManager().registerEvents(new PaperListener(), this);
			
			paper = true;
		} else {
			getServer().getPluginManager().registerEvents(new BukkitListener(), this);
		}
		
		interval = getConfig().getLong("interval");
		physicsLimit = getConfig().getLong("physics-limit");
		armorStandGravityLimit = getConfig().getLong("armor-stand-gravity-limit");

		getServer().getPluginManager().registerEvents(this, this);
		getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
			chunkUpdates.clear();
		}, interval, interval);
	}

	@Override
	public void onDisable() {
		chunkUpdates.clear();
		
		instance = null;
	}

	@EventHandler
	public void onPhysics(BlockPhysicsEvent event) {
		if (incrementChunk(event.getBlock().getChunk()) > physicsLimit) {
			event.setCancelled(true);
		}
	}

	public static int incrementChunk(Chunk chunk) {
		return chunkUpdates.merge(chunk, 1, Integer::sum);
	}

	public static int getUpdates(Chunk chunk) {
		return chunkUpdates.getOrDefault(chunk, 0);
	}

	public static boolean isPaper() {
		return paper;
	}
	
	public static boolean hasClass(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

}
