package com.gamezgalaxy.ctf.commands;

import com.gamezgalaxy.ctf.main.main;

import net.mcforge.API.CommandExecutor;
import net.mcforge.API.plugin.Command;

public class CmdForceEnd extends Command {

	@Override
	public void execute(CommandExecutor arg0, String[] arg1) {
		new Thread() {
			@Override
			public void run() {
				main.INSTANCE.getCurrentGame().roundEnd();
			}
		}.start();
	}

	@Override
	public int getDefaultPermissionLevel() {
		return 100;
	}

	@Override
	public String getName() {
		return "forceend";
	}

	@Override
	public String[] getShortcuts() {
		return new String[] { "fe" };
	}

	@Override
	public void help(CommandExecutor arg0) {
		arg0.sendMessage("/fe - Force end the round!");
	}

	@Override
	public boolean isOpCommandDefault() {
		return true;
	}
}
