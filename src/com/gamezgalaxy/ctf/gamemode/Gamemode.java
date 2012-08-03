package com.gamezgalaxy.ctf.gamemode;

import com.gamezgalaxy.ctf.map.Map;

public abstract class Gamemode {
	Map _map;
	
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

}
