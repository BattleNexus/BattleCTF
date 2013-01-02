package net.battlenexus.classic.ctf.events;

import net.mcforge.API.Cancelable;
import net.mcforge.API.EventList;
import net.mcforge.API.player.PlayerEvent;
import net.mcforge.iomodel.Player;
import net.battlenexus.classic.ctf.gamemode.ctf.CTF;

public class PlayerTaggedEvent extends PlayerEvent implements Cancelable {

	private Player _tagger;
	private CTF ctf;
	private boolean _cancel;
	private static EventList events = new EventList();
	public PlayerTaggedEvent(Player who, Player tagger, CTF ctf) {
		super(who);
		this._tagger = tagger;
		this.ctf = ctf;
	}
	
	public CTF getGame() {
		return ctf;
	}
	
	public Player getTagger() {
		return _tagger;
	}

	@Override
	public EventList getEvents() {
		return events;
	}
	
	public static EventList getEventList() {
		return events;
	}

	@Override
	public boolean isCancelled() {
		return _cancel;
	}

	@Override
	public void setCancel(boolean cancel) {
		this._cancel = cancel;
	}

}
