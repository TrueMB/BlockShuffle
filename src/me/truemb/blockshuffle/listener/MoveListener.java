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
import me.truemb.blockshuffle.utils.PlayerManager;

public class MoveListener implements Listener {

	@EventHandler
	public void onMove(PlayerMoveEvent e) {

		Location from = e.getFrom();
		Location to = e.getTo();

		Player p = e.getPlayer();

		if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {

			UUID uuid = PlayerManager.getUUID(p.getName());

			if (Main.getPlugin().challengeStatus.get(PlayerManager.getUUID(p.getName())) == null)
				return;

			ChallengeStatus status = Main.getPlugin().challengeStatus.get(uuid);
			String tBlock = Main.getPlugin().targetBlock.get(uuid);
			Material relativeBlock = p.getLocation().getBlock().getRelative(BlockFace.DOWN).getBlockData()
					.getMaterial();
			Material inPlayerBlock = p.getLocation().getBlock().getType();
			if (status != ChallengeStatus.SEARCHING)
				return;

			if (relativeBlock == Material.valueOf(tBlock) || inPlayerBlock == Material.valueOf(tBlock)) {
				// PLAYER FOUND BLOCK
				Main.getPlugin().challengeStatus.put(uuid, ChallengeStatus.FOUND);
				Bukkit.broadcastMessage(Main.getPlugin().getMessage("blockFind").replace("PLAYERNAME", p.getName()));

				// PLAYER STILL SEARCHING?
				for (Player all : Bukkit.getOnlinePlayers()) {
					UUID uuids = PlayerManager.getUUID(all.getName());

					if (Main.getPlugin().challengeStatus.get(uuids) != null) {
						ChallengeStatus allStatus = Main.getPlugin().challengeStatus.get(uuids);

						if (allStatus == ChallengeStatus.SEARCHING)
							return;
					}
				}

				// NO PLAYER SEARCHING
				Bukkit.broadcastMessage(Main.getPlugin().getMessage("twoFoundBlock"));
				Main.getPlugin().reset();
			}

		}
	}
}
