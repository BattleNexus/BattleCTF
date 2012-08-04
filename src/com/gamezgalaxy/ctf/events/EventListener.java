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
import com.gamezgalaxy.GGS.world.PlaceMode;
import com.gamezgalaxy.ctf.gamemode.ctf.CTF;
import com.gamezgalaxy.ctf.gamemode.ctf.utl.Team;
import com.gamezgalaxy.ctf.main.main;

public class EventListener implements Listener {
	@EventHandler
	public void onBreak(PlayerBlockChangeEvent event) {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF))
			return;
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		final Team team = ctf.getTeam(event.getPlayer());
		final boolean holding = ctf.holders.containsKey(event.getPlayer());
		final short X = event.getX();
		final short Y = event.getY();
		final short Z = event.getZ();
		if (event.getPlaceType() == PlaceMode.BREAK) {
			for (Team t : ctf.teams) {
				if (t.flagx == X && t.flagy == Y && t.flagz == Z && t != team && !holding) {
					main.GlobalMessage(event.getPlayer().username + " took the " + t.name +"'s FLAG!");
					ctf.holders.put(event.getPlayer(), t);
				}
				else if (t.flagx == X && t.flagy == Y && t.flagz == Z && t != team && holding) {
					event.getPlayer().sendMessage("&2[Gbot] You can only hold 1 flag at a time!");
					event.Cancel(true);
				}
				else if (t.flagx == X && t.flagy == Y && t.flagz == Z && t == team && !holding) {
					event.getPlayer().sendMessage("&2[Gbot] You cant take your own flag!");
					event.Cancel(true);
				}
				else if (t.flagx == X && t.flagy == Y && t.flagz == Z && t == team && holding) {
					main.GlobalMessage(event.getPlayer().username + " returned the flag!");
					event.Cancel(true);
					t.points++;
					ctf.resetFlag(ctf.holders.get(event.getPlayer()));
					ctf.holders.remove(event.getPlayer());
				}
			}
		}
	}

}
