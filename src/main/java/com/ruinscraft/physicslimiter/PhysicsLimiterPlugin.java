package com.ruinscraft.physicslimiter;

import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PhysicsLimiterPlugin extends JavaPlugin implements Listener {

	private static Map<Chunk, Integer> chunkUpdates;
	private static Map<UUID, Long> notifiedAt;

	private long interval;
	private long physicsLimit;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		
		chunkUpdates = new HashMap<>();
		notifiedAt = new HashMap<>();
		
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
			Chunk chunk = event.getBlock().getChunk();
			
			for (Entity entity : chunk.getEntities()) {
				try {
					if (System.currentTimeMillis() - notifiedAt.getOrDefault(entity.getUniqueId(), 0L) > 120000L) {
						notifiedAt.put(entity.getUniqueId(), System.currentTimeMillis());
						entity.sendMessage(ChatColor.RED + "A machine in your chunk has been disabled because it exceeds the max physics updates allowed for this chunk. If you believe this should not be happening, please contact an admin.");
					}
				} catch (Exception e) {}
			}
			
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
