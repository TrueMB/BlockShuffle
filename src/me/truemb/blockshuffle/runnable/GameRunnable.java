package me.truemb.blockshuffle.runnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.truemb.blockshuffle.enums.ChallengeStatus;
import me.truemb.blockshuffle.main.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class GameRunnable implements Runnable{
	
	private Main instance;
	private int shuffleTime;
	
	public GameRunnable(Main plugin) {
		this.instance = plugin;
		this.shuffleTime = this.instance.manageFile().getInt("Options.shuffleTime");
		this.instance.fullReset();
	}

	@Override
	public void run() {
			
			for (Player all : Bukkit.getOnlinePlayers()) {
				UUID uuid = all.getUniqueId();
				
				if(this.instance.challengeStatus.get(uuid) != null) {
					ChallengeStatus status = this.instance.challengeStatus.get(uuid);
					
					if (status == ChallengeStatus.IDLE && this.instance.targetBlock.get(uuid) == null) {
						
						//NEW ROUND
						Material type = this.instance.getItemManager().getRandomPhysicalMaterial();
						ItemStack item = new ItemStack(type);
						String itemName = this.instance.getItemManager().getClientItemName(item);
						
						this.instance.targetBlock.put(uuid, type);
						this.instance.challengeStatus.put(uuid, ChallengeStatus.SEARCHING);
								            
						all.sendMessage(this.instance.getMessage("blockGive")
								.replaceAll("(?i)%" + "playername" + "%", all.getDisplayName())
								.replaceAll("(?i)%" + "block" + "%", itemName != null ? itemName : "")
								);
					}
				}
				
				
				if(this.instance.tickCount <= this.shuffleTime && this.shuffleTime - this.instance.tickCount <= 10) 
				    all.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(this.instance.getMessage("countdown").replaceAll("(?i)%" + "time" + "%", String.valueOf(this.shuffleTime - this.instance.tickCount))));
			}
			
			if(this.instance.tickCount > this.shuffleTime) {

				List<Player> searching = new ArrayList<>();
				List<Player> found = new ArrayList<>();
				
				for (int i = 0; i < Bukkit.getOnlinePlayers().size(); i++) {
					Player p = (Player) Bukkit.getOnlinePlayers().toArray()[i];
					UUID uuid = p.getUniqueId();
					
					if(this.instance.challengeStatus.get(uuid) != null) {
						ChallengeStatus status = this.instance.challengeStatus.get(uuid);
						
						if(status == ChallengeStatus.SEARCHING) {
							searching.add(p);							
						}else if(status == ChallengeStatus.FOUND) {
							found.add(p);			
						}
					}
				}
				
				if(found.size() >= 2) {
					Bukkit.broadcastMessage(this.instance.getMessage("twoFoundBlock"));
				
					for(Player searchingP : searching) {
						searchingP.sendMessage(this.instance.getMessage("lose"));
						this.instance.challengeStatus.put(searchingP.getUniqueId(), ChallengeStatus.LOST);
					}
					this.instance.reset();
				}
				if(found.size() == 0 && searching.size() >= 2) {
					Bukkit.broadcastMessage(this.instance.getMessage("twoDidntFindBlock"));
					this.instance.reset();
				}
				
				if(found.size() == 1) {
					Bukkit.broadcastMessage(this.instance.getMessage("win").replaceAll("(?i)%" + "playername" + "%", found.get(0).getName()));
					this.instance.fullReset();
					this.instance.task.cancel();
				}
			}
			
			this.instance.tickCount++;
	}
}
