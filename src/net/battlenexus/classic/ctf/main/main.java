/*******************************************************************************
 * Copyright (c) 2012 GamezGalaxy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.battlenexus.classic.ctf.main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import net.mcforge.API.plugin.Plugin;
import net.mcforge.chat.Messages;
import net.mcforge.iomodel.Player;
import net.mcforge.server.Server;
import net.mcforge.system.ticker.Tick;
import net.mcforge.system.updater.Updatable;
import net.mcforge.system.updater.UpdateType;

import net.battlenexus.classic.ctf.commands.*;
import net.battlenexus.classic.ctf.commands.shop.Shop;
import net.battlenexus.classic.ctf.events.EventListener;
import net.battlenexus.classic.ctf.gamemode.Gamemode;
import net.battlenexus.classic.ctf.map.Map;

public class main extends Plugin implements Updatable, Tick {

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
	@SuppressWarnings("unused")
	private String welcome;
	private int index = -1;
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
		if (new File("levels/ctf.ggs").exists())
			new File("levels/ctf.ggs").delete();
		if (new File("levels/ctf2.ggs").exists())
			new File("levels/ctf2.ggs").delete();
		INSTANCE = this;
		events = new EventListener();
		getServer().getEventSystem().registerEvents(events);
		_shop = new Shop();
		_shop.Load();
		getServer().getCommandHandler().addCommand(new CmdPoints());
		getServer().getCommandHandler().addCommand(new CmdShop());
		getServer().getCommandHandler().addCommand(new CmdFlagReset());
		getServer().getCommandHandler().addCommand(new CmdSetup());
	}
	
	public void start() {
		if (running)
			return;
		try {
			loadMaps();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (maps.size() == 0) {
			getServer().Log("[CTF] No maps found..");
			getServer().Log("[CTF] Will start the round when one is added.");
			getServer().Log("[CTF] Add a map using /setup !");
			return;
		}
		String map = maps.get(random.nextInt(maps.size()));
		setNextMap(map);
		run = new Gametick(this);
		running = true;
		run.start();
		getServer().getTicker().addTick(this);
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
		getServer().getTicker().removeTick(this);
	}

	@Override
	public void tick() {
		if (gm == null)
			return;
		if (gm.isRunning())
			gm.tick();
		GlobalMessage("Visit &2www.gamezgalaxy.com");
	}
	
	public static void GlobalMessage(String message) {
		INSTANCE.globalchat.serverBroadcast(message);
	}
	public static void GlobalClear() {
		for (Player p : INSTANCE.getServer().getPlayers())
			p.clearChatScreen();
	}
	public static String secondsToTime(int sec) {
		int min = 0;
		while (sec >= 60) { sec -= 60; min++; }
		return min + ":" + (sec < 10 ? "0" + sec : sec);
	}
	private class Gametick extends Thread {
		
		Plugin game;
		
		public Gametick(Plugin game) { this.game = game; }
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
	@Override
	public String getCheckURL() {
		return "http://www.gamezgalaxy.com/assets/ctf/version.txt";
	}

	@Override
	public String getCurrentVersion() {
		return "1.0.0";
	}

	@Override
	public String getDownloadPath() {
		return "plugins/CTF.jar";
	}

	@Override
	public String getDownloadURL() {
		return "http://www.gamezgalaxy.com/assets/ctf/CTF.jar";
	}

	@Override
	public UpdateType getUpdateType() {
		return UpdateType.Manual;
	}

	@Override
	public void unload() {
		getServer().getPluginHandler().unload(this);
	}

	@Override
	public boolean inSeperateThread() {
		return false;
	}

	@Override
	public int getTimeout() {
		return 30000;
	}

}
