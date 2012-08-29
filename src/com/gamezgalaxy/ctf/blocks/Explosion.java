package com.gamezgalaxy.ctf.blocks;

import java.util.Random;

import com.gamezgalaxy.GGS.iomodel.Player;
import com.gamezgalaxy.GGS.server.Server;
import com.gamezgalaxy.GGS.world.Block;
import com.gamezgalaxy.GGS.world.PhysicsBlock;

public class Explosion extends PhysicsBlock {

	private static final long serialVersionUID = 1L;
	final Random rand = new Random();
	Server server;
	int wait = 10;
	public Explosion(byte ID, String name) {
		super(ID, name);
	}
	public Explosion(byte ID, String name, Server server) {
		super(ID, name, server);
		this.server = server;
	}

	@Override
	public PhysicsBlock clone(Server s) {
		Explosion ex = new Explosion(ID, name, s);
		return ex;
	}

	@Override
	public void Tick() {
		if (wait <= 0) {
			wait = 10;
			if (rand.nextInt(11) <= 8)
				Player.GlobalBlockChange((short)getX(), (short)getY(), (short)getZ(), Block.getBlock("Air"), getLevel(), server);
			else {
				if (getLevel().getTile(getX(), getY() - 1, getZ()).getVisableBlock() == 0)
					move(getX(), getY() - 1, getZ());
			}
		}
		else
			wait--;
	}

	@Override
	public byte getVisableBlock() {
		return 10;
	}

}
