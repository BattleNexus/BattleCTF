/*******************************************************************************
 * Copyright (c) 2012 GamezGalaxy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.gamezgalaxy.ctf.gamemode;

import java.io.File;

import com.gamezgalaxy.ctf.main.main;
import com.gamezgalaxy.ctf.map.Map;
import com.gamezgalaxy.ctf.map.utl.ConfigGraber;

public abstract class Gamemode {
	Map _map;
	
	private String name;
	
	protected boolean running = false;
	
	public boolean ctfmap = true;
	
	public void setup(Map map) throws Exception {
		this._map = map;
		//Swap levels
		final String BACKUP_PATH = "backups/" + (ctfmap ? "ctf" : "ctf2") + "/" + ConfigGraber.getMapBackupNumber(map.mapname, "config/") + "/" + (ctfmap ? "ctf" : "ctf2") + ".ggs";
		final String FINAL_PATH = "levels/" + (ctfmap ? "ctf" : "ctf2") + ".ggs";
		final String CONVERT_BACKUP_PATH = "backups/" + (ctfmap ? "ctf" : "ctf2") + "/" + ConfigGraber.getMapBackupNumber(map.mapname, "config/") + "/" + (ctfmap ? "ctf" : "ctf2") + ".lvl";
		final String CONVERT_DAT_BACKUP_PATH = "backups/" + (ctfmap ? "ctf" : "ctf2") + "/" + ConfigGraber.getMapBackupNumber(map.mapname, "config/") + "/" + (ctfmap ? "ctf" : "ctf2") + ".lvl";
		if (!new File(BACKUP_PATH).exists() && new File(CONVERT_BACKUP_PATH).exists()) {
			//TODO Convert .lvl to .ggs
		}
		else if (!new File(BACKUP_PATH).exists() && new File(CONVERT_DAT_BACKUP_PATH).exists()) {
			//TODO Convert .dat to .ggs
		}
		ConfigGraber.copyfile(BACKUP_PATH, FINAL_PATH);
		main.INSTANCE.getServer().getLevelHandler().loadLevel(FINAL_PATH);
		//Set main level
		if (main.INSTANCE.getServer().getLevelHandler().findLevel((ctfmap ? "ctf" : "ctf2")) == null)
			throw new Exception("Error Restoring from backup.");
		main.INSTANCE.getServer().MainLevel = main.INSTANCE.getServer().getLevelHandler().findLevel((ctfmap ? "ctf" : "ctf2"));
		_map.level = main.INSTANCE.getServer().getLevelHandler().findLevel((ctfmap ? "ctf" : "ctf2"));
		//Unload the current game level if one is loaded..
		//We swap the in-line if statement to get which one is loaded, not which one will be
		if (main.INSTANCE.getServer().getLevelHandler().findLevel((ctfmap ? "ctf2" : "ctf")) != null)
			main.INSTANCE.getServer().getLevelHandler().unloadLevel(main.INSTANCE.getServer().getLevelHandler().findLevel((ctfmap ? "ctf2" : "ctf")), false);
	}
	public abstract void roundStart();
	
	public abstract void tick();
	
	public abstract void roundEnd();
	
	public Map getMap() {
		return _map;
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
