package com.gamezgalaxy.ctf.gamemode.ctf.stalemate.actions;

import net.mcforge.API.EventHandler;
import net.mcforge.API.Listener;
import net.mcforge.chat.ChatColor;
import net.mcforge.iomodel.Player;

import com.gamezgalaxy.ctf.events.PlayerTaggedEvent;
import com.gamezgalaxy.ctf.gamemode.ctf.CTF;
import com.gamezgalaxy.ctf.gamemode.ctf.stalemate.*;
import com.gamezgalaxy.ctf.gamemode.ctf.utl.Team;
import com.gamezgalaxy.ctf.main.main;
public class NextTag extends Action implements Listener {

	@Override
	public String getMessage() {
		return ChatColor.Purple + "Next team tagged will drop the flag.";
	}

	@Override
	public void performaction(CTF c) {
		c.getMain().getServer().getEventSystem().registerEvents(this);
	}

	@EventHandler
	public void onTagged(PlayerTaggedEvent event) {
		Team t = event.getGame().getTeam(event.getPlayer());
		if (t != null) {
			Team holding = findHolding(t, event.getGame());
			if (holding != null) {
				main.GlobalMessage(event.getPlayer().getDisplayName() + ChatColor.White + " was tagged so the " + t.name + "'s" + ChatColor.White + " flag was reset!");
				event.getGame().resetFlag(holding);
				PlayerTaggedEvent.getEventList().unregister(this);
			}
		}
	}

	private Team findHolding(Team t, CTF c) {
		for (int i = 0; i < c.holders.size(); i++) {
			Player p = (Player)c.holders.keySet().toArray()[i];
			if (c.getTeam(p) == t)
				return c.holders.get(p);
		}
		return null;
	}

}
