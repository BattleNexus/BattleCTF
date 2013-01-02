package net.battlenexus.classic.ctf.commands;

import net.mcforge.API.CommandExecutor;
import net.mcforge.API.ManualLoad;
import net.mcforge.API.plugin.PlayerCommand;
import net.mcforge.chat.ChatColor;
import net.mcforge.iomodel.Player;
import net.battlenexus.classic.ctf.commands.shop.ShopItem;
import net.battlenexus.classic.ctf.gamemode.ctf.CTF;
import net.battlenexus.classic.ctf.main.main;

@ManualLoad
public class CmdShop extends PlayerCommand {

	@Override
	public String[] getShortcuts() {
		return new String[] { "store" };
	}

	@Override
	public String getName() {
		return "shop";
	}


	@Override
	public boolean isOpCommandDefault() {
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
			if (s.getLevel() <= ctf.getLevel(p)) {
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
				if (s.getLevel() <= level) {
					i++;
					player.sendMessage(ChatColor.Yellow + "" + i + ". " + ChatColor.White + s.getName() + " (" + s.getPrice() + " GP's)");
				}
				if (i >= 10)
					break;
			}
			player.sendMessage(ChatColor.Yellow + "Page 1/" + getPages(player, ctf));
			player.sendMessage("To choose a item, type " + ChatColor.Aqua + "/shop <item name>");
			player.sendMessage("or, " + ChatColor.Aqua + "/<item name>");
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
					if (s.getLevel() <= level) {
						i++;
						if (i < start)
							continue;
						player.sendMessage(ChatColor.Yellow + "" + i + ". " + ChatColor.White + s.getName() + " (" + s.getPrice() + " GP's)");
					}
				}
				player.sendMessage(ChatColor.Yellow + "Page " + page + "/" + getPages(player, ctf));
				player.sendMessage("To choose a item, type " + ChatColor.Aqua + "/shop <item name>");
				player.sendMessage("or, " + ChatColor.Aqua + "/<item name>");
				return;
			} catch (Exception e) {
				ShopItem item = findItem(args[0], ctf.getLevel(player));
				if (item == null) {
					player.sendMessage(ChatColor.Dark_Red + "That item does not exist!");
					return;
				}
				String[] tempargs = new String[args.length - 1];
				System.arraycopy(args, 1, tempargs, 0, args.length - 1);
				item.execute(player, tempargs);
			}
		}
	}

	@Override
	public void help(CommandExecutor executor) {
		// TODO Auto-generated method stub
		
	}

}
