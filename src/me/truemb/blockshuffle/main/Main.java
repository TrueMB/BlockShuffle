package me.truemb.blockshuffle.main;

import java.io.File;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import me.truemb.blockshuffle.commands.ShuffleCOMMAND;
import me.truemb.blockshuffle.enums.ChallengeStatus;
import me.truemb.blockshuffle.listener.MoveListener;
import me.truemb.blockshuffle.manager.ItemManager;
import me.truemb.blockshuffle.utils.UTF8YamlConfiguration;

public class Main extends JavaPlugin {
	
	public HashMap<UUID, ChallengeStatus> challengeStatus = new HashMap<>();
	public HashMap<UUID, String> targetBlock = new HashMap<>();

	private ItemManager itemManager;
	
	public BukkitTask task;
	public int tickCount;

	public void onEnable() {
		
		this.manageFile();
		
		this.itemManager = new ItemManager();

		//COMMANDS
		this.getCommand("shuffle").setExecutor(new ShuffleCOMMAND(this));
		
		//LISTENER
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new MoveListener(this), this);
	}

	//METHODES
	
	public void reset() {
		this.tickCount = 0;
		this.targetBlock.clear();
		for(Player all : Bukkit.getOnlinePlayers()) {
			UUID uuid = all.getUniqueId();
			if(this.challengeStatus.get(uuid) != null && this.challengeStatus.get(uuid) != ChallengeStatus.LOST)
				this.challengeStatus.put(uuid, ChallengeStatus.IDLE);
		}
	}
	
	public void fullReset() {
		this.tickCount = 0;
		this.targetBlock.clear();
		for(Player all : Bukkit.getOnlinePlayers())
			this.challengeStatus.put(all.getUniqueId(), ChallengeStatus.IDLE);
	}
	
	//ACTIONBAR
	/*
	public void sendMessage(String message, Player... players) {
		IChatBaseComponent text = ChatSerializer.a("{\"text\": \"" + message + "\"}");
		PacketPlayOutChat bar = new PacketPlayOutChat(text, ChatMessageType.GAME_INFO, null);
		for (Player p : players) {
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(bar);
		}
	}*/

	//CONFIG
	public String getMessage(String path) {
		String s = this.manageFile().getString("Messages.prefix") + " " + this.manageFile().getString("Messages." + path);
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public UTF8YamlConfiguration manageFile() {
		File configFile = this.getConfigFile();
		if (!configFile.exists())
			saveResource("config.yml", true);
		return new UTF8YamlConfiguration(configFile);
	}

	public File getConfigFile() {
		return new File(this.getDataFolder().getPath(), "config.yml");
	}

	public ItemManager getItemManager() {
		return itemManager;
	}
}
