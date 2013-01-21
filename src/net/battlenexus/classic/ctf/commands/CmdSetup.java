package net.battlenexus.classic.ctf.commands;

import java.io.File;
import java.util.ArrayList;

import net.mcforge.API.CommandExecutor;
import net.mcforge.API.ManualLoad;
import net.mcforge.API.action.Action;
import net.mcforge.API.action.BlockChangeAction;
import net.mcforge.API.action.ChatAction;
import net.mcforge.API.plugin.PlayerCommand;
import net.mcforge.chat.ChatColor;
import net.mcforge.iomodel.Player;
import net.battlenexus.classic.ctf.gamemode.ctf.CTF;
import net.battlenexus.classic.ctf.main.main;
import net.battlenexus.classic.ctf.map.utl.ConfigGraber;

@ManualLoad
public class CmdSetup extends PlayerCommand {

	@Override
	public void execute(Player player, String[] args) {
		if (args.length == 0) {
			player.sendMessage("/setup <mapname> - Setup a new map for CTF!");
			return;
		}
		String name = "";
		for (String arg : args) {
			name += arg + " ";
		}
		name.trim();
		Thread s = new Setup(player, name);
		s.start();
	}

	@Override
	public String[] getShortcuts() {
		return new String[] { "s" };
	}

	@Override
	public String getName() {
		return "setup";
	}


	@Override
	public boolean isOpCommandDefault() {
		return true;
	}

	@Override
	public int getDefaultPermissionLevel() {
		return 100;
	}
	
	private class Setup extends Thread {
		
