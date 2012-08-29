package com.gamezgalaxy.ctf.events;

import com.gamezgalaxy.GGS.API.EventList;
import com.gamezgalaxy.GGS.iomodel.Player;
import com.gamezgalaxy.ctf.blocks.TNT_Explode;

public class PlayerDeathByTNTEvent extends PlayerDeathEvent<TNT_Explode> {

	private static EventList events = new EventList();
	public PlayerDeathByTNTEvent(Player who, Player killer, Killable<TNT_Explode> object) {
		super(who, killer, object);
	}
	
	@Override
	public EventList getEvents() {
		return events;
	}
	
	public static EventList getEventList() {
		return events;
	}

}
