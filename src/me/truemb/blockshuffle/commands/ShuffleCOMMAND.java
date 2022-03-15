package me.truemb.blockshuffle.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.truemb.blockshuffle.main.Main;
import me.truemb.blockshuffle.runnable.GameRunnable;

public class ShuffleCOMMAND implements CommandExecutor {
	
	private Main instance;
	
	public ShuffleCOMMAND(Main plugin) {
		this.instance = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length == 1 && args[0].equalsIgnoreCase("start")) {

			if (!sender.hasPermission(this.instance.manageFile().getString("Permissions.shuffleTimer"))) {
				sender.sendMessage(this.instance.getMessage("noPermission"));
				return true;
			}
			
			if(this.instance.task != null && !this.instance.task.isCancelled()) {
				sender.sendMessage(this.instance.getMessage("isRunning"));
				return true;
			}
			if (Bukkit.getOnlinePlayers().size() >= 2) {				
				this.instance.task = this.instance.getServer().getScheduler().runTaskTimerAsynchronously(this.instance, new GameRunnable(this.instance), 0L, 20L);				
				
				Bukkit.broadcastMessage(this.instance.getMessage("start"));
				return true;
			} else {
				Bukkit.broadcastMessage(this.instance.getMessage("insufficientPlayer"));
				return true;
			}
		}else if(args.length == 1 && args[0].equalsIgnoreCase("stop")) {
			
			if (!sender.hasPermission(this.instance.manageFile().getString("Permissions.shuffleTimer"))) {
				sender.sendMessage(this.instance.getMessage("noPermission"));
				return true;
			}
			
			if(this.instance.task == null || this.instance.task.isCancelled()) {
				sender.sendMessage(this.instance.getMessage("isNotRunning"));
				return true;
			}
			
			this.instance.task.cancel();
			Bukkit.broadcastMessage(this.instance.getMessage("stop"));
			return true;
		}else if(args.length == 1 && args[0].equalsIgnoreCase("skip")) {
			
			if(this.instance.task == null || this.instance.task.isCancelled()) {
				sender.sendMessage(this.instance.getMessage("isNotRunning"));
				return true;
			}
			
			Player p = (Player) sender;
			
			Material type = this.instance.getItemManager().getRandomPhysicalMaterial();
			ItemStack item = new ItemStack(type);
			
			this.instance.targetBlock.put(p.getUniqueId(), type);

			p.sendMessage(this.instance.getMessage("blockNewGive")
					.replaceAll("(?i)%" + "playername" + "%", p.getDisplayName())
					.replaceAll("(?i)%" + "block" + "%", this.instance.getItemManager().getClientItemName(item))
					);
			
			return true;
		}else {
			sender.sendMessage(this.instance.getMessage("help"));
			return true;
		}
	}
}
