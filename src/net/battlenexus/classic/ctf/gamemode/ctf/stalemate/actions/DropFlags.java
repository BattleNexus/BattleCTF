package net.battlenexus.classic.ctf.gamemode.ctf.stalemate.actions;

import net.mcforge.chat.ChatColor;
import net.battlenexus.classic.ctf.gamemode.ctf.CTF;
import net.battlenexus.classic.ctf.gamemode.ctf.stalemate.Action;
import net.battlenexus.classic.ctf.gamemode.ctf.utl.Team;
import net.battlenexus.classic.ctf.main.main;

public class DropFlags extends Action {

	@Override
	public String getMessage() {
		return ChatColor.Dark_Blue + "All Flags will be dropped!";
	}

	@Override
	public void performaction(CTF c) {
		for (int i = 0; i < c.holders.size(); i++) {
			Team b = c.holders.get(c.holders.keySet().toArray()[i]);
			c.resetFlag(b);
		}
		c.holders.clear();
		main.INSTANCE.getEvents().clear();
	}

}
