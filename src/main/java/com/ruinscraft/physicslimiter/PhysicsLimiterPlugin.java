package com.ruinscraft.physicslimiter;

import java.util.*;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PhysicsLimiterPlugin extends JavaPlugin implements Listener {

	private static Map<Chunk, Integer> chunkUpdates;

	private long interval;
	private long physicsLimit;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		
		chunkUpdates = new HashMap<>();
		interval = getConfig().getLong("interval");
		physicsLimit = getConfig().getLong("physics-limit");
	
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
			chunkUpdates.clear();
		}, interval, interval);
	}
	
	@Override
	public void onDisable() {
		chunkUpdates.clear();
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
	
}
