package net.battlenexus.classic.ctf.commands.shop;

import net.mcforge.API.plugin.PlayerCommand;
import net.mcforge.chat.ChatColor;
import net.mcforge.iomodel.Player;
import net.battlenexus.classic.ctf.gamemode.ctf.CTF;
import net.battlenexus.classic.ctf.main.main;

public abstract class ShopItem extends PlayerCommand {
	
	private Shop shop;
	
	public ShopItem(Shop parent) {
		this.shop = parent;
	}
	
	public Shop getParent() {
		return shop;
	}
	
	private int _price = 0;
	
	private int _level = 0;
	
	private String _name = "";
	
	public abstract String getShopName();
	
	public abstract boolean run(Player p);
	
	public abstract int getDefaultPrice();
	
	public abstract int getDefaultLevel();
	
	public abstract String getLevelUpMessage(Player p);
	
	public String checkUnlock(int level) {
		if (level == getLevel())
			return ChatColor.White + "You unlocked " + ChatColor.Bright_Green + getName();
		return "";
	}
	
	/**
	 * Override this method if you want command-line
	 * arguments in your shop item
	 */
	public boolean run(Player p, String[] args) {
		return run(p);
	}
	
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
	public boolean isOpCommandDefault() {
		return false;
	}

	@Override
	public int getDefaultPermissionLevel() {
		return 0;
	}
	
	public boolean showItem(Player p) {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF))
			return false;
		final int level = ((CTF)main.INSTANCE.getCurrentGame()).getLevel(p);
		return getLevel() <= level;
	}
	
	@Override
	public void execute(Player p, String[] args) {
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		int points = ctf.getPoints(p);
		if (points < getPrice()) {
			p.sendMessage(ChatColor.Dark_Red + "You dont have enough points to buy this item!");
			return;
		}
		if (run(p, args)) {
			ctf.setValue(p, "points", points - getPrice(), false);
			p.sendMessage(ChatColor.White + "" + getPrice() + ChatColor.Bright_Green +  "GP's " + ChatColor.White + "have been removed from your saving!");
			p.sendMessage("&3Transaction complete! Thank you for your purchase.");
		}
	}
}
