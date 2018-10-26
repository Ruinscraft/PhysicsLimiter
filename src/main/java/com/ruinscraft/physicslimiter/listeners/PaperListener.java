package com.ruinscraft.physicslimiter.listeners;

import org.bukkit.Chunk;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.ruinscraft.physicslimiter.PhysicsLimiterPlugin;

public class PaperListener implements Listener {

	@EventHandler
	public void onEntityAdd(EntityAddToWorldEvent event) {
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
				((ArmorStand) entity).setCanMove(false);
			}
		}
	}
	
	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		Entity entity = event.getEntity();
		
		Chunk chunk = entity.getLocation().getChunk();

		int amountInChunk = 0;
		
		for (Entity entityInChunk : chunk.getEntities()) {
			if (entityInChunk instanceof FallingBlock) {
				FallingBlock fallingBlock = (FallingBlock) event.getEntity();
				fallingBlock.setDropItem(false);
				amountInChunk++;
			}
			
			if (amountInChunk > 150) {
				event.setCancelled(true);
				break;
			}
		}
	}
	
}
