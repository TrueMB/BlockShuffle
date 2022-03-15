package me.truemb.blockshuffle.runnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.truemb.blockshuffle.enums.ChallengeStatus;
import me.truemb.blockshuffle.main.Main;
import me.truemb.blockshuffle.utils.PlayerManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;

public class GameRunnable implements Runnable{
	
	private int shuffleTime;
	
	public GameRunnable() {
		this.shuffleTime = Main.getPlugin().manageFile().getInt("Options.shuffleTime");
		Main.getPlugin().fullReset();
	}

	@Override
	public void run() {
			
			for (Player all : Bukkit.getOnlinePlayers()) {
				UUID uuid = PlayerManager.getUUID(all.getName());
				
				if(Main.getPlugin().challengeStatus.get(uuid) != null) {
					ChallengeStatus status = Main.getPlugin().challengeStatus.get(uuid);
					
					if (status == ChallengeStatus.IDLE && Main.getPlugin().targetBlock.get(uuid) == null) {
						
						//NEUE RUNDE
						String tBlock = Main.getPlugin().getRandomBlock().toString();
						Main.getPlugin().targetBlock.put(uuid, tBlock);
						Main.getPlugin().challengeStatus.put(uuid, ChallengeStatus.SEARCHING);
						
						net.minecraft.server.v1_16_R2.ItemStack nmsItem = CraftItemStack.asNMSCopy(new ItemStack(Material.valueOf(tBlock)));
			            TranslatableComponent comp = new TranslatableComponent(nmsItem.getItem().getName());			            
			            TextComponent main = new TextComponent(Main.getPlugin().getMessage("blockGive").replace("PLAYERNAME", all.getDisplayName()));
			            main.addExtra(comp);
			            main.addExtra(new TextComponent("!"));
			            all.spigot().sendMessage(main);
						
						//all.sendMessage(Main.getPlugin().getMessage("blockGive").replace("PLAYERNAME", all.getDisplayName()).replace("TARGETBLOCK", tBlock));
					}
				}
				
				
				if(Main.getPlugin().tickCount <= this.shuffleTime && this.shuffleTime - Main.getPlugin().tickCount <= 10) 
				    all.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Main.getPlugin().getMessage("countdown").replace("TIME", String.valueOf(this.shuffleTime - Main.getPlugin().tickCount))));
			}
			
			if(Main.getPlugin().tickCount > this.shuffleTime) {

				List<Player> searching = new ArrayList<>();
				List<Player> found = new ArrayList<>();
				
				for (int i = 0; i < Bukkit.getOnlinePlayers().size(); i++) {
					Player p = (Player) Bukkit.getOnlinePlayers().toArray()[i];
					UUID uuid = PlayerManager.getUUID(p.getName());
					
					if(Main.getPlugin().challengeStatus.get(uuid) != null) {
						ChallengeStatus status = Main.getPlugin().challengeStatus.get(uuid);
						
						if(status == ChallengeStatus.SEARCHING) {
							searching.add(p);							
						}else if(status == ChallengeStatus.FOUND) {
							found.add(p);			
						}
					}
				}
				
				if(found.size() >= 2) {
					Bukkit.broadcastMessage(Main.getPlugin().getMessage("twoFoundBlock"));
				
					for(Player searchingP : searching) {
						searchingP.sendMessage(Main.getPlugin().getMessage("lose"));
						Main.getPlugin().challengeStatus.put(PlayerManager.getUUID(searchingP.getName()), ChallengeStatus.LOST);
					}
					Main.getPlugin().reset();
				}
				if(found.size() == 0 && searching.size() >= 2) {
					Bukkit.broadcastMessage(Main.getPlugin().getMessage("twoDidntFindBlock"));
					Main.getPlugin().reset();
				}
				
				if(found.size() == 1) {
					Bukkit.broadcastMessage(Main.getPlugin().getMessage("win").replace("PLAYERNAME", found.get(0).getName()));
					Main.getPlugin().fullReset();
					Main.getPlugin().task.cancel();
				}
			}
			
			Main.getPlugin().tickCount++;
	}
}
