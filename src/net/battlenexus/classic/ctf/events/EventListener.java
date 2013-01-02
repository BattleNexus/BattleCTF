/*******************************************************************************
 * Copyright (c) 2012 GamezGalaxy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.battlenexus.classic.ctf.events;

import java.io.IOException;
import java.io.NotSerializableException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import net.mcforge.API.EventHandler;
import net.mcforge.API.Listener;
import net.mcforge.API.level.PlayerJoinedLevel;
import net.mcforge.API.player.PlayerBlockChangeEvent;
import net.mcforge.API.player.PlayerCommandEvent;
import net.mcforge.API.player.PlayerDisconnectEvent;
import net.mcforge.API.player.PlayerLoginEvent;
import net.mcforge.API.player.PlayerMoveEvent;
import net.mcforge.API.server.ServerStartedEvent;
import net.mcforge.chat.ChatColor;
import net.mcforge.iomodel.Player;
import net.mcforge.server.Tick;
import net.mcforge.world.Block;
import net.mcforge.world.PlaceMode;

import net.battlenexus.classic.ctf.blocks.Mine;
import net.battlenexus.classic.ctf.blocks.TNT_Explode;
import net.battlenexus.classic.ctf.commands.shop.ShopItem;
import net.battlenexus.classic.ctf.gamemode.ctf.CTF;
import net.battlenexus.classic.ctf.gamemode.ctf.utl.Team;
import net.battlenexus.classic.ctf.main.main;

public class EventListener implements Listener, Tick {
	private HashMap<Player, Data> flagfloat = new HashMap<Player, Data>();
	public ArrayList<Vector> tempflags = new ArrayList<Vector>();
	public ArrayList<Player> tagged = new ArrayList<Player>();
	
	public EventListener() {
		main.INSTANCE.getServer().Add(this);
	}
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF))
			return;
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		if (!ctf.isRunning())
			return;
		if (event.getPlayer().getLevel() != ctf.getMap().level)
			return;
		checkTag(event.getPlayer(), ctf);
		if (flagfloat.containsKey(event.getPlayer())) {
			Data temp = flagfloat.get(event.getPlayer());
			int x = temp.pos[0];
			int y = temp.pos[1];
			int z = temp.pos[2];
			Block oldb = temp.oldblock;
			if (oldb == null)
				oldb = Block.getBlock("Air");
			if (x != event.getPlayer().getBlockX() || y != event.getPlayer().getBlockY() || z != event.getPlayer().getBlockZ()) {
				Player.GlobalBlockChange((short)x, (short)(y + 3), (short)z, oldb, event.getPlayer().getLevel(), event.getPlayer().getServer());
				x = event.getPlayer().getBlockX();
				y = event.getPlayer().getBlockY();
				z = event.getPlayer().getBlockZ();
				Block tile = event.getPlayer().getLevel().getTile(x, y + 3, z);
				Player.GlobalBlockChange((short)x, (short)(y + 3), (short)z, ctf.holders.get(event.getPlayer()).flagblock, event.getPlayer().getLevel(), event.getPlayer().getServer());
				Data temp1 = new Data();
				temp1.pos = new int[] {
						x,
						y,
						z
				};
				temp1.oldblock = tile;
				flagfloat.remove(event.getPlayer());
				flagfloat.put(event.getPlayer(), temp1);
			}
		}
	}
	public void checkTag(Player p, CTF ctf) {
		if (ctf == null)
			return;
		Team t = ctf.getTeam(p);
		if (t == null)
			return;
		if (t.isSafe(p)) { //If he's inside his own field
			int minx = p.getBlockX() - 2;
			int maxx = p.getBlockX() + 2;
			int miny = p.getBlockY() - 2;
			int maxy = p.getBlockY() + 2;
			int minz = p.getBlockZ() - 2;
			int maxz = p.getBlockZ() + 2;
			for (int i = 0; i < main.INSTANCE.getServer().getPlayers().size(); i++) {
				final Player tagged = main.INSTANCE.getServer().getPlayers().get(i);
				if (main.INSTANCE.getEvents().tagged.contains(tagged))
					continue;
				if (t.members.contains(tagged))
					continue;
				if (ctf.getTeam(tagged) == null)
					continue;
				if (tagged.getBlockX() > minx && tagged.getBlockX() < maxx && tagged.getBlockY() > miny && tagged.getBlockY() < maxy && tagged.getBlockZ() > minz && tagged.getBlockZ() < maxz)
					ctf.tag(p, tagged);
			}
		}
	}
	public void clear() {
		flagfloat.clear();
	}
	public void drop(final Player p, final Block block) {
		Data temp = flagfloat.get(p);
		flagfloat.remove(p);
		final int x = temp.pos[0];
		final int y = temp.pos[1];
		final int z = temp.pos[2];
		final Block oldb = temp.oldblock;
		Player.GlobalBlockChange((short)x, (short)(y + 3), (short)z, oldb, p.getLevel(), p.getServer());
		Thread t = new Thread() {
			
			@Override
			public void run() {
				int temp = y + 3;
				System.out.println(p.getLevel().getTile(x, temp - 1, z).name);
				while (p.getLevel().getTile(x, temp - 1, z).name.equals("Air") || p.getLevel().getTile(x, temp - 1, z).getVisibleBlock() == 10) {
					Player.GlobalBlockChange((short)x, (short)temp, (short)z, Block.getBlock("Air"), p.getLevel(), p.getServer());
					temp--;
					Player.GlobalBlockChange((short)x, (short)temp, (short)z, block, p.getLevel(), p.getServer());
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) { }
					System.out.println(p.getLevel().getTile(x, temp - 1, z).name);
				}
				tempflags.add(new Vector(x, temp, z));
				Player.GlobalBlockChange((short)x, (short)temp, (short)z, block, p.getLevel(), p.getServer());
			}
		};
		t.start();
	}
	
	@EventHandler
	public void onAllLoad(ServerStartedEvent event) {
		if (event.getServer().getLevelHandler().findLevel("ctf") != null)
			event.getServer().getLevelHandler().unloadLevel(event.getServer().getLevelHandler().findLevel("ctf"), false);
		if (event.getServer().getLevelHandler().findLevel("ctf2") != null)
			event.getServer().getLevelHandler().unloadLevel(event.getServer().getLevelHandler().findLevel("ctf2"), false);
		main.INSTANCE.start();
	}
	
	@EventHandler
	public void onBreak(PlayerBlockChangeEvent event) {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF))
			return;
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		if (event.getPlayer().getLevel() != ctf.getMap().level)
			return;
		if (ctf.isOnNoTeam(event.getPlayer())) {
			event.setCancel(true);
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
		Vector v = new Vector(X, Y, Z);
		for (Team t : ctf.teams) {
			if (t.safe.isSafe(X, Y, Z)) {
				event.getPlayer().sendMessage(ChatColor.Dark_Red + "You cant build inside the a safezone!");
				event.setCancel(true);
				return;
			}
		}
		if (event.getPlaceType() == PlaceMode.BREAK) {
			for (Team t : ctf.teams) {
				if (t.flagx == X && t.flagy == Y && t.flagz == Z && t != team && !holding && event.getPlayer().getLevel().getTile(X, Y, Z) == t.flagblock) {
					main.GlobalMessage(event.getPlayer().username + " took the " + t.name +"'s FLAG!");
					ctf.holders.put(event.getPlayer(), t);
					Data d = new Data();
					d.pos = new int[] {
							0,
							0,
							0
					};
					d.oldblock = Block.getBlock("Air");
					flagfloat.put(event.getPlayer(), d);
				}
				else if (tempflags.contains(v) && t != team && !holding && event.getPlayer().getLevel().getTile(X, Y, Z) == t.flagblock) {
					tempflags.remove(v);
					main.GlobalMessage(event.getPlayer().username + " picked up " + t.name +"'s FLAG!");
					ctf.holders.put(event.getPlayer(), t);
					Data d = new Data();
					d.pos = new int[] {
							0,
							0,
							0
					};
					d.oldblock = Block.getBlock("Air");
					flagfloat.put(event.getPlayer(), d);
				}
				else if (tempflags.contains(v) && t == team && event.getPlayer().getLevel().getTile(X, Y, Z) == t.flagblock) {
					tempflags.remove(v);
					main.GlobalMessage(event.getPlayer().username + " returned the flag!");
					ctf.resetFlag(t);
				}
				else if (t.flagx == X && t.flagy == Y && t.flagz == Z && t != team && holding && event.getPlayer().getLevel().getTile(X, Y, Z) == t.flagblock) {
					event.getPlayer().sendMessage("&2[Gbot] You can only hold 1 flag at a time!");
					event.setCancel(true);
				}
				else if (t.flagx == X && t.flagy == Y && t.flagz == Z && t == team && !holding && event.getPlayer().getLevel().getTile(X, Y, Z) == t.flagblock) {
					event.getPlayer().sendMessage("&2[Gbot] You cant take your own flag!");
					event.setCancel(true);
				}
				else if (t.flagx == X && t.flagy == Y && t.flagz == Z && t == team && holding && event.getPlayer().getLevel().getTile(X, Y, Z) == t.flagblock) {
					main.GlobalMessage(event.getPlayer().username + " returned the flag!");
					event.setCancel(true);
					Data temp = flagfloat.get(event.getPlayer());
					flagfloat.remove(event.getPlayer());
					int x = temp.pos[0];
					int y = temp.pos[1];
					int z = temp.pos[2];
					Block b = temp.oldblock;
					Player.GlobalBlockChange((short)x, (short)(y + 3), (short)z, b, event.getPlayer().getLevel(), event.getPlayer().getServer());
					t.points++;
					ctf.addCapture(event.getPlayer());
					ctf.rewardCap(event.getPlayer());
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
			if (event.getBlock().getVisibleBlock() == 34) {
				if (!event.getPlayer().hasAttribute("mine")) {
					event.getPlayer().sendMessage(ChatColor.Dark_Red + "You have no mines!");
					event.getPlayer().sendMessage("Buy some in the /shop!");
					event.setCancel(true);
					return;
				}
				int value = event.getPlayer().getAttribute("mine");
				if (value == 0) {
					event.getPlayer().sendMessage(ChatColor.Dark_Red + "You have no mines!");
					event.getPlayer().sendMessage("Buy some in the /shop!");
					event.setCancel(true);
					return;
				}
				value--;
				Mine m = new Mine(event.getPlayer(), event.getServer());
				m.setPos(event.getX(), event.getY(), event.getZ());
				event.setBlock(m);
				event.getPlayer().sendMessage(ChatColor.Aqua + " You have " + ChatColor.Dark_Red + value + ChatColor.Aqua + " mines left!");
				event.getPlayer().setAttribute("mine", value);
				try {
					event.getPlayer().saveAttribute("mine");
				} catch (NotSerializableException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (event.getBlock().getVisibleBlock() == 46 && !ctf.tntholders.containsKey(event.getPlayer())) {
				TNT_Explode t = new TNT_Explode(event.getPlayer(), event.getServer());
				t.setPos(event.getX(), event.getY(), event.getZ());
				event.setBlock(t);
				ctf.tntholders.put(event.getPlayer(), t);
				event.getPlayer().sendMessage(ChatColor.Aqua + " Place " + ChatColor.Red + "\"Brick\" " + ChatColor.Aqua + "to detonate the TNT");
			}
			else if (event.getBlock().getVisibleBlock() == 46 && ctf.tntholders.containsKey(event.getPlayer()))
				event.setCancel(true);
			else if (event.getBlock().getVisibleBlock() == 45 && ctf.tntholders.containsKey(event.getPlayer())) {
				if (!event.getLevel().getTile(ctf.tntholders.get(event.getPlayer()).getX(), ctf.tntholders.get(event.getPlayer()).getY(), ctf.tntholders.get(event.getPlayer()).getZ()).name.equals("TNTEXE"))
					ctf.tntholders.remove(event.getPlayer());
				else {
					TNT_Explode t = (TNT_Explode)event.getLevel().getTile(ctf.tntholders.get(event.getPlayer()).getX(), ctf.tntholders.get(event.getPlayer()).getY(), ctf.tntholders.get(event.getPlayer()).getZ());
					t.explode();
					event.setCancel(true);
				}
			}
		}
	}
	
	@EventHandler
	public void joinLevel(PlayerJoinedLevel event) {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF))
			return;
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		if (event.getPlayer().getLevel() != ctf.getMap().level)
			return;
		final Player p = event.getPlayer();
		if (ctf.getTeam(p) != null)
			ctf.getTeam(p).spawnPlayer(p);
	}
	
	@EventHandler
	public void onCommand(PlayerCommandEvent event) {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF))
			return;
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		if (event.getPlayer().getLevel() != ctf.getMap().level)
			return;
		if (ctf.isOnNoTeam(event.getPlayer())) {
			for (int i = 0; i < ctf.teamcount; i++) {
				if (CTF.SYSTEM_TEAM_NAME[i].equalsIgnoreCase(event.getCommand())) {
					Team t = ctf.teams.get(i);
					if (t.members.size() > 0 && getBiggest() == t) {
						event.getPlayer().sendMessage("&2[GBot] " + t.name + " &2is full!");
						event.getPlayer().sendMessage("&2[GBot] Please join /" + getSmallest().system_name);
						event.setCancel(true);
						break;
					}
					t.members.add(event.getPlayer());
					main.GlobalMessage(event.getPlayer().username + " joined the " + t.name + "!");
					//Spawn the player
					t.spawnPlayer(event.getPlayer());
					t.setColor(event.getPlayer());
					event.setCancel(true);
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
						event.setCancel(true);
					}
				}
			}
			if (event.getCommand().equalsIgnoreCase("spawn")) {
				Team t = ctf.getTeam(event.getPlayer());
				t.spawnPlayer(event.getPlayer());
				event.setCancel(true);
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
	
	@EventHandler
	public void onConnect(PlayerLoginEvent event) {
		event.getPlayer().sendMessage("Welcome to " + ChatColor.Dark_Red + "BattleNexus " + ChatColor.White + " Capture the Flag!");
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		for (Team t : ctf.teams)
			event.getPlayer().sendMessage("Type /" + t.system_name + " to join the " + t.name);
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
	public class Vector {
		public int X;
		public int Y;
		public int Z;
		public int tick;
		
		public Vector(int x, int y, int z) {
			this.X = x;
			this.Y = y;
			this.Z = z;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Vector) {
				Vector v = (Vector)obj;
				return v.X == X && v.Y == Y && v.Z == Z;
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return X + Y + Z;
		}
	}
	@Override
	public void tick() {
		tagged.clear();
	}
	
	private class Data {
		public int[] pos;
		public Block oldblock;
	}
}