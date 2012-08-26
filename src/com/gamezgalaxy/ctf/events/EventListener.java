/*******************************************************************************
 * Copyright (c) 2012 GamezGalaxy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.gamezgalaxy.ctf.events;

import com.gamezgalaxy.GGS.API.EventHandler;
import com.gamezgalaxy.GGS.API.Listener;
import com.gamezgalaxy.GGS.API.level.PlayerJoinedLevel;
import com.gamezgalaxy.GGS.API.player.PlayerBlockChangeEvent;
import com.gamezgalaxy.GGS.API.player.PlayerCommandEvent;
import com.gamezgalaxy.GGS.API.player.PlayerDisconnectEvent;
import com.gamezgalaxy.GGS.chat.ChatColor;
import com.gamezgalaxy.GGS.iomodel.Player;
import com.gamezgalaxy.GGS.world.PlaceMode;
import com.gamezgalaxy.ctf.blocks.TNT_Explode;
import com.gamezgalaxy.ctf.commands.shop.ShopItem;
import com.gamezgalaxy.ctf.gamemode.ctf.CTF;
import com.gamezgalaxy.ctf.gamemode.ctf.utl.Team;
import com.gamezgalaxy.ctf.main.main;

public class EventListener implements Listener {
	@EventHandler
	public void onBreak(PlayerBlockChangeEvent event) {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF))
			return;
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		if (ctf.isOnNoTeam(event.getPlayer())) {
			event.Cancel(true);
			event.getPlayer().sendMessage("&2[GBot] You are spectating!");
			event.getPlayer().sendMessage("&2[GBot] Please join one of the follow teams:");
			for (int i = 0; i < ctf.teamcount; i++)
				event.getPlayer().sendMessage("/" + CTF.SYSTEM_TEAM_NAME[i]);
			return;
		}
		final Team team = ctf.getTeam(event.getPlayer());
		final boolean holding = ctf.holders.containsKey(event.getPlayer());
		final short X = event.getX();
		final short Y = event.getY();
		final short Z = event.getZ();
		if (event.getPlaceType() == PlaceMode.BREAK) {
			for (Team t : ctf.teams) {
				if (t.flagx == X && t.flagy == Y && t.flagz == Z && t != team && !holding && event.getPlayer().getLevel().getTile(X, Y, Z) == t.flagblock) {
					main.GlobalMessage(event.getPlayer().username + " took the " + t.name +"'s FLAG!");
					ctf.holders.put(event.getPlayer(), t);
				}
				else if (t.flagx == X && t.flagy == Y && t.flagz == Z && t != team && holding && event.getPlayer().getLevel().getTile(X, Y, Z) == t.flagblock) {
					event.getPlayer().sendMessage("&2[Gbot] You can only hold 1 flag at a time!");
					event.Cancel(true);
				}
				else if (t.flagx == X && t.flagy == Y && t.flagz == Z && t == team && !holding && event.getPlayer().getLevel().getTile(X, Y, Z) == t.flagblock) {
					event.getPlayer().sendMessage("&2[Gbot] You cant take your own flag!");
					event.Cancel(true);
				}
				else if (t.flagx == X && t.flagy == Y && t.flagz == Z && t == team && holding && event.getPlayer().getLevel().getTile(X, Y, Z) == t.flagblock) {
					main.GlobalMessage(event.getPlayer().username + " returned the flag!");
					event.Cancel(true);
					t.points++;
					ctf.addCapture(event.getPlayer());
					ctf.resetFlag(ctf.holders.get(event.getPlayer()));
					ctf.holders.remove(event.getPlayer());
					main.GlobalMessage(ChatColor.Orange + "Current Score:");
					for (Team tt : ctf.teams) {
						main.GlobalMessage(tt.name + ChatColor.White + ": " + tt.points);
					}
				}
			}
		}
		else {
			if (event.getBlock().getVisableBlock() == 46 && !ctf.tntholders.containsKey(event.getPlayer())) {
				TNT_Explode t = new TNT_Explode(event.getPlayer());
				event.setBlock(t);
				ctf.tntholders.put(event.getPlayer(), t);
				event.getPlayer().sendMessage(ChatColor.Aqua + " Place " + ChatColor.Red + "\"Brick\" " + ChatColor.Aqua + " to detonate the TNT");
			}
			else if (event.getBlock().getVisableBlock() == 46 && ctf.tntholders.containsKey(event.getPlayer()))
				event.Cancel(true);
			else if (event.getBlock().getVisableBlock() == 45 && ctf.tntholders.containsKey(event.getPlayer())) {
				ctf.tntholders.get(event.getPlayer()).explode();
				event.Cancel(true);
			}
		}
	}
	
	@EventHandler
	public void joinLevel(PlayerJoinedLevel event) {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF))
			return;
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		final Player p = event.getPlayer();
		if (ctf.getTeam(p) != null)
			ctf.getTeam(p).spawnPlayer(p);
	}
	
	@EventHandler
	public void onCommand(PlayerCommandEvent event) {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF))
			return;
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		if (ctf.isOnNoTeam(event.getPlayer())) {
			for (int i = 0; i < ctf.teamcount; i++) {
				if (CTF.SYSTEM_TEAM_NAME[i].equalsIgnoreCase(event.getCommand())) {
					Team t = ctf.teams.get(i);
					if (getBiggest() == t) {
						event.getPlayer().sendMessage("&2[GBot] " + t.name + " &2is full!");
						event.getPlayer().sendMessage("&2[GBot] Please join /" + getSmallest().system_name);
						event.Cancel(true);
						break;
					}
					t.members.add(event.getPlayer());
					main.GlobalMessage(event.getPlayer().username + " joined the " + t.name + "!");
					//Spawn the player
					t.spawnPlayer(event.getPlayer());
					event.Cancel(true);
					break;
				}
			}
		}
		else {
			for (ShopItem item : main.INSTANCE.getShop().items) {
				if (ctf.getLevel(event.getPlayer()) >= item.getLevel()) {
					if (item.getName().equalsIgnoreCase(event.getCommand())) {
						String[] args = new String[event.getArgs().size()];
						item.execute(event.getPlayer(), event.getArgs().toArray(args));
						event.Cancel(true);
					}
				}
			}
			if (event.getCommand().equalsIgnoreCase("spawn")) {
				Team t = ctf.getTeam(event.getPlayer());
				t.spawnPlayer(event.getPlayer());
				event.Cancel(true);
			}
		}
	}
	
	@EventHandler
	public void onDisconnect(PlayerDisconnectEvent event) {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF))
			return;
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		if (ctf.getTeam(event.getPlayer()) != null) {
			final Team t = ctf.getTeam(event.getPlayer());
			t.members.remove(event.getPlayer());
			main.GlobalMessage(ChatColor.Dark_Red + event.getPlayer().username + " has left the " + t.name);
		}
	}
	
	public Team getBiggest() {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF))
			return null;
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		int big = 0;
		Team biggest = null;
		for (int i = 0; i < ctf.teams.size(); i++) {
			if (biggest == null || ctf.teams.get(i).members.size() > big) {
				biggest = ctf.teams.get(i);
				big = ctf.teams.get(i).members.size();
			}
		}
		return biggest;
	}
	public Team getSmallest() {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF))
			return null;
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		int small = 0;
		Team biggest = null;
		for (int i = 0; i < ctf.teams.size(); i++) {
			if (ctf.teams.get(i).members.size() == 0)
				return ctf.teams.get(i);
			if (biggest == null || ctf.teams.get(i).members.size() < small) {
				biggest = ctf.teams.get(i);
				small = ctf.teams.get(i).members.size();
			}
		}
		return biggest;
	}
}
