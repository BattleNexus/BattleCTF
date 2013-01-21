package net.battlenexus.classic.ctf.commands.shop;

import net.mcforge.API.CommandExecutor;
import net.mcforge.API.ManualLoad;
import net.mcforge.chat.ChatColor;
import net.mcforge.iomodel.Player;

@ManualLoad
public class Mine extends ShopItem {

	public Mine(Shop parent) {
		super(parent);
	}

	@Override
	public String getShopName() {
		return "mine";
	}

	@Override
	public boolean run(Player p) {
		if (p.hasAttribute("mine")) {
			int value = p.getAttribute("mine");
			value++;
			p.setAttribute("mine", value);
		}
		else
			p.setAttribute("mine", 1);
		p.saveAttribute("mine");
		p.sendMessage(ChatColor.Bright_Green + "+" + ChatColor.White + " You bought 1 mine!");
		p.sendMessage("You have " + p.getAttribute("mine") + " mine(s)");
		return true;
	}

	@Override
	public int getDefaultPrice() {
		return 40;
	}

	@Override
	public int getDefaultLevel() {
		return 1;
	}

	@Override
	public String getLevelUpMessage(Player p) {
		return "";
	}

	@Override
	public void help(CommandExecutor arg0) {
		
	}

}
