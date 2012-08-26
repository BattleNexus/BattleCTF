package com.gamezgalaxy.ctf.commands;

import com.gamezgalaxy.GGS.API.plugin.Command;
import com.gamezgalaxy.GGS.chat.ChatColor;
import com.gamezgalaxy.GGS.iomodel.Player;
import com.gamezgalaxy.ctf.commands.shop.ShopItem;
import com.gamezgalaxy.ctf.gamemode.ctf.CTF;
import com.gamezgalaxy.ctf.main.main;

public class CmdShop extends Command {

	@Override
	public String[] getShortcuts() {
		return new String[] { "store" };
	}

	@Override
	public String getName() {
		return "shop";
	}

	@Override
	public boolean isOpCommand() {
		return false;
	}

	@Override
	public int getDefaultPermissionLevel() {
		return 0;
	}
	
	private int getPages(Player p, CTF ctf) {
		int pages = 1;
		int i = 0;
		for (ShopItem s : main.INSTANCE.getShop().items) {
			if (s.getLevel() >= ctf.getLevel(p)) {
				i++;
				if (i >= 10) {
					i = 0;
					pages++;
				}
			}
		}
		return pages;
	}
	
	private ShopItem findItem(String name, int level) {
		ShopItem i = null;
		for (ShopItem item : main.INSTANCE.getShop().items) {
			if (level >= item.getLevel()) {
				if (item.getName().equalsIgnoreCase(name))
					return item;
				else if (item.getName().indexOf(name) != -1 && i == null)
					i = item;
				else if (item.getName().indexOf(name) != -1 && i != null)
					return null;
			}
		}
		return i;
	}

	@Override
	public void execute(Player player, String[] args) {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF))
			return;
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		if (args.length == 0) {
			final int level = ctf.getLevel(player);
			player.sendMessage(ChatColor.Yellow + "Avarable shop items:");
			int i = 0;
			for (ShopItem s : main.INSTANCE.getShop().items) {
				if (s.getLevel() >= level) {
					i++;
					player.sendMessage(ChatColor.Yellow + "" + i + ". " + ChatColor.White + s.getName() + "(" + s.getPrice() + " GP's)");
				}
				if (i >= 10)
					break;
			}
			player.sendMessage(ChatColor.Yellow + "Page 1/" + getPages(player, ctf));
			player.sendMessage("To choose a item, type " + ChatColor.Aqua + "/shop <item name>");
			return;
		}
		else {
			try {
				int page = Integer.parseInt(args[0]);
				if (page > getPages(player, ctf) || page <= 0) {
					player.sendMessage(ChatColor.Dark_Red + "Page doesnt exist!");
					return;
				}
				int i = 0;
				final int level = ctf.getLevel(player);
				int start = 10 * (page - 1);
				for (ShopItem s : main.INSTANCE.getShop().items) {
					if (s.getLevel() >= level) {
						i++;
						if (i < start)
							continue;
						player.sendMessage(ChatColor.Yellow + "" + i + ". " + ChatColor.White + s.getName() + "(" + s.getPrice() + " GP's)");
					}
				}
				player.sendMessage(ChatColor.Yellow + "Page " + page + "/" + getPages(player, ctf));
				player.sendMessage("To choose a item, type " + ChatColor.Aqua + "/shop <item name>");
				return;
			} catch (Exception e) {
				ShopItem item = findItem(args[0], ctf.getLevel(player));
				if (item == null) {
					player.sendMessage(ChatColor.Dark_Red + "That item does not exist!");
					return;
				}
				item.execute(player, args);
			}
		}
	}

}
