package com.gamezgalaxy.ctf.commands.shop;

import java.util.ArrayList;

import com.gamezgalaxy.GGS.API.EventHandler;
import com.gamezgalaxy.GGS.API.Listener;
import com.gamezgalaxy.GGS.chat.ChatColor;
import com.gamezgalaxy.GGS.iomodel.Player;
import com.gamezgalaxy.ctf.events.PlayerTaggedEvent;
import com.gamezgalaxy.ctf.gamemode.ctf.CTF;
import com.gamezgalaxy.ctf.gamemode.ctf.utl.Team;
import com.gamezgalaxy.ctf.main.main;

public class Cloak extends ShopItem implements Listener {

	private boolean started = false;
	private ArrayList<Player> cloaked = new ArrayList<Player>();
	@Override
	public String getShopName() {
		return "clock";
	}

	@Override
	public int getDefaultPrice() {
		return 300;
	}

	@Override
	public int getDefaultLevel() {
		return 1;
	}

	@Override
	public boolean run(Player p) {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF)) {
			p.sendMessage(ChatColor.Dark_Red + "You must be playing CTF to use this!");
			return false;
		}
		if (!started) {
			main.INSTANCE.getServer().getEventSystem().registerEvents(this);
			started = true;
		}
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		Team t = ctf.getTeam(p);
		if (t == null) {
			p.sendMessage(ChatColor.Dark_Red + "You must be on a team!");
			return false;
		}
		if (cloaked.contains(p)) {
			p.sendMessage(ChatColor.Dark_Red + "You are already cloaked!");
			return false;
		}
		for (Team current : ctf.teams) {
			if (current == t)
				continue;
			for (Player pp : current.members) {
				pp.Despawn(p);
			}
		}
		int seconds = ctf.getLevel(p) * 2 + 15;
		p.sendMessage(ChatColor.Bright_Green + "You are now completely invisable to the other team(s)!");
		p.sendMessage(ChatColor.Bright_Green + "You will be invisable for " + ChatColor.White + seconds + ChatColor.Bright_Green + " seconds..");
		cloaked.add(p);
		for (; seconds > 0; --seconds) {
			if (!cloaked.contains(p))
				break;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) { }
		}
		for (Team current : ctf.teams) {  
			if (current == t)
				continue;
			for (Player pp : current.members) {
				pp.spawnPlayer(p);
			}
		}
		p.sendMessage(ChatColor.Dark_Red + "You are now visable to the other team!");
		return true;
	}
	
	@EventHandler
	public void tagged(PlayerTaggedEvent event) {
		if (event.isCancelled())
			return;
		if (cloaked.contains(event.getPlayer())) {
			cloaked.remove(event.getPlayer());
			event.getPlayer().sendMessage(ChatColor.Dark_Red + "You have been tagged!");
		}
		else if (cloaked.contains(event.getTagger())) {
			cloaked.remove(event.getTagger());
			event.getTagger().sendMessage(ChatColor.Dark_Red + "YOU HAVE BEEN SPOTTED!");
		}
	}

	@Override
	public String getLevelUpMessage(Player p) {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF))
			return "";
		CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		if (ctf.getLevel(p) % 3 == 0)
			return ChatColor.Bright_Green + "+ " + ChatColor.White + "Your cloak has been upgraded! It will now last for " + (ctf.getLevel(p) * 2 + 15) + " seconds!";
		return "";
	}

}
