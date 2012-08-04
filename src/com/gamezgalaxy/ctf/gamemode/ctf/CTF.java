/*******************************************************************************
 * Copyright (c) 2012 GamezGalaxy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.gamezgalaxy.ctf.gamemode.ctf;

import java.util.ArrayList;
import java.util.HashMap;

import com.gamezgalaxy.GGS.iomodel.Player;
import com.gamezgalaxy.ctf.blocks.BlueFlag;
import com.gamezgalaxy.ctf.blocks.RedFlag;
import com.gamezgalaxy.ctf.gamemode.Gamemode;
import com.gamezgalaxy.ctf.gamemode.ctf.utl.Team;
import com.gamezgalaxy.ctf.main.main;

public class CTF extends Gamemode {

	public static final String[] TEAM_NAMES = new String[] {
		"Blue Team",
		"Red Team",
		"Green Team",
		"Purple Team",
		"Yellow Team",
		"Iron Team",
		"Gold Team",
		"Team Derpy"
	};
	public static final String[] SYSTEM_TEAM_NAME = new String[] {
		"blue",
		"red",
		"green",
		"purple",
		"yellow",
		"iron",
		"gold",
		"derpy"
	};
	public ArrayList<Team> teams = new ArrayList<Team>();
	public HashMap<Player, Team> holders = new HashMap<Player, Team>();
	public int teamcount;
	public int goal;
	public Player blueholder;
	public Player redholder;
	@Override
	public void roundStart() {
		//Set each team
		this.teamcount = getMap().teamcount;
		for (int i = 0; i < teamcount; i++) {
			teams.add(getMap().teams.get(i));
		}
		//Place each player on a team
		int i = 0;
		for (Player p : main.INSTANCE.getServer().players) {
			if (i >= teamcount)
				i = 0;
			teams.get(i).members.add(p);
		}
		//Place the flags
		for (Team t : teams) {
			resetFlag(t);
			int x = ((t.safe.getBigX() - t.safe.getSmallX()) / 2) + t.safe.getSmallX();
			int y = ((t.safe.getBigY() - t.safe.getSmallY()) / 2) + t.safe.getSmallY();
			int z = ((t.safe.getBigZ() - t.safe.getSmallZ()) / 2) + t.safe.getSmallZ();
			for (Player p : t.members) {
				p.setPos((short)(x * 32), (short)(y * 32), (short)(z * 32)); //Multiply by 32 to convert to player pos
			}
		}
		goal = main.random.nextInt(5);
		main.GlobalMessage("&2[GBot] In this round, your team must score &4" + goal + " &2points!");
		main.GlobalMessage("&2[GBot] The round has started! Good luck!");
		running = true;
	}

	@Override
	public void tick() {
		for (Team t : teams) {
			if (t.points >= goal) {
				roundEnd();
				break;
			}
		}
	}
	
	public void resetFlag(Team t) {
		getMap().level.setTile(t.flagblock, t.flagx, t.flagy, t.flagz, main.INSTANCE.getServer());
	}

	@Override
	public void roundEnd() {
		running = false;
	}
	
	public Team getTeam(Player p) {
		for (Team t : teams) {
			for (Player pp : t.members) {
				if (pp.username.equalsIgnoreCase(p.username))
					return t;
			}
		}
		return null;
	}
	public boolean isOnNoTeam(Player p) {
		return getTeam(p) == null;
	}

}
