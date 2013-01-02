package com.gamezgalaxy.ctf.commands.shop;

import net.mcforge.API.CommandExecutor;
import net.mcforge.API.ManualLoad;
import net.mcforge.chat.ChatColor;
import net.mcforge.groups.Group;
import net.mcforge.iomodel.Player;
import com.gamezgalaxy.ctf.gamemode.ctf.CTF;
import com.gamezgalaxy.ctf.main.main;

@ManualLoad
public class Rank extends ShopItem {

	public Rank(Shop parent) {
		super(parent);
	}

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
	public boolean showItem(Player p) {
		/*final Group current = p.getGroup();
		Group nexthighest = null;
		for (Group g : Group.getGroupList()) {
			if (rankBuyable(g) && g.permissionlevel > current.permissionlevel) {
				if (nexthighest == null) nexthighest = g;
				if (nexthighest.permissionlevel > g.permissionlevel) nexthighest = g;
			}
		}
		if (nexthighest == null || !rankBuyable(nexthighest) || !nexthighest.name.equals(getName()))
			return false;*/
		return super.showItem(p);
	}
	
	@SuppressWarnings("unused")
	private boolean rankBuyable(Group g) {
		for (ShopItem s : getParent().items) {
			if (s.getName().equals(g.name))
				return true;
		}
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
		executor.sendMessage("/rank - Rank up.");
	}

}
