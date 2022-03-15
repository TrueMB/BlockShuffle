package me.truemb.blockshuffle.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.truemb.blockshuffle.enums.ChallengeStatus;
import me.truemb.blockshuffle.main.Main;

public class MoveListener implements Listener {
	
	private Main instance;
	
	public MoveListener(Main plugin) {
		this.instance = plugin;
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {

		Location from = e.getFrom();
		Location to = e.getTo();

		Player p = e.getPlayer();

		if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {

			UUID uuid = p.getUniqueId();

			if (this.instance.challengeStatus.get(uuid) == null)
				return;

			ChallengeStatus status = this.instance.challengeStatus.get(uuid);
			String tBlock = this.instance.targetBlock.get(uuid);
			Material relativeBlock = p.getLocation().getBlock().getRelative(BlockFace.DOWN).getBlockData().getMaterial();
			Material inPlayerBlock = p.getLocation().getBlock().getType();
			if (status != ChallengeStatus.SEARCHING)
				return;

			if (relativeBlock == Material.valueOf(tBlock) || inPlayerBlock == Material.valueOf(tBlock)) {
				// PLAYER FOUND BLOCK
				this.instance.challengeStatus.put(uuid, ChallengeStatus.FOUND);
				Bukkit.broadcastMessage(this.instance.getMessage("blockFind").replace("PLAYERNAME", p.getName()));

				// PLAYER STILL SEARCHING?
				for (Player all : Bukkit.getOnlinePlayers()) {
					UUID uuids = all.getUniqueId();

					if (this.instance.challengeStatus.get(uuids) != null) {
						ChallengeStatus allStatus = this.instance.challengeStatus.get(uuids);

						if (allStatus == ChallengeStatus.SEARCHING)
							return;
					}
				}

				// NO PLAYER SEARCHING
				Bukkit.broadcastMessage(this.instance.getMessage("twoFoundBlock"));
				this.instance.reset();
			}

		}
	}
}
