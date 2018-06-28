package com.ruinscraft.physicslimiter.listeners;

import org.bukkit.Chunk;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import com.ruinscraft.physicslimiter.PhysicsLimiterPlugin;

public class BukkitListener implements Listener {

	@EventHandler
	public void onEntityAdd(EntitySpawnEvent event) {
		Entity entity = event.getEntity();

		if (!(entity instanceof ArmorStand)) {
			return;
		}

		Chunk chunk = entity.getLocation().getChunk();

		int amountInChunk = 0;

		for (Entity entityInChunk : chunk.getEntities()) {
			if (entityInChunk instanceof ArmorStand) {
				amountInChunk++;
			}

			long limit = PhysicsLimiterPlugin.getInstance().armorStandGravityLimit;
			
			if (amountInChunk > limit) {
				// use setGravity, setCanMove is paper only
				// https://destroystokyo.com/javadocs/org/bukkit/entity/ArmorStand.html
				// https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/ArmorStand.html
				((ArmorStand) entity).setGravity(false);
			}
		}
	}
	
}
