package me.truemb.blockshuffle.manager;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemManager {

	public Material getRandomPhysicalMaterial() {
		Material material = null;
		Random random = new Random();
		while (material == null) {
			material = Material.values()[random.nextInt(Material.values().length)];
			if (!material.isBlock() || material.isAir()) {
				material = null;
			}
		}
		return material;
	}
	
	public ItemStack getRandomPhysicalItemStack() {
		return new ItemStack(this.getRandomPhysicalMaterial());
	}
	

	public Material getRandomCraftableMaterial() {
		Material material = null;
		Random random = new Random();
		while (material == null) {
			material = Material.values()[random.nextInt(Material.values().length)];
			if (!material.is() || material.isAir()) {
				material = null;
			}
		}
		return material;
	}
	
	public ItemStack getRandomCraftableItemStack() {
		return new ItemStack(this.getRandomCraftableMaterial());
	}
	
	public String getClientItemName(ItemStack item) {
		return item.hasItemMeta() && item.getItemMeta().hasLocalizedName() ? item.getItemMeta().getLocalizedName() : null;
	}
}
