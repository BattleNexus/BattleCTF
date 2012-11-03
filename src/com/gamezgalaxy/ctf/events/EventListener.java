/*******************************************************************************
 * Copyright (c) 2012 GamezGalaxy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.gamezgalaxy.ctf.events;

import java.util.ArrayList;
import java.util.HashMap;

import net.mcforge.API.EventHandler;
import net.mcforge.API.Listener;
import net.mcforge.API.level.PlayerJoinedLevel;
import net.mcforge.API.player.PlayerBlockChangeEvent;
import net.mcforge.API.player.PlayerCommandEvent;
import net.mcforge.API.player.PlayerDisconnectEvent;
import net.mcforge.API.player.PlayerMoveEvent;
import net.mcforge.API.server.ServerStartedEvent;
import net.mcforge.chat.ChatColor;
import net.mcforge.iomodel.Player;
import net.mcforge.world.Block;
import net.mcforge.world.PlaceMode;
import com.gamezgalaxy.ctf.blocks.TNT_Explode;
import com.gamezgalaxy.ctf.commands.shop.ShopItem;
import com.gamezgalaxy.ctf.gamemode.ctf.CTF;
import com.gamezgalaxy.ctf.gamemode.ctf.utl.Team;
import com.gamezgalaxy.ctf.main.main;

public class EventListener implements Listener {
	private HashMap<Player, Integer[]> flagfloat = new HashMap<Player, Integer[]>();
	private ArrayList<Vector> tempflags = new ArrayList<Vector>();
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF))
			return;
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		if (event.getPlayer().getLevel() != ctf.getMap().level)
			return;
		if (flagfloat.containsKey(event.getPlayer())) {
			Integer[] temp = flagfloat.get(event.getPlayer());
			int x = temp[0].intValue();
			int y = temp[1].intValue();
			int z = temp[2].intValue();
			if (x != event.getPlayer().getBlockX() || y != event.getPlayer().getBlockY() || z != event.getPlayer().getBlockZ()) {
				Player.GlobalBlockChange((short)x, (short)(y + 3), (short)z, Block.getBlock("Air"), event.getPlayer().getLevel(), event.getPlayer().getServer());
				x = event.getPlayer().getBlockX();
				y = event.getPlayer().getBlockY();
				z = event.getPlayer().getBlockZ();
				Player.GlobalBlockChange((short)x, (short)(y + 3), (short)z, ctf.holders.get(event.getPlayer()).flagblock, event.getPlayer().getLevel(), event.getPlayer().getServer());
				Integer[] temp1 = new Integer[] {
					x,
					y,
					z
				};
				flagfloat.remove(event.getPlayer());
				flagfloat.put(event.getPlayer(), temp1);
			}
		}
	}
	public void clear() {
		flagfloat.clear();
	}
	public void drop(final Player p, final Block block) {
		Integer[] temp = flagfloat.get(p);
		flagfloat.remove(p);
		final int x = temp[0].intValue();
		final int y = temp[1].intValue();
		final int z = temp[2].intValue();
		Player.GlobalBlockChange((short)x, (short)(y + 3), (short)z, Block.getBlock("Air"), p.getLevel(), p.getServer());
		Thread t = new Thread() {
			
			@Override
			public void run() {
				int temp = y + 3;
				System.out.println(p.getLevel().getTile(x, temp - 1, z).name);
				while (p.getLevel().getTile(x, temp - 1, z).name.equals("Air") || p.getLevel().getTile(x, temp - 1, z).getVisableBlock() == 10) {
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
		if (event.getPlaceType() == PlaceMode.BREAK) {
			for (Team t : ctf.teams) {
				if (t.flagx == X && t.flagy == Y && t.flagz == Z && t != team && !holding && event.getPlayer().getLevel().getTile(X, Y, Z) == t.flagblock) {
					main.GlobalMessage(event.getPlayer().username + " took the " + t.name +"'s FLAG!");
					ctf.holders.put(event.getPlayer(), t);
					flagfloat.put(event.getPlayer(), new Integer[] {
						0,
						0,
						0
					});
				}
				else if (tempflags.contains(v) && t != team && !holding && event.getPlayer().getLevel().getTile(X, Y, Z) == t.flagblock) {
					tempflags.remove(v);
					main.GlobalMessage(event.getPlayer().username + " picked up " + t.name +"'s FLAG!");
					ctf.holders.put(event.getPlayer(), t);
					flagfloat.put(event.getPlayer(), new Integer[] {
						0,
						0,
						0
					});
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
					Integer[] temp = flagfloat.get(event.getPlayer());
					flagfloat.remove(event.getPlayer());
					int x = temp[0].intValue();
					int y = temp[1].intValue();
					int z = temp[2].intValue();
					Player.GlobalBlockChange((short)x, (short)(y + 3), (short)z, Block.getBlock("Air"), event.getPlayer().getLevel(), event.getPlayer().getServer());
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
			if (event.getBlock().getVisableBlock() == 46 && !ctf.tntholders.containsKey(event.getPlayer())) {
				TNT_Explode t = new TNT_Explode(event.getPlayer(), event.getServer());
				t.setPos(event.getX(), event.getY(), event.getZ());
				event.setBlock(t);
				ctf.tntholders.put(event.getPlayer(), t);
				event.getPlayer().sendMessage(ChatColor.Aqua + " Place " + ChatColor.Red + "\"Brick\" " + ChatColor.Aqua + "to detonate the TNT");
			}
			else if (event.getBlock().getVisableBlock() == 46 && ctf.tntholders.containsKey(event.getPlayer()))
				event.setCancel(true);
			else if (event.getBlock().getVisableBlock() == 45 && ctf.tntholders.containsKey(event.getPlayer())) {
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
					if (getBiggest() == t) {
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
		int X;
		int Y;
		int Z;
		
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
}