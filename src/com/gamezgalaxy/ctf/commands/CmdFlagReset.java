package com.gamezgalaxy.ctf.commands;

import net.mcforge.API.CommandExecutor;
import net.mcforge.API.ManualLoad;
import net.mcforge.API.plugin.PlayerCommand;
import net.mcforge.chat.ChatColor;
import net.mcforge.iomodel.Player;
import com.gamezgalaxy.ctf.gamemode.ctf.CTF;
import com.gamezgalaxy.ctf.gamemode.ctf.utl.Team;
import com.gamezgalaxy.ctf.main.main;

@ManualLoad
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
	public boolean isOpCommandDefault() {
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
