package com.gamezgalaxy.ctf.events;

import com.gamezgalaxy.GGS.API.Cancelable;
import com.gamezgalaxy.GGS.API.EventList;
import com.gamezgalaxy.GGS.API.player.PlayerEvent;
import com.gamezgalaxy.GGS.iomodel.Player;

public abstract class PlayerDeathEvent<T> extends PlayerEvent implements Cancelable {

	private static EventList events = new EventList();
	private Player killer;
	private boolean _cancel;
	private Killable<T> object;
	public PlayerDeathEvent(Player who, Player killer, Killable<T> object) {
		super(who);
		this.killer = killer;
		this.object = object;
	}

	@Override
	public EventList getEvents() {
		return events;
	}
	
	public static EventList getEventList() {
		return events;
	}
	
	public Player getKiller() {
		return killer;
	}

	@Override
	public boolean isCancelled() {
		return _cancel;
	}

	@Override
	public void Cancel(boolean cancel) {
		this._cancel = cancel;
	}
	
	public T getObject() {
		return object.getObject();
	}
}
