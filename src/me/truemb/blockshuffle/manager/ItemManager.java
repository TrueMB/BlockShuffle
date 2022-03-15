package me.truemb.blockshuffle.manager;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.truemb.blockshuffle.reflections.MinecraftReflectionProvider;
import me.truemb.blockshuffle.reflections.ReflectionUtil;

public class ItemManager {

	public Material getRandomPhysicalMaterial() {
		Material material = null;
		Random random = new Random();
		while (material == null) {
			material = Material.values()[random.nextInt(Material.values().length)];
			System.out.println("PHYSICAL: " + material.toString());
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
			System.out.println("CRAFTABLE: " + material.toString());
			if (!material.isItem()) {
				material = null;
			}
		}
		return material;
	}
	
	public ItemStack getRandomCraftableItemStack() {
		return new ItemStack(this.getRandomCraftableMaterial());
	}
	
	public String getClientItemName(ItemStack itemStack) {
        final String[] item = {itemStack.getType().name()};
        ReflectionUtil.newCall().getMethod(MinecraftReflectionProvider.CRAFT_ITEMSTACK, "asNMSCopy", ItemStack.class)
                .get().passIfValid(reflectionMethod -> {
            Object nmsItemStack = reflectionMethod.invokeIfValid(null, itemStack);
            item[0] = ReflectionUtil.newCall().getMethod(MinecraftReflectionProvider.NMS_ITEMSTACK, "getName").get().invokeIfValid(nmsItemStack);
        });
        return item[0];
    }
}
