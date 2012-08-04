package com.gamezgalaxy.ctf.events;

import com.gamezgalaxy.GGS.API.EventHandler;
import com.gamezgalaxy.GGS.API.Listener;
import com.gamezgalaxy.GGS.API.player.PlayerBlockChangeEvent;
import com.gamezgalaxy.GGS.world.PlaceMode;
import com.gamezgalaxy.ctf.gamemode.ctf.CTF;
import com.gamezgalaxy.ctf.main.main;

public class EventListener implements Listener {
	@EventHandler
	public void onBreak(PlayerBlockChangeEvent event) {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF))
			return;
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		if (event.getPlaceType() == PlaceMode.BREAK) {
			if (ctf.isOnBlueTeam(event.getPlayer())) {
				if (event.getX() == ctf.getMap().redx && event.getY() == ctf.getMap().redy && event.getZ() == ctf.getMap().redz && ctf.redholder == null) { //Getting the red flag
					ctf.redholder = event.getPlayer();
					main.GlobalMessage("&1" + event.getPlayer().username + " tood the &4RED TEAM'S FLAG!");
				}
				else if (event.getX() == ctf.getMap().bluex && event.getY() == ctf.getMap().bluey && event.getZ() == ctf.getMap().bluez && ctf.redholder == event.getPlayer()) { //Return the flag
					
				}
			}
		}
	}

}
