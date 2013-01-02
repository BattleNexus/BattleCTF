package com.gamezgalaxy.ctf.commands.shop;

import java.util.ArrayList;

import net.mcforge.API.CommandExecutor;
import net.mcforge.API.ManualLoad;
import net.mcforge.chat.ChatColor;
import net.mcforge.iomodel.Player;
import net.mcforge.world.Block;

@ManualLoad
public class JetPack extends ShopItem {

	public JetPack(Shop parent) {
		super(parent);
	}

	@Override
	public String getShopName() {
		return "jetpack";
	}

	@Override
	public boolean run(Player p) {
		Thread t = new Fly(p);
		t.start();
		return true;
	}

	@Override
	public int getDefaultPrice() {
		return 300;
	}

	@Override
	public int getDefaultLevel() {
		return 5;
	}

	@Override
	public String getLevelUpMessage(Player p) {
		return "";
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
	private class Fly extends Thread {
		Player player;
		public Fly(Player player) { this.player = player; }
		@Override
		public void run() {
			int seconds = 10;
			int i = 0;
			int minx = 0;
			int maxx = 0;
			int miny = 0;
			int maxy = 0;
			int minz = 0;
			int maxz = 0;
			ArrayList<Vector> temp = new ArrayList<Vector>();
			ArrayList<Vector> buffer = new ArrayList<Vector>();
			ArrayList<Vector> toremove = new ArrayList<Vector>();
			player.sendMessage("You are now " + ChatColor.Bright_Green + "FLYING!");
			player.sendMessage("You will be able to fly for 10 seconds..");
			while (seconds > 0) {
				if (i >= 50) {
					i = 0;
					seconds--;
				}
				miny = player.getBlockY() - 3;
				maxy = player.getBlockY() - 1;
				minx = player.getBlockX() - 3;
				maxx = player.getBlockX() + 3;
				minz = player.getBlockZ() - 3;
				maxz = player.getBlockZ() + 3;
				for (int xx = minx; xx < maxx; xx++) {
					for (int yy = miny; yy < maxy; yy++) {
						for (int zz = minz; zz < maxz; zz++) {
							temp.add(new Vector(xx, yy, zz));
						}
					}
				}
				for (Vector v : buffer) {
					if (!temp.contains(v)) {
						player.sendBlockChange((short)v.X, (short)v.Y, (short)v.Z, player.getLevel().getTile(v.X, v.Y, v.Z));
						toremove.add(v);
					}
				}
				for (Vector v : toremove) {
					buffer.remove(v);
				}
				for (Vector v : temp) {
					if (!buffer.contains(v)) {
						player.sendBlockChange((short)v.X, (short)v.Y, (short)v.Z, Block.getBlock("Glass"));
						buffer.add(v);
					}
				}
				temp.clear();
				toremove.clear();
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) { }
				i++;
			}
			for (Vector v : buffer) {
				player.sendBlockChange((short)v.X, (short)v.Y, (short)v.Z, player.getLevel().getTile(v.X, v.Y, v.Z));
			}
			buffer.clear();
			toremove.clear();
			temp.clear();
			player.sendMessage(ChatColor.Dark_Red + "You have stopped flying..");
		}
	}
	@Override
	public void help(CommandExecutor executor) {
		executor.sendMessage("/jetpack - Fly for a limited time.");
	}
}
