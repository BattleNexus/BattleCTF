package com.gamezgalaxy.ctf.blocks;

import java.util.Random;

import net.mcforge.iomodel.Player;
import net.mcforge.server.Server;
import net.mcforge.world.PhysicsBlock;

public class Mine extends TNT_Explode {

	private static final long serialVersionUID = 98033632394972889L;

	public Mine(byte ID, String name, Server server, Player owner) {
		super(ID, name, server, owner);
	}
	
	
	public Mine(Player owner, Server server) {
		super((byte)34, "Mine", server, owner);
	}
	
	@Override
	public PhysicsBlock clone(Server s) {
		Mine m = new Mine((byte)34, "Mine", s, owner);
		m.wait = new Random().nextInt(40 - 5) + 5;
		m.size = 1;
		return m;
	}
	
	@Override
	public void tick() {
		int xmax = getX() + 2;
		int ymax = getY() + 3;
		int zmax = getZ() + 2;
		int zmin = getZ() - 2;
		int xmin = getX() - 2;
		int ymin = getY() + 3;
		for (int i = 0; i < server.getPlayers().size(); i++) {
			if (server.getPlayers().get(i) == owner)
				continue;
			final Player p = server.getPlayers().get(i);
			if (p.getBlockX() >= xmin && p.getBlockX() <= xmax && p.getBlockY() >= ymin && p.getBlockY() <= ymax && p.getBlockZ() >= zmin && p.getBlockZ() <= zmax)
				super.explode();
		}
	}

}
