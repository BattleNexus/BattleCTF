/*******************************************************************************
 * Copyright (c) 2012 GamezGalaxy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.gamezgalaxy.ctf.gamemode;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import net.mcforge.API.plugin.Plugin;
import net.mcforge.iomodel.Player;
import net.mcforge.world.Level;
import com.gamezgalaxy.ctf.main.main;
import com.gamezgalaxy.ctf.map.Map;
import com.gamezgalaxy.ctf.map.utl.ConfigGraber;

public abstract class Gamemode {
	Map _map;
	
	public Plugin parent;
	
	private String name;
	
	protected boolean running = false;
	
	public boolean ctfmap = true;
	
	public void setup(Map map) throws Exception {
		this._map = map;
		//Unload level if it exists
		System.out.println("Use : " + (ctfmap ? "ctf" : "ctf2"));
		//Swap levels
		final String BACKUP_PATH = "backups/" + (ctfmap ? "ctf" : "ctf2") + "/" + ConfigGraber.getMapBackupNumber(map.mapname, "config/") + "/" + (ctfmap ? "ctf" : "ctf2") + ".ggs";
		final String FINAL_PATH = "levels/" + (ctfmap ? "ctf" : "ctf2") + ".ggs";
		final String CONVERT_BACKUP_PATH = "backups/" + (ctfmap ? "ctf" : "ctf2") + "/" + ConfigGraber.getMapBackupNumber(map.mapname, "config/") + "/" + (ctfmap ? "ctf" : "ctf2") + ".lvl";
		final String CONVERT_DAT_BACKUP_PATH = "backups/" + (ctfmap ? "ctf" : "ctf2") + "/" + ConfigGraber.getMapBackupNumber(map.mapname, "config/") + "/" + (ctfmap ? "ctf" : "ctf2") + ".dat";
		if (!new File(BACKUP_PATH).exists() && new File(CONVERT_BACKUP_PATH).exists()) {
			//TODO Convert .lvl to .ggs
		}
		else if (!new File(BACKUP_PATH).exists() && new File(CONVERT_DAT_BACKUP_PATH).exists()) {
			getMain().getServer().Log("Converting .dat..");
			Level l = Level.convertDat(CONVERT_DAT_BACKUP_PATH);
			l.save();
			ConfigGraber.copyfile("levels/" + l.name + ".ggs", BACKUP_PATH);
			getMain().getServer().Log("Done!");
		}
		else if (!new File(BACKUP_PATH).exists() && new File(CONVERT_BACKUP_PATH).exists()) {
			getMain().getServer().Log("Converting .lvl...");
			Level l = Level.convertLVL(CONVERT_BACKUP_PATH, getMain().getServer());
			l.save();
			ConfigGraber.copyfile("levels/" + l.name + ".ggs", BACKUP_PATH);
			getMain().getServer().Log("Done!");
		}
		ConfigGraber.copyfile(BACKUP_PATH, FINAL_PATH);
		getMain().getServer().getLevelHandler().loadLevel(FINAL_PATH);
		//Set main level
		if (getMain().getServer().getLevelHandler().findLevel((ctfmap ? "ctf" : "ctf2")) == null)
			throw new Exception("Error Restoring from backup.");
		getMain().getServer().MainLevel = ctfmap ? "ctf" : "ctf2";
		_map.level = getMain().getServer().getLevelHandler().findLevel((ctfmap ? "ctf" : "ctf2"));
		getMain().getServer().getLevelHandler().findLevel((ctfmap ? "ctf" : "ctf2")).setAutoSave(false);
		//Unload the current game level if one is loaded..
		//We swap the in-line if statement to get which one is loaded, not which one will be
		if (getMain().getServer().getLevelHandler().findLevel((ctfmap ? "ctf2" : "ctf")) != null)
			getMain().getServer().getLevelHandler().unloadLevel(getMain().getServer().getLevelHandler().findLevel((ctfmap ? "ctf2" : "ctf")), false);
	}
	public abstract void roundStart();
	
	public abstract void tick();
	
	public abstract void roundEnd();
	
	public Map getMap() {
		return _map;
	}
	
	public Plugin getParent() {
		return parent;
	}
	
	public main getMain() {
		if (parent instanceof main)
			return (main)parent;
		return null;
	}
	
	public synchronized void dispose() {
		running = false;
		_map.games.clear();
		_map.teams.clear();
		_map.stalemate.clear();
		super.notify();
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public synchronized void waitForEnd() throws InterruptedException {
		while (true) {
			if (!isRunning())
				return;
			super.wait(0L);
		}
	}
	
	public String getGameName() {
		return (name == null || name.equals("")) ? getClass().getSimpleName() : name;
	}
	
	public void rewardPlayer(Player p, int amount) {
		setValue(p, "points", amount, true);
		saveValue(p, "points");
	}
	public int getPoints(Player p) {
		return getValue(p, "points");
	}
	
	public int getKillStreak(Player p) {
		return getValue(p, "killstreak");
	}
	
	public void setKillStreak(Player p, int amount) {
		setValue(p, "killstreak", amount, false);
	}
	
	public void addCapture(Player p) {
		setValue(p, "capture", 1, true);
		saveValue(p, "capture");
		setValue(p, "caps", 1, true);
	}
	
	public void addDrop(Player p) {
		setValue(p, "drop", 1, true);
		saveValue(p, "drop");
		setValue(p, "balls", 1, true);
	}
	
	public int getCapture(Player p) {
		return getValue(p, "capture");
	}
	
	public int getDrop(Player p) {
		return getValue(p, "drop");
	}
	
	public void addEXP(Player p, int amount) {
		setValue(p, "exp", amount, true);
		saveValue(p, "exp");
	}
	public void resetEXP(Player p) {
		setValue(p, "exp", 0, false);
	}
	
	public void levelUp(Player p) {
		setValue(p, "level", 1, true);
		saveValue(p, "level");
	}
	
	public int getEXP(Player p) {
		return getValue(p, "exp");
	}
	
	public int getLevel(Player p) {
		return getValue(p, "level");
	}
	
	public int getRequiredEXP(Player p) {
		int lvl = getLevel(p);
		int required = 0;
		for (int i = lvl; i > 0; --i)
			required += (lvl * 100) / 2;
		required += lvl * 100;
		return required;
	}
	
	public int capturesThisRound(Player p) {
		return getValue(p, "caps");
	}
	
	public void resetCapturesthisRound(Player p) {
		setValue(p, "caps", 0, false);
	}
	
	public int dropsThisRound(Player p) {
		return getValue(p, "balls");
	}
	
	public void resetDropsthisRound(Player p) {
		setValue(p, "balls", 0, false);
	}
	
	public void setValue(Player p, String setting, int value, boolean add) {
		if (add) {
			int points = getValue(p, setting);
			points += value;
			p.setAttribute(setting, points);
		}
		else
			p.setAttribute(setting, value);
	}
	public void saveValue(Player p, String setting) {
		try {
			p.saveAttribute(setting);
		} catch (SQLException e) { 
			main.INSTANCE.getServer().Log("Could not save " + p.username + " " + setting + "..."); 
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public int getValue(Player p, String setting) {
		Integer points = 0;
		if (p.getAttribute(setting) != null) {
			points = p.getAttribute(setting);
		}
		return points.intValue();
	}

}
