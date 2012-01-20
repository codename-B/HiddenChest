package de.bananaco.hidden;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class OldListener {
	
	private final HiddenChestListener listener;
	private final HiddenChestPlugin plugin;
	private final OldBlockListener block;
	private final OldPlayerListener player;
	
	public OldListener(HiddenChestPlugin plugin) {
		this.plugin = plugin;
		this.listener = plugin.getListener();
		this.block = new OldBlockListener(listener);
		this.player = new OldPlayerListener(listener);
	}
	
	public void registerEvents() {
		Type b = Event.Type.BLOCK_BREAK;
		Type p = Event.Type.BLOCK_PLACE;
		Type i = Event.Type.PLAYER_INTERACT;
		
		plugin.getServer().getPluginManager().registerEvent(b, block, Priority.Normal, plugin);
		plugin.getServer().getPluginManager().registerEvent(p, block, Priority.Normal, plugin);
		plugin.getServer().getPluginManager().registerEvent(i, player, Priority.Normal, plugin);
	}
	
}
class OldBlockListener extends BlockListener {
	
	private final HiddenChestListener listener;

	protected OldBlockListener(HiddenChestListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		if(event.isCancelled())
			return;
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if(!listener.blockBreak(block, player))
			event.setCancelled(true);
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		if(event.isCancelled())
			return;
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if(!listener.blockPlace(block, player))
			event.setCancelled(true);
	}

}
class OldPlayerListener extends PlayerListener {
	
	private final HiddenChestListener listener;
	
	protected OldPlayerListener(HiddenChestListener listener) {
		this.listener = listener;
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.isCancelled())
			return;
		if(event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;
		Block block = event.getClickedBlock();
		Player player = event.getPlayer();
		if(block.getType() == Material.CHEST) {
			if(!listener.chestLeftClick(block, player))
				event.setCancelled(true);
		}
		else {
			if(!listener.blockLeftClick(block, player))
				event.setCancelled(true);
		}
	}
	
}