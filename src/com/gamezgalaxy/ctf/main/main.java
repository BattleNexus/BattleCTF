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
	public main(Server server) {
		super(server);
		globalchat = new Messages(server);
	}

	@Override
	public void onLoad(String[] arg0) {
		INSTANCE = this;
		try {
			loadMaps();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		events = new EventListener();
		getServer().getEventSystem().registerEvents(events);
		run = new Game();
		running = true;
		run.start();
		_shop = new Shop();
		_shop.Load();
		getServer().getCommandHandler().addCommand(new CmdPoints());
		getServer().getCommandHandler().addCommand(new CmdShop());
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
	public void Start() {
	}

	@Override
	public void Stop() {
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
	public class Game extends Thread {
		
		@Override
		public void run() {
			Map m = new Map();
			while (running) {
				String map = maps.get(random.nextInt(maps.size()));
				m.mapname = map;
				getServer().Log("Loading " + map + " config!");
				m.load();
				getServer().Log("Done!");
				gm = m.games.get(random.nextInt(m.games.size()));
				try {
					getServer().Log("Setting up...");
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
			}
		}
	}

}
