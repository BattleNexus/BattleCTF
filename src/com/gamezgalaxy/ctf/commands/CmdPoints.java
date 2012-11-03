package com.gamezgalaxy.ctf.commands;

import net.mcforge.API.CommandExecutor;
import net.mcforge.API.ManualLoad;
import net.mcforge.API.plugin.PlayerCommand;
import net.mcforge.chat.ChatColor;
import net.mcforge.iomodel.Player;
import com.gamezgalaxy.ctf.gamemode.ctf.CTF;
import com.gamezgalaxy.ctf.gamemode.ctf.utl.Team;
import com.gamezgalaxy.ctf.main.main;

@ManualLoad
public class CmdPoints extends PlayerCommand {

	@Override
	public String[] getShortcuts() {
		return new String[] { "p", "points", "stat" };
	}

	@Override
	public String getName() {
		return "stats";
	}


	@Override
	public boolean isOpCommandDefault() {
		return false;
	}

	@Override
	public int getDefaultPermissionLevel() {
		return 0;
	}

	@Override
	public void execute(Player player, String[] args) {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF)) {
			player.sendMessage("Sorry, you must be playing CTF to use this command!");
			return;
		}
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		Player stats;
		if (args.length == 0) {
			stats = player;
		}
		else {
			stats = player.getServer().findPlayer(args[0]);
			if (stats == null) {
				player.sendMessage(ChatColor.Dark_Red + "Could not find player " + args[0]);
				return;
			}
		}
		int points = ctf.getPoints(stats);
		int caps = ctf.getCapture(stats);
		int drops = ctf.getDrop(stats);
		double rate = (double)((double)caps / (double)drops);
		int exp = ctf.getEXP(stats);
		int level = ctf.getLevel(stats);
		int required = ctf.getRequiredEXP(stats);
		Team team = ctf.getTeam(stats);
		player.sendMessage(ChatColor.Yellow + "Stats for: " + stats.getDisplayName());
		player.sendMessage(ChatColor.Yellow + "------------------------------------------------------------");
		player.sendMessage("GP: &a" + points);
		player.sendMessage("Flag Captures: " + caps);
		player.sendMessage("Flag Drops: " + drops);
		player.sendMessage("Flag Ratio: " + rate);
		player.sendMessage("Current Rank: " + stats.getGroup().name);
		if (team != null)
			player.sendMessage("Team: " + team.name);
		player.sendMessage("&eLevel :: &a" + level + "    &eEXP :: &a" + exp + "/" + required);
		player.sendMessage(ChatColor.Yellow + "-----------------------------------------------------------");
	}

	@Override
	public void help(CommandExecutor executor) {
		// TODO Auto-generated method stub
		
	}

}
