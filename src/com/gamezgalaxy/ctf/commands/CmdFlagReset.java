package com.gamezgalaxy.ctf.commands;

import com.gamezgalaxy.GGS.API.CommandExecutor;
import com.gamezgalaxy.GGS.API.plugin.PlayerCommand;
import com.gamezgalaxy.GGS.chat.ChatColor;
import com.gamezgalaxy.GGS.iomodel.Player;
import com.gamezgalaxy.ctf.gamemode.ctf.CTF;
import com.gamezgalaxy.ctf.gamemode.ctf.utl.Team;
import com.gamezgalaxy.ctf.main.main;

public class CmdFlagReset extends PlayerCommand {

	@Override
	public String[] getShortcuts() {
		return new String[0];
	}

	@Override
	public String getName() {
		return "flagreset";
	}

	@Override
	public boolean isOpCommand() {
		return true;
	}

	@Override
	public int getDefaultPermissionLevel() {
		return 100;
	}

	@Override
	public void execute(Player player, String[] args) {
		if (!(main.INSTANCE.getCurrentGame() instanceof CTF))
			return;
		final CTF ctf = (CTF)main.INSTANCE.getCurrentGame();
		if (args.length == 0) {
			for (int i = 0; i < ctf.holders.size(); i++) {
				Team b = ctf.holders.get(ctf.holders.keySet().toArray()[i]);
				ctf.resetFlag(b);
			}
			ctf.holders.clear();
			player.sendMessage("All flags have been reset!");
		}
		else {
			for (int i = 0; i < ctf.holders.size(); i++) {
				Team b = ctf.holders.get(ctf.holders.keySet().toArray()[i]);
				if (b.system_name.indexOf(args[0]) != -1) {
					ctf.resetFlag(b);
					ctf.holders.remove(b);
					player.sendMessage(b.name + ChatColor.White + " flag has been reset!");
					break;
				}
			}
		}
	}

	@Override
	public void help(CommandExecutor executor) {
		// TODO Auto-generated method stub
		
	}

}
