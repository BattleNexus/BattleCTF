/*******************************************************************************
 * Copyright (c) 2012 GamezGalaxy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.battlenexus.classic.ctf.map;

import net.mcforge.iomodel.Player;

public class SafeZone {
	private int smallx;
	private int bigx;
	private int smally;
	private int bigy;
	private int smallz;
	private int bigz;
	
	public void setSmallX(int value) {
		this.smallx = value;
	}
	public void setBigX(int value) {
		this.bigx = value;
	}
	public void setSmallY(int value) {
		this.smally = value;
	}
	public void setBigY(int value) {
		this.bigy = value;
	}
	public void setSmallZ(int value) {
		this.smallz = value;
	}
	public void setBigZ(int value) {
		this.bigz = value;
	}
	public int getSmallX() {
		return smallx;
	}
	public int getBigX() {
		return bigx;
	}
	public int getSmallY() {
		return smally;
	}
	public int getBigY() {
		return bigy;
	}
	public int getSmallZ() {
		return smallz;
	}
	public int getBigZ() {
		return bigz;
	}
	public boolean isSafe(int x, int y, int z) {
		return x > smallx - 2 && x < bigx + 2 && y > smally - 2 && y < bigy + 2 && z > smallz - 2 && z < bigz + 2;
	}
	public boolean isSafe(Player p) {
		if (bigx == smallx)
			bigx = p.getLevel().width;
		if (bigy == smally)
			bigy = p.getLevel().height;
		if (bigz == smallz)
			bigz = p.getLevel().depth;
		return isSafe(p.getBlockX(), p.getBlockY(), p.getBlockZ());
	}

}
