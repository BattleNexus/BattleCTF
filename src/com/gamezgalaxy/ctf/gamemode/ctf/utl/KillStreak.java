package com.gamezgalaxy.ctf.gamemode.ctf.utl;

public enum KillStreak {
	Double(2, "DOUBLE KILL"),
	Triple(3, "TRIPLE KILL"),
	Overkill(6, "OVERKILL");
	
	
	int amount;
	String message;
	KillStreak(int amount, String message) { this.amount = amount; this.message = message; }
	
	public int getAmount() {
		return amount;
	}
	
	public String getMessage() {
		return message;
	}
	
	public boolean equal(Object obj) {
		if (!(obj instanceof KillStreak))
			return false;
		KillStreak k = (KillStreak)obj;
		return k.amount == amount;
	}
}
