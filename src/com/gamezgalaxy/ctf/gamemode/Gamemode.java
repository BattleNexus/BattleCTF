/*******************************************************************************
 * Copyright (c) 2012 GamezGalaxy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.gamezgalaxy.ctf.gamemode;

import java.io.File;

import com.gamezgalaxy.GGS.API.plugin.Game;
import com.gamezgalaxy.GGS.world.Level;
import com.gamezgalaxy.ctf.main.main;
import com.gamezgalaxy.ctf.map.Map;
import com.gamezgalaxy.ctf.map.utl.ConfigGraber;

public abstract class Gamemode {
	Map _map;
	
	public Game parent;
	
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
			Level l = Level.convertDAT(CONVERT_DAT_BACKUP_PATH);
			l.Save();
			ConfigGraber.copyfile("levels/" + l.name + ".ggs", BACKUP_PATH);
			getMain().getServer().Log("Done!");
		}
		else if (!new File(BACKUP_PATH).exists() && new File(CONVERT_BACKUP_PATH).exists()) {
			getMain().getServer().Log("Converting .lvl...");
			Level l = Level.convertLVL(CONVERT_BACKUP_PATH);
			l.Save();
			ConfigGraber.copyfile("levels/" + l.name + ".ggs", BACKUP_PATH);
			getMain().getServer().Log("Done!");
		}
		ConfigGraber.copyfile(BACKUP_PATH, FINAL_PATH);
		getMain().getServer().getLevelHandler().loadLevel(FINAL_PATH);
		//Set main level
		if (getMain().getServer().getLevelHandler().findLevel((ctfmap ? "ctf" : "ctf2")) == null)
			throw new Exception("Error Restoring from backup.");
		getMain().getServer().MainLevel = getMain().getServer().getLevelHandler().findLevel((ctfmap ? "ctf" : "ctf2"));
		_map.level = getMain().getServer().MainLevel;
		getMain().getServer().MainLevel.setAutoSave(false);
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
	
	public Game getParent() {
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

}
