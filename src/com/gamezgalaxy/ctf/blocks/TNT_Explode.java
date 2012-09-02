package com.gamezgalaxy.ctf.blocks;

import java.util.HashMap;
import java.util.Random;

import com.gamezgalaxy.GGS.chat.ChatColor;
import com.gamezgalaxy.GGS.iomodel.Player;
import com.gamezgalaxy.GGS.server.Server;
import com.gamezgalaxy.GGS.world.Block;
import com.gamezgalaxy.GGS.world.PhysicsBlock;
import com.gamezgalaxy.ctf.events.Killable;
import com.gamezgalaxy.ctf.events.PlayerDeathByTNTEvent;
import com.gamezgalaxy.ctf.gamemode.ctf.CTF;
import com.gamezgalaxy.ctf.gamemode.ctf.utl.KillStreak;
import com.gamezgalaxy.ctf.gamemode.ctf.utl.Team;
import com.gamezgalaxy.ctf.main.main;

public class TNT_Explode extends PhysicsBlock implements Killable<TNT_Explode> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9132287119137033841L;
	Player owner;
	Server server;
	public int wait = 400;
	int size = 0;
	boolean exploding = false;
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
	public TNT_Explode(Player owner, Server server) {
		this((byte)46, "TNTEXE", server, owner);
	}
	@Override
	public PhysicsBlock clone(Server s) {
		TNT_Explode te = new TNT_Explode(ID, name, s, owner);
		te.wait = new Random().nextInt(40 - 5) + 5;
		te.size = 2;
		return te;
	}

	@Override
	public void Tick() {
		if (wait <= 0) {
			explode();
		}
		else
			wait--;
	}
	public void explode() {
		if (exploding)
			return;
		exploding = true;
		final Random rand = new Random();
		HashMap<Vector, Player> cache = new HashMap<Vector, Player>();
		for (int i = 0; i < server.players.size(); i++)
			cache.put(new Vector(server.players.get(i).getBlockX(), server.players.get(i).getBlockY(), server.players.get(i).getBlockZ()), server.players.get(i));
		final int X = getX();
		final int Y = getY();
		final int Z = getZ();
		final CTF ctf = (main.INSTANCE.getCurrentGame() instanceof CTF ? (CTF)main.INSTANCE.getCurrentGame() : null);
		int kills = 0;
		for (int xx = (X - (size + 1)); xx <= (X + (size + 1)); ++xx) {
			for (int yy = (Y - (size + 1)); yy <= (Y + (size + 1)); ++yy) {
				for (int zz = (Z - (size + 1)); zz <= (Z + (size + 1)); ++zz) {
					Vector loc = new Vector(xx, yy, zz);
					if (cache.containsKey(loc)) {
						Player p = cache.get(loc);
						if (p == owner) {
							cache.remove(loc);
							continue;
						}
						if (ctf == null)
							p.setPos((short)(getLevel().spawnx * 32), (short)(getLevel().spawny * 32), (short)(getLevel().spawnz * 32));
						else {
							if (ctf.isOnNoTeam(p))
								p.setPos((short)(getLevel().spawnx * 32), (short)(getLevel().spawny * 32), (short)(getLevel().spawnz * 32));
							else {
								Team t = ctf.getTeam(p);
								PlayerDeathByTNTEvent event = new PlayerDeathByTNTEvent(p, owner, this);
								owner.getServer().getEventSystem().callEvent(event);
								if (!event.isCancelled()) {
									t.spawnPlayer(p);
									if (ctf.getKillStreak(p) != 0) {
										main.GlobalMessage(ChatColor.Dark_Red + this.owner.username + " ended " + p.username + " killstreak of " + ctf.getKillStreak(p));
										ctf.setKillStreak(p, 0);
										if (ctf.dominate.containsKey(p)) {
											if (ctf.dominate.get(p).containsKey(owner)) {
												ctf.dominate.get(p).remove(owner);
												main.GlobalMessage(owner.username + " got " + ChatColor.Dark_Red + " REVENGE " + ChatColor.White + " on " + p.username);
											}
										}
									}
									main.GlobalMessage(this.owner.username + " &4EXPLODED&f " + p.username);
									kills++;
									if (!ctf.dominate.containsKey(owner))
										ctf.dominate.put(owner, new HashMap<Player, Integer>());
									if (ctf.dominate.get(owner).containsKey(p)) {
										int temp = ctf.dominate.get(owner).get(p);
										temp++;
										ctf.dominate.get(owner).remove(p);
										ctf.dominate.get(owner).put(p, temp);
										if (temp % 4 == 0)
											main.GlobalMessage(owner.username + " is " + ChatColor.Dark_Red + " DOMINATING " + ChatColor.White + p.username);
									}
									else
										ctf.dominate.get(owner).put(p, 1);
								}
							}
						}
						cache.remove(loc);
					}
					
					if (getLevel().getTile(xx, yy, zz).name.equals("TNTEXE") && xx != getX() && yy != getY() && zz != getZ()) {
						TNT_Explode tnt = (TNT_Explode)getLevel().getTile(xx, yy, zz);
						tnt.wait = 0;
					}
					else if (isInSafe(xx, yy, zz, (main.INSTANCE.getCurrentGame() instanceof CTF ? (CTF)main.INSTANCE.getCurrentGame() : null)) || isTeamFlag(getLevel().getTile(xx, yy, zz), (main.INSTANCE.getCurrentGame() instanceof CTF ? (CTF)main.INSTANCE.getCurrentGame() : null)))
						continue;
					else if (rand.nextInt(11) <= 8)
						Player.GlobalBlockChange((short)xx, (short)yy, (short)zz, Block.getBlock("Air"), getLevel(), server);
					else if (rand.nextInt(11) <= 4)
						Player.GlobalBlockChange((short)xx, (short)yy, (short)zz, new Explosion((byte)10, "exe"), getLevel(), server);
					
				}
			}
		}
		cache.clear();
		if (ctf != null) {
			ctf.setKillStreak(owner, kills);
			String message = "";
			for (KillStreak k : KillStreak.values()) {
				if (kills == k.getAmount()) {
					message = k.getMessage();
					break;
				}
			}
			if (!message.equals(""))
				main.GlobalMessage(ChatColor.Dark_Blue + this.owner.username + ": " + message);
			ctf.rewardPlayer(owner, 2 * kills + ctf.getKillStreak(owner));
			if (ctf.tntholders.containsKey(owner))
				ctf.tntholders.remove(owner);
			exploding = false;
		}
	}
	public boolean isInSafe(int x, int y, int z, CTF ctf) {
		if (ctf == null || (x == getX() && y == getY() && z == getZ()))
			return false;
		for (Team t : ctf.teams) {
			if (t.safe.isSafe(x, y, z))
				return true;
		}
		return false;
	}
	public boolean isTeamFlag(Block flag, CTF ctf) {
		if (ctf == null)
			return false;
		for (Team t : ctf.teams) {
			if (t.flagblock == flag)
				return true;
		}
		return false;
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

	@Override
	public TNT_Explode getObject() {
		return this;
	}
}
