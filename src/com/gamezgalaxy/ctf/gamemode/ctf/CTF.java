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
import com.gamezgalaxy.ctf.blocks.TNT_Explode;
import com.gamezgalaxy.ctf.commands.shop.ShopItem;
import com.gamezgalaxy.ctf.events.PlayerTaggedEvent;
import com.gamezgalaxy.ctf.exceptions.VoteDidntStartException;
import com.gamezgalaxy.ctf.gamemode.Gamemode;
import com.gamezgalaxy.ctf.gamemode.ctf.stalemate.Action;
import com.gamezgalaxy.ctf.gamemode.ctf.stalemate.actions.DropFlags;
import com.gamezgalaxy.ctf.gamemode.ctf.utl.Team;
import com.gamezgalaxy.ctf.gamemode.ctf.utl.Voter;
import com.gamezgalaxy.ctf.main.main;

public class CTF extends Gamemode {

	public static final String[] TEAM_NAMES = new String[] {
		"&1Blue Team",
		"&4Red Team",
		"&2Green Team",
		ChatColor.Purple + "Purple Team",
		ChatColor.Yellow + "Yellow Team",
		ChatColor.Grey + "Iron Team",
		ChatColor.Orange + "Gold Team",
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
	public HashMap<Player, HashMap<Player, Integer>> dominate = new HashMap<Player, HashMap<Player, Integer>>();
	public HashMap<Player, TNT_Explode> tntholders = new HashMap<Player, TNT_Explode>();
	public ArrayList<Player> tagged = new ArrayList<Player>();
	public int teamcount;
	public int goal;
	public boolean started;
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
			p.clearChatScreen();
			i++;
		}
		//Place the flags
		for (Team t : teams) {
			resetFlag(t);
			for (Player p : t.members) {
				t.spawnPlayer(p);
				t.setColor(p);
			}
		}
		goal = main.random.nextInt(5) + 1;
		running = true;
		Thread run = new Checker();
		run.start();
		for (int i1 = 20; i1 > 0; i1--) {
			if (i1 % 5 == 0 || i < 10)
				main.GlobalMessage(ChatColor.Dark_Green + "[GBot] Round will start in: " + (i1 < 10 ? ChatColor.Dark_Red : ChatColor.Dark_Aqua) + i1);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) { }
			main.GlobalClear();
		}
		started = true;
		main.INSTANCE.getServer().Log("Round will require " + goal + " points");
		main.GlobalMessage("&2[GBot] In this round, your team must score &4" + goal + " &2points!");
		main.GlobalMessage("&2[GBot] The round has started! Good luck!");
		resetClients();
		for (Team t : teams) {
			for (Player p : t.members) {
				p.sendMessage("You are on the " + t.name);
			}
		}
	}
	public void resetClients() {
		for (Player p : main.INSTANCE.getServer().players) {
			for (Player pp : main.INSTANCE.getServer().players) {
				if (p != pp && p.getLevel() == pp.getLevel()) {
					pp.Despawn(p);
					pp.spawnPlayer(p);
				}
			}
		}
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
		PlayerTaggedEvent event = new PlayerTaggedEvent(tagged, tagger, this);
		main.INSTANCE.getServer().getEventSystem().callEvent(event);
		if (event.isCancelled())
			return;
		getTeam(tagged).spawnPlayer(tagged); //Spawn the person who got tagged
		//Reward the tagger
		rewardPlayer(tagger, 2);
		try {
			tagger.saveValue("points");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		main.GlobalMessage(tagger.username + " &2TAGGED&f " + tagged.username);
	}
	
	public void rewardPlayer(Player p, int amount) {
		setValue(p, "points", amount, true);
		saveValue(p, "points");
	}
	public int getPoints(Player p) {
		return getValue(p, "points");
	}
	
	public int getKillStreak(Player p) {
		return getValue(p, "killstreak");
	}
	
	public void setKillStreak(Player p, int amount) {
		setValue(p, "killstreak", amount, false);
	}
	
	public void addCapture(Player p) {
		setValue(p, "capture", 1, true);
		saveValue(p, "capture");
		setValue(p, "caps", 1, true);
	}
	
	public void addDrop(Player p) {
		setValue(p, "drop", 1, true);
		saveValue(p, "drop");
		setValue(p, "balls", 1, true);
	}
	
	public int getCapture(Player p) {
		return getValue(p, "capture");
	}
	
	public int getDrop(Player p) {
		return getValue(p, "drop");
	}
	
	public void addEXP(Player p, int amount) {
		setValue(p, "exp", amount, true);
		saveValue(p, "exp");
	}
	public void resetEXP(Player p) {
		setValue(p, "exp", 0, false);
	}
	
	public void levelUp(Player p) {
		setValue(p, "level", 1, true);
		saveValue(p, "level");
	}
	
	public int getEXP(Player p) {
		return getValue(p, "exp");
	}
	
	public int getLevel(Player p) {
		return getValue(p, "level");
	}
	
	public int getRequiredEXP(Player p) {
		int lvl = getLevel(p);
		int required = 0;
		for (int i = lvl; i > 0; --i)
			required += (lvl * 100) / 2;
		required += lvl * 100;
		return required;
	}
	
	public int capturesThisRound(Player p) {
		return getValue(p, "caps");
	}
	
	public void resetCapturesthisRound(Player p) {
		setValue(p, "caps", 0, false);
	}
	
	public int dropsThisRound(Player p) {
		return getValue(p, "balls");
	}
	
	public void resetDropsthisRound(Player p) {
		setValue(p, "balls", 0, false);
	}
	
	public void setValue(Player p, String setting, int value, boolean add) {
		if (add) {
			int points = getValue(p, setting);
			points += value;
			p.setValue(setting, points);
		}
		else
			p.setValue(setting, value);
	}
	public void saveValue(Player p, String setting) {
		try {
			p.saveValue(setting);
		} catch (SQLException e) { main.INSTANCE.getServer().Log("Could not save " + p.username + " " + setting + "..."); }
	}
	public int getValue(Player p, String setting) {
		int points = 0;
		if (p.getValue(setting) != null)
			points = Integer.parseInt(p.getValue(setting));
		return points;
	}

	public void resetFlag(Team t) {
		Player.GlobalBlockChange((short)t.flagx, (short)t.flagy, (short)t.flagz, t.flagblock, getMap().level, main.INSTANCE.getServer());
	}

	@Override
	public void roundEnd() {
		main.INSTANCE.getServer().Log("Round end!");
		main.GlobalMessage(ChatColor.Dark_Green + "The game is over!");
		main.GlobalMessage("The " + getWinner().name + ChatColor.White + " has won this round!");
		for (Player p : getWinner().members) {
			rewardPlayer(p, 10);
			this.addEXP(p, 50 * teams.size());
		}
		for (Team t : teams) { 
			t.points = 0;
		}
		for (Player p : main.INSTANCE.getServer().players) {
			int caps = getCapture(p);
			if (caps == 0)
				caps = 1;
			int drops = getDrop(p);
			if (drops == 0)
				drops = 1;
			int temp = capturesThisRound(p);
			if (temp == 0)
				temp = 1;
			int temp2 = dropsThisRound(p);
			if (temp2 == 0)
				temp2 = 1;
			double rate = (double)((double)caps / (double)drops);
			int EXP = (int)((10 * temp / temp2) * rate);
			addEXP(p, EXP);
			resetCapturesthisRound(p);
			resetDropsthisRound(p);
		}
		this.dominate.clear();
		this.holders.clear();
		this.tntholders.clear();
		try {
			Thread.sleep(10000);
			vote();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (VoteDidntStartException e) {
			e.printStackTrace();
		}
		super.dispose();
	}
	
	private void vote() throws VoteDidntStartException, InterruptedException {
		Voter v = new Voter(this, main.INSTANCE.getServer());
		v.start();
		main.GlobalMessage(ChatColor.Dark_Green + "[GBot] " + ChatColor.Dark_Red + "You will have 30 seconds to vote..");
		Thread.sleep(30000);
		v.end();
		main.GlobalMessage(ChatColor.Dark_Green + "[GBot] " + ChatColor.White + "Voting has ending!");
		String winner = v.getResults();
		main.INSTANCE.setNextMap(winner);
		main.GlobalMessage(ChatColor.Dark_Green + "[GBot] " + ChatColor.White + winner + ChatColor.White + " won the vote!");
		main.GlobalMessage(ChatColor.Dark_Green + "[GBot] " + ChatColor.Dark_Red + "Starting next round...");
		v.reset();
		Thread.sleep(5000);
	}
	
	public Team getWinner() {
		for (Team t : teams) {
			if (t.points >= goal)
				return t;
		}
		return null;
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
						if (!started) {
							if (t.safe.isSafe(p))
								t.spawnPlayer(p);
						}
						else {
							if (getEXP(p) >= getRequiredEXP(p)) {
								resetEXP(p);
								levelUp(p);
								p.sendMessage(ChatColor.Bright_Green + "Level Up!");
								int level = getLevel(p);
								String message;
								for (ShopItem item : main.INSTANCE.getShop().items) {
									if (item.getLevel() < level)
										message = item.getLevelUpMessage(p);
									else
										message = item.checkUnlock(level);
									if (message == null || message.equals(""))
										continue;
									p.sendMessage(ChatColor.Bright_Green + "+ " + message);
								}
							}
							if (t.isSafe(p)) { //If he's inside his own field
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
