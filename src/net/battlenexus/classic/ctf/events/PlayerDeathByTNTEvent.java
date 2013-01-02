package net.battlenexus.classic.ctf.events;

import net.mcforge.API.EventList;
import net.mcforge.iomodel.Player;
import net.battlenexus.classic.ctf.blocks.TNT_Explode;

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
