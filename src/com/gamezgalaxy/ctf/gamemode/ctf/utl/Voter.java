package com.gamezgalaxy.ctf.gamemode.ctf.utl;

import java.util.ArrayList;
import java.util.Random;

import com.gamezgalaxy.GGS.API.EventHandler;
import com.gamezgalaxy.GGS.API.Priority;
import com.gamezgalaxy.GGS.API.Listener;
import com.gamezgalaxy.GGS.API.player.PlayerChatEvent;
import com.gamezgalaxy.GGS.chat.ChatColor;
import com.gamezgalaxy.GGS.iomodel.Player;
import com.gamezgalaxy.GGS.server.Server;
import com.gamezgalaxy.ctf.exceptions.VoteDidntStartException;
import com.gamezgalaxy.ctf.gamemode.Gamemode;
import com.gamezgalaxy.ctf.main.main;

public class Voter implements Listener {
	
	private Gamemode game;
	private int count;
	private Server server;
	private boolean started;
	private String[] cache;
	private ArrayList<MapData> maps = new ArrayList<MapData>();
	private ArrayList<Player> voters = new ArrayList<Player>();
	public Voter(Gamemode game, Server server) {
		this.game = game;
		this.server = server;
	}
	
	public void setMapCount(int count) {
		this.count = count;
	}
	
	private void pick() {
		final Random rand = new Random();
		for (int i = 0; i < count; i++) {
			MapData md = new MapData(game.getMain().maps.get(rand.nextInt(game.getMain().maps.size())));
			maps.add(md);
		}
	}
	
	private String[] generateLines() {
		if (cache == null) {
			ArrayList<String> lines = new ArrayList<String>();
			String finals = "";
			for (int i = 0; i < maps.size(); i++) {
				finals += "" + ChatColor.Bright_Green + (i + 1) + ". " + ChatColor.White + maps.get(i).getMap();
				if ((i + 1) % 3 == 0) {
					lines.add(finals);
					System.out.println(finals);
					finals = "";
				}
			}
			cache = lines.toArray(new String[lines.size()]);
		}
		return cache;
	}
	
	private void sendGlobalVoteMessage() {
		String[] lines = generateLines();
		main.GlobalMessage(ChatColor.Yellow + "------------------------------------------------------------");
		main.GlobalMessage(ChatColor.Dark_Green + "Time to vote! (Type the number/name of the map you want!");
		for (String line : lines) {
			main.GlobalMessage(line);
		}
		main.GlobalMessage(ChatColor.Yellow + "------------------------------------------------------------");
	}
	
	private void sendMapMessage(Player p) {
		String[] lines = generateLines();
		p.sendMessage(ChatColor.Dark_Green + "Time to vote! (Type the number/name of the map you want!");
		for (String line : lines) {
			p.sendMessage(line);
		}
	}
	
	private MapData findMap(String name) {
		for (MapData d : maps) {
			if (d.getMap().equalsIgnoreCase(name))
				return d;
		}
		return null;
	}
	
	public void start() {
		if (started)
			return;
		pick();
		sendGlobalVoteMessage();
		started = true;
		server.getEventSystem().registerEvents(this);
	}
	
	@EventHandler(priority = Priority.Low)
	public void onchat(PlayerChatEvent event) {
		if (voters.contains(event.getPlayer())) {
			event.getPlayer().sendMessage(ChatColor.Dark_Red + "You already voted!");
			event.Cancel(true);
			return;
		}
		try {
			int index = Integer.parseInt(event.getMessage());
			index--;
			if (index < 0 || index >= count) {
				event.getPlayer().sendMessage(ChatColor.Dark_Red + "Invalid map!");
				sendMapMessage(event.getPlayer());
				event.Cancel(true);
				return;
			}
			MapData d = maps.get(index);
			d.votes++;
			event.getPlayer().sendMessage("You voted for " + d.getMap());
			event.getPlayer().sendMessage(ChatColor.Bright_Green + "Thanks :D");
		}
		catch (Exception e) {
			MapData md = findMap(event.getMessage());
			if (md == null) {
				event.getPlayer().sendMessage(ChatColor.Dark_Red + "Invalid map!");
				sendMapMessage(event.getPlayer());
				event.Cancel(true);
				return;
			}
			md.votes++;
			event.getPlayer().sendMessage("You voted for " + event.getMessage());
			event.getPlayer().sendMessage(ChatColor.Bright_Green + "Thanks :D");
		}
		voters.add(event.getPlayer());
		event.Cancel(true);
	}
	
	public void end() throws VoteDidntStartException {
		if (!started)
			throw new VoteDidntStartException("You cant end a vote that hasnt started yet!");
		PlayerChatEvent.getEventList().unregister(this);
		started = false;
	}
	
	public String getResults() {
		MapData maxd = null;
		for (MapData d : maps) {
			if (maxd == null)
				maxd = d;
			else if (maxd.getVotes() < d.getVotes())
				maxd = d;
		}
		return maxd.getMap();
	}
	
	public void reset() {
		maps.clear();
		voters.clear();
	}
	
	private class MapData {
		private String map;
		
		private int votes;
		
		public MapData(String map) {
			this.setMap(map);
		}

		/**
		 * @return the map
		 */
		public String getMap() {
			return map;
		}

		/**
		 * @param map the map to set
		 */
		public void setMap(String map) {
			this.map = map;
		}

		/**
		 * @return the votes
		 */
		public int getVotes() {
			return votes;
		}
		
		
	}

}
