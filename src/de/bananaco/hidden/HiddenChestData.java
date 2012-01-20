package de.bananaco.hidden;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

/**
 * Stores the data for HiddenChests and has loading and saving capabilities
 */
public class HiddenChestData {
	
	private final Map<String, Set<HiddenChest>> data = new HashMap<String, Set<HiddenChest>>();
	private final File file = new File("plugins/HiddenChest/chests.dat");
	
	/**
	 * Loads the data from the chests.dat - may crash
	 */
	@SuppressWarnings("unchecked")
	public void load() {
		// Clear the data (obviously)
		data.clear();
		try {
		// If the file doesn't exist, save the default set of data and escape
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
			save();
			return;
		}
		// Otherwise load the data
		GZIPInputStream is = new GZIPInputStream(new FileInputStream(file));
		ObjectInputStream os = new ObjectInputStream(is);
		Object read = os.readObject();
		// Don't forget to cleanup
		is.close();
		// This is the bit that could go funky
		data.putAll((Map<String, Set<HiddenChest>>) read);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Saves the data to the chests.dat - may be slowish
	 */
	public void save() {
		try {
		// Write the whole damn object, this might take a while...
		GZIPOutputStream in = new GZIPOutputStream(new FileOutputStream(file));
		ObjectOutputStream on = new ObjectOutputStream(in);
		on.writeObject(data);
		// Don't forget to cleanup
		on.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns if the Block is a HiddenChest
	 * @param block
	 * @return isLocationHiddenChest(block.getLocation())
	 */
	public boolean isBlockHiddenChest(Block block) {
		return isLocationHiddenChest(block.getLocation());
	}
	
	/**
	 * Returns if the Location is a HiddenChest
	 * @param location
	 * @return boolean
	 */
	public boolean isLocationHiddenChest(Location location) {
		String world = location.getWorld().getName();
		Set<HiddenChest> chests = getChests(world);
		for(HiddenChest chest : chests)
			if(chest.isLocation(location))
				return true;
		return false;
	}
	
	/**
	 * Returns the HiddenChest at that block (or null)
	 * Also accounts for double chests
	 * @param block
	 * @return getHiddenChest(block.getLocation())
	 */
	public HiddenChest getHiddenChest(Block block) {
		return getHiddenChest(block.getLocation());
	}
	
	/**
	 * Returns the HiddenChest at that Location (or null)
	 * Also accounts for double chests
	 * @param location
	 * @return HiddenChest
	 */
	public HiddenChest getHiddenChest(Location location) {
		String world = location.getWorld().getName();
		Set<HiddenChest> chests = getChests(world);
		for(HiddenChest chest : chests)
			if(chest.isLocation(location))
				return chest;
		return null;
	}
	
	/**
	 * Adds a new HiddenChest (by the chest data)
	 * Blocks still need to be changed however
	 * @param chest
	 * @return HiddenChest
	 */
	public HiddenChest add(Chest chest) {
		String world = chest.getWorld().getName();
		Set<HiddenChest> chests = getChests(world);
		HiddenChest ch = new HiddenChest(chest);
		chests.add(ch);
		return ch;
	}
	
	/**
	 * Removes a HiddenChest (after it has been broken for example)
	 * @param chest
	 */
	public void remove(HiddenChest chest) {
		String world = chest.getWorld();
		Set<HiddenChest> chests = getChests(world);
		chests.remove(chest);
	}
	
	/**
	 * Internal method, creates a Set<HiddenChest> for the world if none exists
	 * @param world
	 * @return Set<HiddenChest>
	 */
	private Set<HiddenChest> getChests(String world) {
		// Simple logic to create an entry if none exists
		Set<HiddenChest> chests = data.get(world);
		if(chests == null) {
			chests = new HashSet<HiddenChest>();
			data.put(world, chests);
		}
		return chests;
	}

}
