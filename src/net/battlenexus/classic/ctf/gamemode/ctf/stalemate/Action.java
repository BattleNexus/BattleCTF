package net.battlenexus.classic.ctf.gamemode.ctf.stalemate;

import net.battlenexus.classic.ctf.gamemode.ctf.CTF;

public abstract class Action {
	
	public abstract String getMessage();
	public abstract void performaction(CTF c);

}
