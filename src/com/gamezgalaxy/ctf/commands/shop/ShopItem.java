package com.gamezgalaxy.ctf.commands.shop;

import com.gamezgalaxy.GGS.API.plugin.Command;
import com.gamezgalaxy.GGS.chat.ChatColor;
import com.gamezgalaxy.GGS.iomodel.Player;
import com.gamezgalaxy.ctf.gamemode.ctf.CTF;
import com.gamezgalaxy.ctf.main.main;

public abstract class ShopItem extends Command {
	
	private int _price = 0;
	
	private int _level = 0;
	
	private String _name = "";
	
	public abstract String getShopName();
	
	public abstract boolean run(Player p);
	
	public abstract int getDefaultPrice();
	
	public abstract int getDefaultLevel();
	
	public abstract String getLevelUpMessage(Player p);
	
	public int getPrice() {
		return (_price > 0 ? _price : getDefaultPrice());
	}
	
	public void setPrice(int value) {
		_price = value;
	}
	
	public int getLevel() {
		return (_level > 0 ? _level : getDefaultLevel());
	}
	
	public void setLevel(int value) {
		_level = value;
	}
	
	public void setName(String name) {
		_name = name;
	}
	
	@Override
	public String[] getShortcuts() {
		return new String[0];
	}

	@Override
	public String getName() {
		return (_name.equals("") ? getShopName() : _name);
	}

	@Override
	public boolean isOpCommand() {
		return false;
	}

	@Override
	public int getDefaultPermissionLevel() {
		return 0;
	}
	
	@Override
	public void execute(Player p, String[] args) {
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		int points = ctf.getPoints(p);
		if (points < getPrice()) {
			p.sendMessage(ChatColor.Dark_Red + "You dont have enough points to buy this item!");
			return;
		}
		if (run(p)) {
			ctf.setValue(p, "points", points - getPrice(), false);
			p.sendMessage(ChatColor.White + "" + getPrice() + ChatColor.Bright_Green +  "GP's " + ChatColor.White + "have been taken from your account!");
			p.sendMessage("Thank you for using the shop!");
		}
	}
}