		private Player p;
		private String map;
		public Setup(Player p, String map) { this.p = p; this.map = map; }
		@Override
		public void run() {
			ArrayList<String> config = new ArrayList<String>();
			config.add("CONFIG.2.2");
			try {
				ChatAction c = null;
				Action<ChatAction> a = new ChatAction();
				a.setPlayer(p);
				p.sendMessage("How many players is this map for?");
				c = a.waitForResponse();
				int teams = Integer.parseInt(c.getMessage());
				config.add("core.teamcount = " + teams);
				/**
				 * FLAG CONFIG
				 */
				for (int i = 0; i < teams; i++) {
					BlockChangeAction b = null;
					Action<BlockChangeAction> bb = new BlockChangeAction();
					bb.setPlayer(p);
					p.sendMessage("Break the flag for " + CTF.TEAM_NAMES[i]);
					b = bb.waitForResponse();
					config.add("flag." + CTF.SYSTEM_TEAM_NAME[i] + ".x = " + b.getX());
					config.add("flag." + CTF.SYSTEM_TEAM_NAME[i] + ".y = " + b.getY());
					config.add("flag." + CTF.SYSTEM_TEAM_NAME[i] + ".z = " + b.getZ());
					config.add("block." + CTF.SYSTEM_TEAM_NAME[i] + " = " + CTF.DEFAULT_FLAGS[i]);
					b = null;
					bb = null;
					Thread.sleep(100);
				}
				p.clearChatScreen();
				/**
				 * AREA CONFIG
				 */
				for (int z = 0; z < teams; z++) {
					int ii = 0;
					while (true) {
						p.sendMessage("Stand in one corner of one of " + CTF.TEAM_NAMES[z] + ChatColor.White + "'s owned area");
						p.sendMessage("And say apple!");
						c = null;
						while (true) {
							a = new ChatAction();
							a.setPlayer(p);
							c = a.waitForResponse();
							if (c.getMessage().equalsIgnoreCase("apple"))
								break;
						}
						int X1 = p.getBlockX();
						int Z1 = p.getBlockZ();
						p.sendMessage("Now stand in the opposite of the owned area and say");
						p.sendMessage("cornflakes!");
						while (true) {
							a = new ChatAction();
							a.setPlayer(p);
							c = a.waitForResponse();
							if (c.getMessage().equalsIgnoreCase("cornflakes"))
								break;
						}
						int X2 = p.getBlockX();
						int Z2 = p.getBlockZ();
						if (X1 == X2 || Z1 == Z2) {
							p.sendMessage("Two of the values equal eachother..");
							p.sendMessage("They shouldnt equal eachother..");
							p.sendMessage("Lets try again!");
							continue;
						}
						config.add("area." + CTF.SYSTEM_TEAM_NAME[z] + "." + ii + ".smallx = " + Math.min(X1, X2));
						config.add("area." + CTF.SYSTEM_TEAM_NAME[z] + "." + ii + ".smallz = " + Math.min(Z1, Z2));
						config.add("area." + CTF.SYSTEM_TEAM_NAME[z] + "." + ii + ".bigx = " + Math.max(X1, X2));
						config.add("area." + CTF.SYSTEM_TEAM_NAME[z] + "." + ii + ".bigz = " + Math.max(Z1, Z2));
						p.sendMessage("Area configed!");
						p.sendMessage("Does " + CTF.TEAM_NAMES[z] + ChatColor.White + " own another area?");
						ii++;
						a = new ChatAction();
						a.setPlayer(p);
						c = a.waitForResponse();
						p.clearChatScreen();
						if (c.getMessage().equalsIgnoreCase("yes")) {
							p.sendMessage("Ok! Lets config that one!");
							continue;
						}
						else {
							p.sendMessage("Ok, " + ((z + 1 >= teams) ? "on to the next thing." : "lets move to the next team.."));
							break;
						}
					}
				}
				/**
				 * SPAWN & SAFEMODE CONFIG
				 */
				for (int x = 0; x < teams; x++) {
					p.sendMessage("Stand on the spawn point for the " + CTF.TEAM_NAMES[x]);
					p.sendMessage("And say apples");
					c = null;
					while (true) {
						a = new ChatAction();
						a.setPlayer(p);
						c = a.waitForResponse();
						if (c.getMessage().equalsIgnoreCase("apple"))
							break;
					}
					p.sendMessage("Setting spawn..");
					config.add("spawn." + CTF.SYSTEM_TEAM_NAME[x] + ".x = " + p.getBlockX());
					config.add("spawn." + CTF.SYSTEM_TEAM_NAME[x] + ".y = " + p.getBlockY());
					config.add("spawn." + CTF.SYSTEM_TEAM_NAME[x] + ".z = " + p.getBlockZ());
					p.sendMessage("Guessing safezone..");
					int bX = p.getBlockX();
					int bY = p.getBlockY();
					int bZ = p.getBlockZ();
					while (p.getLevel().getTile(bX + 1, bY, bZ).getVisibleBlock() == 0) {
						bX++;
					}
					config.add("safezone." + CTF.SYSTEM_TEAM_NAME[x] + ".bigx = " + bX);
					bX = p.getBlockX();
					while (p.getLevel().getTile(bX - 1, bY, bZ).getVisibleBlock() == 0) {
						bX--;
					}
					config.add("safezone." + CTF.SYSTEM_TEAM_NAME[x] + ".smallx = " + bX);
					bX = p.getBlockX();
					while (p.getLevel().getTile(bX, bY + 1, bZ).getVisibleBlock() == 0) {
						bY++;
					}
					config.add("safezone." + CTF.SYSTEM_TEAM_NAME[x] + ".bigy = " + bY);
					bY = p.getBlockY();
					while (p.getLevel().getTile(bX, bY - 1, bZ).getVisibleBlock() == 0) {
						bY--;
					}
					config.add("safezone." + CTF.SYSTEM_TEAM_NAME[x] + ".smally = " + bY);
					bY = p.getBlockY();
					while (p.getLevel().getTile(bX, bY, bZ + 1).getVisibleBlock() == 0) {
						bZ++;
					}
					config.add("safezone." + CTF.SYSTEM_TEAM_NAME[x] + ".bigz = " + bZ);
					bZ = p.getBlockZ();
					while (p.getLevel().getTile(bX, bY, bZ - 1).getVisibleBlock() == 0) {
						bZ--;
					}
					config.add("safezone." + CTF.SYSTEM_TEAM_NAME[x] + ".smallz = " + bZ);
					bZ = p.getBlockZ();
					p.clearChatScreen();
				}
				p.sendMessage("Adding gamemodes..");
				config.add("core.gamemode = net.battlenexus.classic.ctf.gamemode.ctf.CTF");
				config.add("stalemate.action = net.battlenexus.classic.ctf.gamemode.ctf.stalemate.actions.DropFlags");
				config.add("stalemate.action = net.battlenexus.classic.ctf.gamemode.ctf.stalemate.actions.NextTag");
				p.sendMessage("Making levels..");
				if (!new File("backups/ctf/" + ConfigGraber.getMapmax("config/")).exists())
					new File("backups/ctf/" + ConfigGraber.getMapmax("config/")).mkdirs();
				if (!new File("backups/ctf2/" + ConfigGraber.getMapmax("config/")).exists())
					new File("backups/ctf2/" + ConfigGraber.getMapmax("config/")).mkdirs();
				ConfigGraber.copyfile("levels/" + p.getLevel().getName() + ".ggs", "backups/ctf/" + ConfigGraber.getMapmax("config/") + "/ctf.ggs");
				ConfigGraber.copyfile("levels/" + p.getLevel().getName() + ".ggs", "backups/ctf2/" + ConfigGraber.getMapmax("config/") + "/ctf2.ggs");
				p.sendMessage("Bumping..");
				ConfigGraber.bump("config/");
				p.sendMessage("Generating config file..");
				ConfigGraber.addMap(map, config);
				p.sendMessage("Cleaning up..");
				config.clear();
				a = null;
				c = null;
				p.sendMessage("Stopping..");
				Thread.sleep(500);
				p.sendMessage(map + " has been added!");
				main.INSTANCE.start();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void help(CommandExecutor executor) {
		executor.sendMessage("/setup <mapname> - Setup a config file for <mapname>!");
	}

}
