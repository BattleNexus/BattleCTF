package com.gamezgalaxy.ctf.blocks;

import java.util.HashMap;
import java.util.Random;

import com.gamezgalaxy.GGS.iomodel.Player;
import com.gamezgalaxy.GGS.server.Server;
import com.gamezgalaxy.GGS.world.Block;
import com.gamezgalaxy.GGS.world.PhysicsBlock;
import com.gamezgalaxy.ctf.gamemode.Gamemode;
import com.gamezgalaxy.ctf.gamemode.ctf.CTF;
import com.gamezgalaxy.ctf.gamemode.ctf.utl.Team;
import com.gamezgalaxy.ctf.main.main;

public class TNT_Explode extends PhysicsBlock {

	Player owner;
	Server server;
	public int wait = 400;
	int size = 0;
	public TNT_Explode(byte ID, String name) {
		this(ID, name, null, null);
	}
	public TNT_Explode(byte ID, String name, Server server) {
		this(ID, name, server, null);
	}
	public TNT_Explode(byte ID, String name, Server server, Player owner) {
		super(ID, name);
		this.owner = owner;
		this.server = server;
	}

	public TNT_Explode(Player owner) {
		this((byte)46, "TNTEXE");
		this.owner = owner;
	}
	@Override
	public PhysicsBlock clone(Server s) {
		TNT_Explode te = new TNT_Explode(ID, name, s, owner);
		te.wait = new Random().nextInt(40 - 15) + 15;
		te.size = new Random().nextInt(3);
		return te;
	}

	@Override
	public void Tick() {
		if (wait <= 0) {
			//TODO Blow up
			final Random rand = new Random();
			HashMap<Vector, Player> cache = new HashMap<Vector, Player>();
			for (int i = 0; i < server.players.size(); i++)
				cache.put(new Vector(server.players.get(i).getBlockX(), server.players.get(i).getBlockY(), server.players.get(i).getBlockZ()), server.players.get(i));
			final int X = getX();
			final int Y = getY();
			final int Z = getZ();
			for (int xx = (X - (size + 1)); xx <= (X + (size + 1)); ++xx) {
				for (int yy = (Y - (size + 1)); yy <= (Y + (size + 1)); ++yy) {
					for (int zz = (Z - (size + 1)); zz <= (Z + (size + 1)); ++zz) {
						Vector loc = new Vector(xx, yy, zz);
						if (cache.containsKey(loc)) {
							Player p = cache.get(loc);
							if (!(main.INSTANCE.getCurrentGame() instanceof CTF))
								p.setPos((short)(getLevel().spawnx * 32), (short)(getLevel().spawny * 32), (short)(getLevel().spawnz * 32));
							else {
								CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
								if (ctf.isOnNoTeam(p))
									p.setPos((short)(getLevel().spawnx * 32), (short)(getLevel().spawny * 32), (short)(getLevel().spawnz * 32));
								else {
									Team t = ctf.getTeam(p);
									int x = ((t.safe.getBigX() - t.safe.getSmallX()) / 2) + t.safe.getSmallX();
									int y = ((t.safe.getBigY() - t.safe.getSmallY()) / 2) + t.safe.getSmallY();
									int z = ((t.safe.getBigZ() - t.safe.getSmallZ()) / 2) + t.safe.getSmallZ();
									p.setPos((short)(x * 32), (short)(y * 32), (short)(z * 32));
								}
							}
							cache.remove(loc);
						}
						if (getLevel().getTile(xx, yy, zz).name.equals("TNTEXE")) {
							TNT_Explode tnt = (TNT_Explode)getLevel().getTile(xx, yy, zz);
							tnt.wait = 0;
						}
						else if (rand.nextInt(11) <= 8)
							Player.GlobalBlockChange((short)xx, (short)yy, (short)zz, Block.getBlock("Air"), getLevel(), server);
						else if (rand.nextInt(11) <= 4)
							Player.GlobalBlockChange((short)xx, (short)yy, (short)zz, new Explosion((byte)10, "exe"), getLevel(), server);
						
					}
				}
			}
		}
		else
			wait--;
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
	}
}
