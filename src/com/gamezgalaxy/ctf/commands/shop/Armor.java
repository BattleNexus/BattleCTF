package com.gamezgalaxy.ctf.commands.shop;

import com.gamezgalaxy.GGS.API.EventHandler;
import com.gamezgalaxy.GGS.API.Listener;
import com.gamezgalaxy.GGS.chat.ChatColor;
import com.gamezgalaxy.GGS.iomodel.Player;
import com.gamezgalaxy.ctf.events.PlayerDeathEvent;
import com.gamezgalaxy.ctf.main.main;
import com.gamezgalaxy.ctf.gamemode.ctf.*;

public class Armor extends ShopItem {

	@Override
	public String getShopName() {
		return "Armor";
	}

	@Override
	public boolean run(Player p) {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF))
			return false;
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		ArmorObject o = new ArmorObject(p, ctf.getLevel(p) / 3);
		o.start();
		return true;
	}

	@Override
	public int getDefaultPrice() {
		return 100;
	}

	@Override
	public int getDefaultLevel() {
		return 3;
	}

	@Override
	public String getLevelUpMessage(Player p) {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF))
			return "";
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		return ChatColor.White + "Your armor power is now " + ChatColor.Bright_Green + (ctf.getLevel(p) / 3) + ChatColor.White + "!";
	}
	
	private class ArmorObject implements Listener {
		private Player player;
		private int power = 1;
		public ArmorObject(Player player, int power) {
			this.player = player;
			this.power = power;
		}
		
		@SuppressWarnings("unused")
		@EventHandler
		public void event(PlayerDeathEvent<?> event) {
			if (event.getPlayer() == player) {
				power--;
				event.Cancel(true);
				if (power <= 0) {
					player.sendMessage(ChatColor.Dark_Red + "Your armmor has ran out!");
					PlayerDeathEvent.getEventList().unregister(this);
				}
			}
		}
		
		public void start() {
			main.INSTANCE.getServer().getEventSystem().registerEvents(this);
			player.sendMessage(ChatColor.Dark_Green + "Your armor is now active with the power of " + power + "!");
		}
	}
}
