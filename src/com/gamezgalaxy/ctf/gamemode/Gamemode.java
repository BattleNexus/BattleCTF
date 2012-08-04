package com.gamezgalaxy.ctf.gamemode;

import com.gamezgalaxy.ctf.map.Map;

public abstract class Gamemode {
	Map _map;
	
	private String name;
	
	protected boolean running = false;
	
	public void setup(Map map) {
		this._map = map;
		//TODO Unload and load shit
	}
	public abstract void roundStart();
	
	public abstract void tick();
	
	public abstract void roundEnd();
	
	public Map getMap() {
		return _map;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void waitForEnd() throws InterruptedException {
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
