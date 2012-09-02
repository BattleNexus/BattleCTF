/*******************************************************************************
 * Copyright (c) 2012 GamezGalaxy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.gamezgalaxy.ctf.main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import com.gamezgalaxy.GGS.API.plugin.Game;
import com.gamezgalaxy.GGS.chat.Messages;
import com.gamezgalaxy.GGS.iomodel.Player;
import com.gamezgalaxy.GGS.server.Server;
import com.gamezgalaxy.ctf.commands.*;
import com.gamezgalaxy.ctf.commands.shop.Shop;
import com.gamezgalaxy.ctf.events.EventListener;
import com.gamezgalaxy.ctf.gamemode.Gamemode;
import com.gamezgalaxy.ctf.map.Map;

public class main extends Game {

	Messages globalchat;
	int tick = 30000;
	Gamemode gm;
	Thread run;
	boolean running;
	EventListener events;
	public ArrayList<String> maps = new ArrayList<String>();
	public static main INSTANCE;
	public static final Random random = new Random();
	private Shop _shop;
	boolean ctfmap = true;
	private String welcome;
	private int index;
	public main(Server server) {
		super(server);
		globalchat = new Messages(server);
	}
	
	public EventListener getEvents() {
		return events;
	}
	
	public void setNextMap(String value) {
		if (maps.contains(value)) {
			index = maps.indexOf(value);
			System.out.println("Next map will be " + value + " (ID: " + index + ")");
		}
	}

	@Override
	public void onLoad(String[] arg0) {
		INSTANCE = this;
		if (getServer().getLevelHandler().findLevel("ctf") != null)
			getServer().getLevelHandler().unloadLevel(getServer().getLevelHandler().findLevel("ctf"), false);
		if (getServer().getLevelHandler().findLevel("ctf2") != null)
			getServer().getLevelHandler().unloadLevel(getServer().getLevelHandler().findLevel("ctf2"), false);
		try {
			loadMaps();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		events = new EventListener();
		getServer().getEventSystem().registerEvents(events);
		run = new Gametick(this);
		running = true;
		run.start();
		_shop = new Shop();
		_shop.Load();
		getServer().getCommandHandler().addCommand(new CmdPoints());
		getServer().getCommandHandler().addCommand(new CmdShop());
		getServer().getCommandHandler().addCommand(new CmdFlagReset());
		welcome = getServer().getSystemProperties().getValue("welcome-message");
		if (welcome.equals("null"))
			getServer().getSystemProperties().addSetting("welcome-message", "Welcome to my CTF server!");
	}
	
	public Gamemode getCurrentGame() {
		return gm;
	}
	
	public Shop getShop() {
		return _shop;
	}
	
	public void loadMaps() throws IOException {
		FileInputStream fstream = new FileInputStream("config/maps.config");
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		while ((strLine = br.readLine()) != null) {
			maps.add(strLine);
		}
		in.close();
	}

	@Override
	public void onUnload() {
		running = false;
		try {
			run.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void Tick() {
		if (gm == null)
			return;
		if (gm.isRunning())
			gm.tick();
		if (tick <= 0) {
			GlobalMessage("Visit &2www.gamezgalaxy.com");
			tick = 30000;
		}
		else
			tick--;
	}
	
	public static void GlobalMessage(String message) {
		INSTANCE.globalchat.serverBroadcast(message);
	}
	public static void GlobalClear() {
		for (Player p : INSTANCE.getServer().players)
			p.clearChatScreen();
	}
	public static String secondsToTime(int sec) {
		int min = 0;
		while (sec >= 60) { sec -= 60; min++; }
		return min + ":" + (sec < 10 ? "0" + sec : sec);
	}
	private class Gametick extends Thread {
		
		Game game;
		
		public Gametick(Game game) { this.game = game; }
		@Override
		public void run() {
			Map m = null;
			while (running) {
				m = new Map();
				String map = maps.get(random.nextInt(maps.size()));
				if (index != -1) {
					map = maps.get(index);
					index = -1;
				}
				m.mapname = map;
				getServer().Log("Loading " + map + " config!");
				m.load();
				getServer().Log("Done!");
				gm = m.games.get(random.nextInt(m.games.size()));
				try {
					getServer().Log("Setting up...");
					gm.parent = game;
					gm.ctfmap = ctfmap;
					gm.setup(m);
					getServer().Log("Done!");
				} catch (Exception e1) {
					e1.printStackTrace();
					continue;
				}
				getServer().Log("Starting round..");
				gm.roundStart();
				getServer().Log("Waiting for end..");
				try {
					gm.waitForEnd();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ctfmap = !ctfmap;
				m = null;
			}
		}
	}

}
