package de.bananaco.hidden;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HiddenChestPlugin extends JavaPlugin {
	
	private HiddenChestData data;
	private HiddenChestListener listener;
	private OldListener oldListener;
	
	/**
	 * Yay superperms (not)
	 * @param pm
	 * @param p
	 */
	public void registerPermissions(PluginManager pm, Plugin p) {
		Map<String, Boolean> children = new HashMap<String, Boolean>();
		// Iterate through all Material values and add them
		for(Material mat : Material.values())
			if(mat.getId()<127 && mat.getId() != 0)
				children.put("hiddenchest.transform."+mat.getId(), true);
		Permission perm = new Permission("hiddenchest.transform.*", PermissionDefault.FALSE, children);
		// Add it to superperms
		pm.addPermission(perm);
		// Redefine the reference
		children = new HashMap<String, Boolean>();
		children.put("hiddenchest.break", true);
		children.put("hiddenchest.transform.*", true);
		perm = new Permission("hiddenchest.*", PermissionDefault.OP, children);
		// Add it to superperms
		pm.addPermission(perm);
	}

	@Override
	public void onDisable() {
		data.save();
		log("Disabled");
	}

	@Override
	public void onEnable() {
		// register the permissions with superperms
		registerPermissions(getServer().getPluginManager(), this);
		data = new HiddenChestData();
		data.load();
		listener = new HiddenChestListener(data);
		oldListener = new OldListener(this);
		// register the events
		oldListener.registerEvents();
		log("Enabled");
	}
	
	public static void log(String message) {
		System.out.println("[HiddenChest] "+message);
	}

	public HiddenChestListener getListener() {
		return listener;
	}

}