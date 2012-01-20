package de.bananaco.hidden;

import java.io.Serializable;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
/**
 * Provides easily serialisable data
 * for a chest at a particular x, y, z
 * in a world. Can probably just serialise this.
 */
public class HiddenChest implements Serializable {

	/**
	 * Just your average UID
	 */
	private static final long serialVersionUID = 118788430476405569L;
	@SuppressWarnings("rawtypes")
	private Map[] contents = {};
	// Name of the world
	private final String world;
	// XYZ of the primary block - must be set
	private final int x1;
	private final int y1;
	private final int z1;
	// Is the chest a double chest? must be set
	private final boolean isDouble;
	// Stores the values for the next block of a double chest
	private int x2;
	private int y2;
	private int z2;
	
	/**
	 * Used for directly creating a chest from a (Chest) BlockState.
	 * This is a handy convenience method that removes the need for repeated logic.
	 * @param chest
	 */
	public HiddenChest(Chest chest) {
		Block block = chest.getBlock();
		Block block2 = null;
		// The faces we're going to iterate through
		BlockFace[] faces = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
		// Detect if any of the nearby blocks are chest blocks (ie. is this a double chest?)
		for(BlockFace face : faces)
			if(block.getRelative(face).getType() == Material.CHEST) {
				block2 = block.getRelative(face);
			}
		this.world = block.getWorld().getName();
		this.x1 = block.getX();
		this.y1 = block.getY();
		this.z1 = block.getZ();
		setContents(chest.getInventory().getContents());
		this.isDouble = block2 != null;
		if(isDouble) {
			this.x2 = block2.getX();
			this.y2 = block2.getY();
			this.z2 = block2.getZ();
		}
	}
	
	/**
	 * Constructor for single chests
	 * @param world
	 * @param x1
	 * @param y1
	 * @param z1
	 * @param contents
	 */
	public HiddenChest(String world, int x1, int y1, int z1, ItemStack[] contents) {
		this.world = world;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		setContents(contents);
		isDouble = false;
	}
	
	/**
	 * Constructor for double chests
	 * @param world
	 * @param x1
	 * @param y1
	 * @param z1
	 * @param contents
	 * @param x2
	 * @param y2
	 * @param z2
	 */
	public HiddenChest(String world, int x1, int y1, int z1, ItemStack[] contents, int x2, int y2, int z2) {
		this.world = world;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		setContents(contents);
		isDouble = true;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
	}
	
	/**
	 * Gets (direct) references to the blocks, for changing back
	 * to a chest etc
	 * @return Block[] blocks
	 */
	public Block[] getBlocks() {
		Block[] blocks = new Block[isDouble?2:1];
		blocks[0] = Bukkit.getWorld(world).getBlockAt(x1, y1, z1);
		if(isDouble)
			blocks[1] = Bukkit.getWorld(world).getBlockAt(x2, y2, z2);
		return blocks;
	}
	
	/**
	 * Returns if the Location of a Block object corresponds to the Location of
	 * this HiddenChest. Should be a fast check.
	 * @param location
	 * @return boolean isLocation
	 */
	public boolean isBlock(Block block) {
		return isLocation(block.getLocation());
	}
	
	/**
	 * Returns if the Location object corresponds to the Location of
	 * this HiddenChest. Should be a fast check.
	 * @param location
	 * @return boolean isLocation
	 */
	public boolean isLocation(Location location) {
		// null check
		if(location == null)
			return false;
		// Define the variables
		String world = location.getWorld().getName();
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		// If the world isn't the world of this chest it's not even worth checking
		if(!world.equals(this.world))
			return false;
		// If the chest is a double chest we should check the alternate block first
		else if(isDouble && x == x2 && y == y2 && z == z2)
			return true;
		// Now we check the main block
		else if(x == x1 && y == y1 && z == z1)
			return true;
		return false;
	}
	
	/**
	 * Sets the contents of a chest to an ItemStack[] array
	 * @param contents
	 */
	public void setContents(ItemStack[] contents) {
		this.contents = serialise(contents);
	}
	
	/**
	 * Returns the contents of the chest
	 * @return ItemStack[]
	 */
	@SuppressWarnings("unchecked")
	public ItemStack[] getContents() {
		return deserialise(contents);
	}
	
	/**
	 * Gets the direct reference to the Map[] contained in this HiddenChest
	 * @return Map[]
	 */
	@SuppressWarnings("rawtypes")
	public Map[] getRawContents() {
		return contents;
	}
	
	/**
	 * Serialises an ItemStack[] to a Map[]
	 * @param contents
	 * @return Map[]
	 */
	@SuppressWarnings("rawtypes")
	public static Map[] serialise(ItemStack[] contents) {
		Map[] data = new Map[contents.length];
		for(int i=0; i<contents.length; i++) {
			ItemStack from = contents[i];
			// Can't forget a nice easy null check
			if(from != null) {
				Map<String, Object> to = Stack.serialize(from);
				data[i] = to;
			}
		}
		return data;
	}
	
	/**
	 * Deserialises a Map[] to an ItemStack[]
	 * @param data
	 * @return ItemStack[]
	 */
	public static ItemStack[] deserialise(Map<String, Object>[] data) {
		ItemStack[] contents = new ItemStack[data.length];
		for(int i=0; i<contents.length; i++) {
			Map<String, Object> from = data[i];
			// Can't forget a nice easy null check
			if(from != null) {
				ItemStack to = Stack.deserialize(from);
				contents[i] = to;
			}
		}
		return contents;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {
		return world+":"+x1+","+y1+","+z1+":"+isDouble;
	}

	/**
	 * Returns the name of the world the chest is in
	 * @return String world
	 */
	public String getWorld() {
		return world;
	}

}
