package com.snowgears.battleground.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.snowgears.battleground.Battleground;
import com.snowgears.battleground.customevents.PlayerLeaveBattlegroundEvent;

public final class TeamAPI {

		public static boolean isOnRedTeam(Player player) {
			if(Battleground.plugin.playerManager.getRedTeam().contains(player.getName()))
				return true;
			return false;
		}
		
		public static boolean isOnBlueTeam(Player player) {
			if(Battleground.plugin.playerManager.getBlueTeam().contains(player.getName()))
				return true;
			return false;
		}
		
		public static boolean isSpectator(Player player) {
			if(Battleground.plugin.playerManager.getSpectators().contains(player.getName()))
				return true;
			return false;
		}
		
		public static boolean inCurrentBattleground(Player player) {
			if(Battleground.plugin.worldHandler.getCurrentWorld().equals(player.getWorld().getName()))
				return true;
			return false;
		}
		
		public static void assignToRedTeam(Player player) {
			Battleground.plugin.playerManager.removePlayerFromAllTeams(player);
			Battleground.plugin.playerManager.getRedTeam().add(player.getName());
		}
		
		public static void assignToBlueTeam(Player player) {
			Battleground.plugin.playerManager.removePlayerFromAllTeams(player);
			Battleground.plugin.playerManager.getRedTeam().add(player.getName());
		}
		
		public static void assignToSpectator(Player player) {
			Battleground.plugin.playerManager.removePlayerFromAllTeams(player);
			Battleground.plugin.playerManager.getSpectators().add(player.getName());
		}
		
		public static void removeFromBattle(Player player) {
			Battleground.plugin.playerManager.removePlayerFromAllTeams(player);
			PlayerLeaveBattlegroundEvent e = new PlayerLeaveBattlegroundEvent(Battleground.plugin, player);
			Bukkit.getServer().getPluginManager().callEvent(e);
		}
}
