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
import java.util.Random;

import com.gamezgalaxy.GGS.chat.ChatColor;
import com.gamezgalaxy.GGS.iomodel.Player;
import com.gamezgalaxy.ctf.blocks.BlueFlag;
import com.gamezgalaxy.ctf.blocks.RedFlag;
import com.gamezgalaxy.ctf.gamemode.Gamemode;
import com.gamezgalaxy.ctf.gamemode.ctf.stalemate.Action;
import com.gamezgalaxy.ctf.gamemode.ctf.stalemate.actions.DropFlags;
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
	public ArrayList<Action> stalemate = new ArrayList<Action>();
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
		goal = main.random.nextInt(5) + 1;
		main.INSTANCE.getServer().Log("Round will require " + goal + " points");
		main.GlobalMessage("&2[GBot] In this round, your team must score &4" + goal + " &2points!");
		main.GlobalMessage("&2[GBot] The round has started! Good luck!");
		running = true;
		Thread run = new Checker();
		run.start();
	}

	@Override
	public void tick() {
		for (Team t : teams) {
			if (t.points >= goal) {
				roundEnd();
				break;
			}
		}
		if (holders == null || holders.size() == 0)
			return;
		if (holders.size() % 2 == 0) {
			if (getMap().stalemate.size() == 0)
				getMap().stalemate.add(new DropFlags());
			main.GlobalMessage(ChatColor.Dark_Green + "[GBot] " + ChatColor.Dark_Red + "STALEMATE DETECTED!");
			main.GlobalMessage(ChatColor.Dark_Green + "[GBot] " + ChatColor.Dark_Red + "Choosing a random action..");
			int index = new Random().nextInt(getMap().stalemate.size());
			Action a = getMap().stalemate.get(index);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			main.GlobalMessage(a.getMessage());
			a.performaction(this);
		}
	}
	
	public void tag(Player tagger, Player tagged) {
		getTeam(tagged).spawnPlayer(tagged); //Spawn the person who got tagged
		//Reward the tagger
		int points = 0;
		if (tagger.getValue("points") != null)
			points = Integer.parseInt(tagger.getValue("points"));
		points += 2;
		tagger.setValue("points", points);
		try {
			tagger.saveValue("points");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		main.GlobalMessage(tagger.username + " &2TAGGED&f " + tagged.username);
	}
	
	public void resetFlag(Team t) {
		Player.GlobalBlockChange((short)t.flagx, (short)t.flagy, (short)t.flagz, t.flagblock, getMap().level, main.INSTANCE.getServer());
	}

	@Override
	public void roundEnd() {
		main.INSTANCE.getServer().Log("Round end!");
		for (Team t : teams) { 
			t.points = 0;
		}
		super.dispose();
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
	
	private class Checker extends Thread {
		
		@Override
		public void run() {
			while (running) {
				for (Team t : teams) {
					for (Player p : t.members) {
						if (t.area.isSafe(p)) { //If he's inside his own field
							int minx = p.getBlockX() - 2;
							int maxx = p.getBlockX() + 2;
							int miny = p.getBlockY() - 2;
							int maxy = p.getBlockY() + 2;
							int minz = p.getBlockZ() - 2;
							int maxz = p.getBlockZ() + 2;
							for (Player tagged : main.INSTANCE.getServer().players) {
								if (t.members.contains(tagged))
									continue;
								if (getTeam(tagged) == null)
									continue;
								if (tagged.getBlockX() > minx && tagged.getBlockX() < maxx && tagged.getBlockY() > miny && tagged.getBlockY() < maxy && tagged.getBlockZ() > minz && tagged.getBlockZ() < maxz)
									tag(p, tagged);
							}
						}
					}
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
