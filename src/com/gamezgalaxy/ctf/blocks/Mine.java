package com.gamezgalaxy.ctf.blocks;

import net.mcforge.iomodel.Player;
import net.mcforge.server.Server;
import net.mcforge.world.PhysicsBlock;

public class Mine extends TNT_Explode {

	private static final long serialVersionUID = 98033632394972889L;

	public Mine(byte ID, String name, Server server, Player owner) {
		super(ID, name, server, owner);
	}
	
	@Override
	public PhysicsBlock clone(Server s) {
		Mine m = new Mine((byte)34, "Mine", s, owner);
		return m;
	}
	
	@Override
	public void tick() {
		int xmax = getX() + 1;
		int ymax = getY() + 3;
		int zmax = getZ() + 1;
		int zmin = getZ() - 1;
		int xmin = getX() - 1;
		int ymin = getY() + 2;
		for (int i = 0; i < server.players.size(); i++) {
			if (server.players.get(i) == owner)
				continue;
			final Player p = server.players.get(i);
			if (p.getBlockX() >= xmin && p.getBlockX() <= xmax && p.getBlockY() >= ymin && p.getBlockY() <= ymax && p.getBlockZ() >= zmin && p.getBlockZ() <= zmax)
				super.explode();
		}
	}

}
