package com.gamezgalaxy.ctf.commands.shop;

import com.gamezgalaxy.GGS.API.CommandExecutor;
import com.gamezgalaxy.GGS.chat.ChatColor;
import com.gamezgalaxy.GGS.groups.Group;
import com.gamezgalaxy.GGS.iomodel.Player;
import com.gamezgalaxy.ctf.gamemode.ctf.CTF;
import com.gamezgalaxy.ctf.main.main;

public class Rank extends ShopItem {

	@Override
	public String getShopName() {
		return "Rank";
	}

	@Override
	public boolean run(Player p) {
		Group g = Group.find(super.getName());
		if (g != null) {
			p.setGroup(g);
			main.GlobalMessage(p.getDisplayName() + " &3just bought the rank " + g.name + " &3rank!");
			main.GlobalMessage("&3Be sure to congradulate them!");
			return true;
		}
		p.sendMessage("That group couldnt be found!");
		return false;
	}

	@Override
	public int getDefaultPrice() {
		return 0;
	}

	@Override
	public int getDefaultLevel() {
		return 0;
	}

	@Override
	public String getLevelUpMessage(Player p) {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF))
			return "";
		CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		if (ctf.getLevel(p) == super.getLevel())
			return ChatColor.White + "You can now buy the " + super.getName() + " rank!";
		return "";
	}

	@Override
	public void help(CommandExecutor executor) {
	}

}
