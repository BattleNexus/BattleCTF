/*******************************************************************************
 * Copyright (c) 2012 GamezGalaxy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.gamezgalaxy.ctf.gamemode.ctf;

import java.sql.SQLException;
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
		"&1Blue Team",
		"&4Red Team",
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
			for (Player p : t.members) {
				t.spawnPlayer(p);
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
			for (Player p : t.members) {
				if (t.safe.isSafe(p)) { //If he's inside his own field
					int minx = p.getX() - 2;
					int maxx = p.getX() + 2;
					int miny = p.getY() - 2;
					int maxy = p.getY() + 2;
					int minz = p.getZ() - 2;
					int maxz = p.getZ() + 2;
					for (Player tagged : main.INSTANCE.getServer().players) {
						if (t.members.contains(tagged))
							continue;
						if (getTeam(tagged) == null)
							continue;
						if (tagged.getX() > minx && tagged.getX() < maxx && tagged.getY() > miny && tagged.getY() < maxy && tagged.getZ() > minz && tagged.getZ() < maxz)
							tag(p, tagged);
					}
				}
			}
		}
	}
	
	public void tag(Player tagger, Player tagged) {
		getTeam(tagged).spawnPlayer(tagged); //Spawn the person who got tagged
		//Reward the tagger
		int points = Integer.parseInt((String)tagger.getValue("points"));
		points += 2;
		tagger.setValue("points", points);
		try {
			tagger.saveValue("points");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void resetFlag(Team t) {
		Player.GlobalBlockChange((short)t.flagx, (short)t.flagy, (short)t.flagz, t.flagblock, getMap().level, main.INSTANCE.getServer());
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
