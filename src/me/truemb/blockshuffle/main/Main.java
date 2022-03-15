package me.truemb.blockshuffle.main;

import java.io.File;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import me.truemb.blockshuffle.commands.ShuffleCOMMAND;
import me.truemb.blockshuffle.enums.ChallengeStatus;
import me.truemb.blockshuffle.listener.MoveListener;
import me.truemb.blockshuffle.utils.PlayerManager;
import me.truemb.blockshuffle.utils.UTF8YamlConfiguration;
import net.minecraft.server.v1_16_R2.ChatMessageType;
import net.minecraft.server.v1_16_R2.IChatBaseComponent;
import net.minecraft.server.v1_16_R2.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R2.PacketPlayOutChat;

public class Main extends JavaPlugin {
	
	private static Main main;
	public HashMap<UUID, ChallengeStatus> challengeStatus = new HashMap<>();
	public HashMap<UUID, String> targetBlock = new HashMap<>();

	public BukkitTask task;

	public int tickCount;

	public void onEnable() {
		
		Main.main = this;
		this.manageFile();

		//COMMANDS
		this.getCommand("shuffle").setExecutor(new ShuffleCOMMAND());
		
		//LISTENER
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new MoveListener(), this);
	}

	//METHODES
	public Material getRandomBlock() {
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
	
	public void reset() {
		this.tickCount = 0;
		Main.getPlugin().targetBlock.clear();
		for(Player all : Bukkit.getOnlinePlayers())
			if(Main.getPlugin().challengeStatus.get(PlayerManager.getUUID(all.getName())) != null && Main.getPlugin().challengeStatus.get(PlayerManager.getUUID(all.getName())) != ChallengeStatus.LOST)
				Main.getPlugin().challengeStatus.put(PlayerManager.getUUID(all.getName()), ChallengeStatus.IDLE);
	}
	
	public void fullReset() {
		this.tickCount = 0;
		Main.getPlugin().targetBlock.clear();
		for(Player all : Bukkit.getOnlinePlayers())
			Main.getPlugin().challengeStatus.put(PlayerManager.getUUID(all.getName()), ChallengeStatus.IDLE);
	}
	
	//ACTIONBAR
	public void sendMessage(String message, Player... players) {
		IChatBaseComponent text = ChatSerializer.a("{\"text\": \"" + message + "\"}");
		PacketPlayOutChat bar = new PacketPlayOutChat(text, ChatMessageType.GAME_INFO, null);
		for (Player p : players) {
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(bar);
		}
	}

	//CONFIG
	public String getMessage(String path) {
		String s = Main.getPlugin().manageFile().getString("Messages.prefix") + " " + Main.getPlugin().manageFile().getString("Messages." + path);
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

	//RETURN MAIN CLASS
	public static Main getPlugin() {
		return Main.main;
	}
}
