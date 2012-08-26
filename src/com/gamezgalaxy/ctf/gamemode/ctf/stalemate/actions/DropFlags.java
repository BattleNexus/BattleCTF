package com.gamezgalaxy.ctf.gamemode.ctf.stalemate.actions;

import com.gamezgalaxy.GGS.chat.ChatColor;
import com.gamezgalaxy.ctf.gamemode.ctf.CTF;
import com.gamezgalaxy.ctf.gamemode.ctf.stalemate.Action;
import com.gamezgalaxy.ctf.gamemode.ctf.utl.Team;

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
	}

}
