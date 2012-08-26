package com.gamezgalaxy.ctf.events;

import com.gamezgalaxy.GGS.API.Cancelable;
import com.gamezgalaxy.GGS.API.EventList;
import com.gamezgalaxy.GGS.API.player.PlayerEvent;
import com.gamezgalaxy.GGS.iomodel.Player;
import com.gamezgalaxy.ctf.gamemode.ctf.CTF;

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
	public void Cancel(boolean cancel) {
		this._cancel = cancel;
	}

}
