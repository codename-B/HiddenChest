package de.bananaco.hidden;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class Stack {
	
	public static Map<String, Object> serialize(ItemStack itemstack) {
		int id = itemstack.getType().getId();
		int amount = itemstack.getAmount();
		short durability = itemstack.getDurability();
		byte data = 0;
		Map<Integer, Integer> enchantment = new HashMap<Integer, Integer>();
		
		Map<String, Object> serialise = new HashMap<String, Object>();
		
		if(itemstack.getData() != null) {
			data = itemstack.getData().getData();
		}
		
		if(itemstack.getEnchantments() != null && itemstack.getEnchantments().size() > 0) {
			for(Enchantment ench : itemstack.getEnchantments().keySet()) {
				enchantment.put(ench.getId(), itemstack.getEnchantments().get(ench));
			}
		}
		
		serialise.put("id", id);
		serialise.put("amount", amount);
		serialise.put("durability", durability);
		serialise.put("data", data);
		serialise.put("enchantment", enchantment);
		
		return serialise;
	}
	
	@SuppressWarnings("unchecked")
	public static ItemStack deserialize(Map<String, Object> serialise) {
		int id = (Integer) serialise.get("id");
		int amount = (Integer) serialise.get("amount");
		short durability = (Short) serialise.get("durability");
		byte data = (Byte) serialise.get("data");
		Map<Integer, Integer> enchantment = (Map<Integer, Integer>) serialise.get("enchantment");
		
		Map<Enchantment, Integer> ench = new HashMap<Enchantment, Integer>();
		
		if(enchantment != null && enchantment.size() > 0) {
			for(Integer in : enchantment.keySet()) {
			Enchantment e = Enchantment.getById(in);
			ench.put(e, enchantment.get(in));
			}
		}
		

		ItemStack stack = new ItemStack(id, amount, durability, data);
		if(enchantment.size() > 0)
			stack.addEnchantments(ench);
		
		return stack;
	}

}
