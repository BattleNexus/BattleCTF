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
import com.gamezgalaxy.GGS.API.player.PlayerBlockChangeEvent;
import com.gamezgalaxy.GGS.API.player.PlayerCommandEvent;
import com.gamezgalaxy.GGS.iomodel.Player;
import com.gamezgalaxy.GGS.world.PlaceMode;
import com.gamezgalaxy.ctf.blocks.TNT_Explode;
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
					ctf.resetFlag(ctf.holders.get(event.getPlayer()));
					ctf.holders.remove(event.getPlayer());
				}
			}
		}
		else {
			if (event.getBlock().getVisableBlock() == 46)
				event.setBlock(new TNT_Explode(event.getPlayer()));
		}
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
					int x = ((t.safe.getBigX() - t.safe.getSmallX()) / 2) + t.safe.getSmallX();
					int y = ((t.safe.getBigY() - t.safe.getSmallY()) / 2) + t.safe.getSmallY();
					int z = ((t.safe.getBigZ() - t.safe.getSmallZ()) / 2) + t.safe.getSmallZ();
					event.getPlayer().setPos((short)(x * 32), (short)(y * 32), (short)(z * 32));
					event.Cancel(true);
					break;
				}
			}
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
			if (biggest == null || ctf.teams.get(i).members.size() < small) {
				biggest = ctf.teams.get(i);
				small = ctf.teams.get(i).members.size();
			}
		}
		return biggest;
	}
}
