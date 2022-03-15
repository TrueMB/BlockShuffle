package me.truemb.blockshuffle.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.truemb.blockshuffle.main.Main;
import me.truemb.blockshuffle.runnable.GameRunnable;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;

public class ShuffleCOMMAND implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length == 1 && args[0].equalsIgnoreCase("start")) {

			if (sender instanceof Player && !((Player) sender).hasPermission(Main.getPlugin().manageFile().getString("Permissions.shuffleTimer"))) {
				sender.sendMessage(Main.getPlugin().getMessage("noPermission"));
				return true;
			}
			
			if(Main.getPlugin().task != null && !Main.getPlugin().task.isCancelled()) {
				sender.sendMessage(Main.getPlugin().getMessage("isRunning"));
				return true;
			}
			if (Bukkit.getOnlinePlayers().size() >= 2) {				
				Main.getPlugin().task = Main.getPlugin().getServer().getScheduler().runTaskTimerAsynchronously(Main.getPlugin(), new GameRunnable(), 0L, 20L);				
				
				Bukkit.broadcastMessage(Main.getPlugin().getMessage("start"));
				return true;
			} else {
				Bukkit.broadcastMessage(Main.getPlugin().getMessage("insufficientPlayer"));
				return true;
			}
		}else if(args.length == 1 && args[0].equalsIgnoreCase("stop")) {
			
			if (sender instanceof Player && !((Player) sender).hasPermission(Main.getPlugin().manageFile().getString("Permissions.shuffleTimer"))) {
				sender.sendMessage(Main.getPlugin().getMessage("noPermission"));
				return true;
			}
			
			if(Main.getPlugin().task == null || Main.getPlugin().task.isCancelled()) {
				sender.sendMessage(Main.getPlugin().getMessage("isNotRunning"));
				return true;
			}
			
			Main.getPlugin().task.cancel();
			Bukkit.broadcastMessage(Main.getPlugin().getMessage("stop"));
			return true;
		}else if(args.length == 1 && args[0].equalsIgnoreCase("skip")) {
			
			if(Main.getPlugin().task == null || Main.getPlugin().task.isCancelled()) {
				sender.sendMessage(Main.getPlugin().getMessage("isNotRunning"));
				return true;
			}
			
			Player p = (Player) sender;
			String tBlock = Main.getPlugin().getRandomBlock().toString();
			Main.getPlugin().targetBlock.put(p.getUniqueId(), tBlock);
			
			net.minecraft.server.v1_16_R2.ItemStack nmsItem = CraftItemStack.asNMSCopy(new ItemStack(Material.valueOf(tBlock)));
            TranslatableComponent comp = new TranslatableComponent(nmsItem.getItem().getName());			            
            TextComponent main = new TextComponent(Main.getPlugin().getMessage("blockNewGive").replace("PLAYERNAME", p.getDisplayName()));
            main.addExtra(comp);
            main.addExtra(new TextComponent("!"));
            p.spigot().sendMessage(main);
			
			return true;
		}else {
			sender.sendMessage(Main.getPlugin().getMessage("help"));
			return true;
		}
	}
}
