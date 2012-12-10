package com.gamezgalaxy.ctf.commands.shop;

import java.io.IOException;
import java.io.NotSerializableException;
import java.sql.SQLException;

import net.mcforge.API.CommandExecutor;
import net.mcforge.chat.ChatColor;
import net.mcforge.iomodel.Player;

public class Mine extends ShopItem {

	@Override
	public String getShopName() {
		return "mine";
	}

	@Override
	public boolean run(Player p) {
		if (p.hasValue("mine")) {
			int value = p.getValue("mine");
			value++;
			p.setValue("mine", value);
		}
		else
			p.setValue("mine", 1);
		try {
			p.saveValue("mine");
		} catch (NotSerializableException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		p.sendMessage(ChatColor.Bright_Green + "+" + ChatColor.White + " You bought 1 mine!");
		p.sendMessage("You have " + p.getValue("mine") + " mine(s)");
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
