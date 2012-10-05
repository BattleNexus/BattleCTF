package com.gamezgalaxy.ctf.commands.shop;

import com.gamezgalaxy.GGS.API.CommandExecutor;
import com.gamezgalaxy.GGS.chat.ChatColor;
import com.gamezgalaxy.GGS.chat.ColorFormatException;
import com.gamezgalaxy.GGS.iomodel.Player;
import com.gamezgalaxy.ctf.gamemode.ctf.CTF;
import com.gamezgalaxy.ctf.gamemode.ctf.utl.Team;
import com.gamezgalaxy.ctf.main.main;

public class Disguise extends ShopItem {

	@Override
	public String getShopName() {
		return "Disguise";
	}

	@Override
	public boolean run(Player p) {
		p.sendMessage("You must specify a team!");
		return false;
	}
	
	@Override
	public boolean run(Player p, String[] args) {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF))
			return false;
		if (args.length == 0) {
			p.sendMessage("You must specify a team!");
			return false;
		}
		final String name = args[0];
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		Team choice = null;
		for (Team t : ctf.teams) {
			if (t.system_name.indexOf(name) != -1) {
				choice = t;
				break;
			}
		}
		if (choice == null) {
			p.sendMessage("That team doesnt exist!");
			p.sendMessage("Here are a list of teams!");
			for (Team t : ctf.teams) {
				p.sendMessage(t.system_name);
			}
			return false;
		}
		String color = choice.name.substring(0, 2);
		if (!color.startsWith("&")) {
			p.sendMessage("For some reason, I couldnt get the color for that team :/");
			return false;
		}
		ChatColor c = null;
		try {
			c = ChatColor.parse(color);
		}
		catch (ColorFormatException e) {
			p.sendMessage("For some reason, I couldnt set your color :/");
			return false;
		}
		p.setDisplayColor(c);
		p.sendMessage(ChatColor.Bright_Green + "You are now disguised as " + args[0]);
		Thread t = new Timer(p, choice, ctf.getTeam(p));
		t.start();
		return true;
	}

	@Override
	public int getDefaultPrice() {
		return 300;
	}

	@Override
	public int getDefaultLevel() {
		return 7;
	}

	@Override
	public String getLevelUpMessage(Player p) {
		return null;
	}
	private class Timer extends Thread {
		
		Player p;
		Team t;
		Team orginal;
		public Timer(Player p, Team t, Team orginal) { this.p = p; this.t = t; this.orginal = orginal; }
		@Override
		public void run() {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			p.sendMessage(ChatColor.Dark_Red + "You are no longer disguised as " + t.name);
			orginal.setColor(p);
		}
	}
	@Override
	public void help(CommandExecutor executor) {
		// TODO Auto-generated method stub
		
	}

}
