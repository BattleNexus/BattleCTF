/*******************************************************************************
 * Copyright (c) 2012 GamezGalaxy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.gamezgalaxy.ctf.gamemode.ctf.utl;

import java.util.ArrayList;

import com.gamezgalaxy.GGS.chat.ChatColor;
import com.gamezgalaxy.GGS.iomodel.Player;
import com.gamezgalaxy.GGS.world.Block;
import com.gamezgalaxy.ctf.gamemode.ctf.CTF;
import com.gamezgalaxy.ctf.main.main;
import com.gamezgalaxy.ctf.map.SafeZone;

public class Team {
	public String name;
	public String system_name;
	public ArrayList<Player> members = new ArrayList<Player>();
	public SafeZone safe = new SafeZone();
	public SafeZone area = new SafeZone();
	public int flagx;
	public int flagy;
	public int flagz;
	public Block flagblock;
	public int points;
	private short SPAWNX = -1;
	private short SPAWNY = -1;
	private short SPAWNZ = -1;
	public Team() { }
	public void spawnPlayer(Player p) {
		if (SPAWNX == -1)
			SPAWNX = (short)((((safe.getBigX() - safe.getSmallX()) / 2) + safe.getSmallX()) * 32);
		if (SPAWNY == -1)
			SPAWNY = (short)((((safe.getBigY() - safe.getSmallY()) / 2) + safe.getSmallY()) * 32);
		if (SPAWNZ == -1)
			SPAWNZ = (short)((((safe.getBigZ() - safe.getSmallZ()) / 2) + safe.getSmallZ()) * 32);
		if (main.INSTANCE.getCurrentGame() instanceof CTF) {
			CTF c = (CTF)main.INSTANCE.getCurrentGame();
			if (c.holders.containsKey(p)) {
				main.GlobalMessage(p.username + ChatColor.Dark_Red + " DROPPED THE FLAG!");
				c.resetFlag(c.holders.get(p));
				c.holders.remove(p);
				c.addDrop(p);
			}
		}
		p.setPos(SPAWNX, SPAWNY, SPAWNZ);
	}
}