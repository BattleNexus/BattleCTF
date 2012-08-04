package com.gamezgalaxy.ctf.gamemode.ctf;

import java.util.ArrayList;

import com.gamezgalaxy.GGS.iomodel.Player;
import com.gamezgalaxy.ctf.blocks.BlueFlag;
import com.gamezgalaxy.ctf.blocks.RedFlag;
import com.gamezgalaxy.ctf.gamemode.Gamemode;
import com.gamezgalaxy.ctf.main.main;

public class CTF extends Gamemode {

	public ArrayList<Player> blueteam = new ArrayList<Player>();
	public ArrayList<Player> redteam = new ArrayList<Player>();
	public int bpoints;
	public int rpoints;
	public int goal;
	public Player blueholder;
	public Player redholder;
	@Override
	public void roundStart() {
		//Split the players up to teams
		boolean blue = false;
		for (Player p : main.INSTANCE.getServer().players){
			if (p.getLevel() == getMap().level) {
				if (blue)
					blueteam.add(p);
				else
					redteam.add(p);
				blue = !blue;
			}
		}
		//Place the flags
		getMap().level.setTile(new BlueFlag(), getMap().bluex, getMap().bluey, getMap().bluez, main.INSTANCE.getServer());
		getMap().level.setTile(new RedFlag(), getMap().redx, getMap().redy, getMap().redz, main.INSTANCE.getServer());
		//Spawn all the players
		int x = ((getMap().bzone.getBigX() - getMap().bzone.getSmallX()) / 2) + getMap().bzone.getSmallX();
		int y = ((getMap().bzone.getBigY() - getMap().bzone.getSmallY()) / 2) + getMap().bzone.getSmallY();
		int z = ((getMap().bzone.getBigZ() - getMap().bzone.getSmallZ()) / 2) + getMap().bzone.getSmallZ();
		for (Player p : blueteam) {
			p.setPos((short)(x * 32), (short)(y * 32), (short)(z * 32)); //Multiply by 32 to convert to player pos
		}
		x = ((getMap().rzone.getBigX() - getMap().rzone.getSmallX()) / 2) + getMap().rzone.getSmallX();
		y = ((getMap().rzone.getBigY() - getMap().rzone.getSmallY()) / 2) + getMap().rzone.getSmallY();
		z = ((getMap().rzone.getBigZ() - getMap().rzone.getSmallZ()) / 2) + getMap().rzone.getSmallZ();
		for (Player p : redteam) {
			p.setPos((short)(x * 32), (short)(y * 32), (short)(z * 32)); //Multiply by 32 to convert to player pos
		}
		goal = main.random.nextInt(5);
		main.GlobalMessage("&2[GBot] In this round, you team must score &4" + goal + " &2points!");
		main.GlobalMessage("&2[GBot] The round has started! Good luck!");
		running = true;
	}

	@Override
	public void tick() {
		if (bpoints >= goal || rpoints >= goal)
			roundEnd();
	}

	@Override
	public void roundEnd() {
		running = false;
	}
	
	public boolean isOnBlueTeam(Player p) {
		return blueteam.contains(p);
	}
	public boolean isOnRedTeam(Player p) {
		return redteam.contains(p);
	}
	public boolean isOnNoTeam(Player p) {
		return (!isOnBlueTeam(p) && !isOnRedTeam(p));
	}

}
