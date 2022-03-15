package me.truemb.blockshuffle.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import com.jeff_media.updatechecker.UserAgentBuilder;

import me.truemb.blockshuffle.commands.ShuffleCOMMAND;
import me.truemb.blockshuffle.enums.ChallengeStatus;
import me.truemb.blockshuffle.listener.MoveListener;
import me.truemb.blockshuffle.manager.ItemManager;
import me.truemb.blockshuffle.utils.ConfigUpdater;
import me.truemb.blockshuffle.utils.UTF8YamlConfiguration;

public class Main extends JavaPlugin {
	
	public HashMap<UUID, ChallengeStatus> challengeStatus = new HashMap<>();
	public HashMap<UUID, Material> targetBlock = new HashMap<>();

	private ItemManager itemManager;
	
	private UTF8YamlConfiguration config;
	public BukkitTask task;
	public int tickCount;
	
	private static final int configVersion = 1;
    private static final String SPIGOT_RESOURCE_ID = ""; //TODO
    private static final int BSTATS_PLUGIN_ID = 14638;

	public void onEnable() {
		
		this.manageFile();
		
		this.itemManager = new ItemManager();

		//COMMANDS
		this.getCommand("shuffle").setExecutor(new ShuffleCOMMAND(this));
		
		//LISTENER
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new MoveListener(this), this);
		
		//METRICS ANALYTICS
		if(this.manageFile().getBoolean("Options.useMetrics"))
			new Metrics(this, BSTATS_PLUGIN_ID);
				
		//UPDATE CHECKER
		this.checkForUpdate();
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
		
		if(this.config == null) {
			
			//TO GET THE CONFIG VERSION
			this.config = new UTF8YamlConfiguration(configFile);
			
			//UPDATE
			if(!this.config.isSet("ConfigVersion") || this.config.getInt("ConfigVersion") < configVersion) {
				this.getLogger().info("Updating Config!");
				try {
					List<String> ignore = new ArrayList<>();
					
					ignore.add("Options.categorySettings");
					ignore.add("Options.maxPossible");
					
					ignore.add("GUI.categoryShop.items");
					ignore.add("GUI.categoryHotel.items");
					
					ConfigUpdater.update(this, "config.yml", configFile, ignore);
					this.reloadConfig();
					this.config = new UTF8YamlConfiguration(configFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		return this.config;
	}

	public File getConfigFile() {
		return new File(this.getDataFolder().getPath(), "config.yml");
	}

	public ItemManager getItemManager() {
		return this.itemManager;
	}
	
	//CHECK FOR UPDATE
	//https://www.spigotmc.org/threads/powerful-update-checker-with-only-one-line-of-code.500010/
	private void checkForUpdate() {
		
		new UpdateChecker(this, UpdateCheckSource.SPIGET, SPIGOT_RESOURCE_ID)
                .setDownloadLink(SPIGOT_RESOURCE_ID) // You can either use a custom URL or the Spigot Resource ID
                .setDonationLink("https://www.paypal.me/truemb")
                .setChangelogLink(SPIGOT_RESOURCE_ID) // Same as for the Download link: URL or Spigot Resource ID
                .setNotifyOpsOnJoin(true) // Notify OPs on Join when a new version is found (default)
                .setNotifyByPermissionOnJoin(this.getDescription().getName() + ".updatechecker") // Also notify people on join with this permission
                .setUserAgent(new UserAgentBuilder().addPluginNameAndVersion())
                .checkEveryXHours(12) // Check every hours
                .checkNow(); // And check right now
        
	}
}
