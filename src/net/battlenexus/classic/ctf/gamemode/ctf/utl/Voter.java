package net.battlenexus.classic.ctf.gamemode.ctf.utl;

import java.util.ArrayList;
import java.util.Random;

import net.mcforge.API.EventHandler;
import net.mcforge.API.Priority;
import net.mcforge.API.Listener;
import net.mcforge.API.player.PlayerChatEvent;
import net.mcforge.chat.ChatColor;
import net.mcforge.iomodel.Player;
import net.mcforge.server.Server;
import net.battlenexus.classic.ctf.exceptions.VoteDidntStartException;
import net.battlenexus.classic.ctf.gamemode.Gamemode;
import net.battlenexus.classic.ctf.main.main;

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
				finals += "" + ChatColor.Bright_Green + (i + 1) + ". " + ChatColor.White + maps.get(i).getMap() + " ";
				if ((i + 1) % 3 == 0) {
					lines.add(finals);
					System.out.println(finals);
					finals = "";
				}
			}
			if (!finals.equals(""))
				lines.add(finals);
			cache = lines.toArray(new String[lines.size()]);
		}
		return cache;
	}
	
	private void sendGlobalVoteMessage() {
		String[] lines = generateLines();
		main.GlobalMessage(ChatColor.Yellow + "------------------------------------------------------------");
		main.GlobalMessage(ChatColor.Dark_Green + "Time to vote! (Type the number/name of the map you want!)");
		for (String line : lines) {
			main.GlobalMessage(line);
		}
		main.GlobalMessage(ChatColor.Yellow + "------------------------------------------------------------");
	}
	
	private void sendMapMessage(Player p) {
		String[] lines = generateLines();
		p.sendMessage(ChatColor.Dark_Green + "Time to vote! (Type the number/name of the map you want!)");
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
			event.setCancel(true);
			return;
		}
		try {
			int index = Integer.parseInt(event.getMessage());
			index--;
			if (index < 0 || index >= count) {
				event.getPlayer().sendMessage(ChatColor.Dark_Red + "Invalid map!");
				sendMapMessage(event.getPlayer());
				event.setCancel(true);
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
				event.setCancel(true);
				return;
			}
			md.votes++;
			event.getPlayer().sendMessage("You voted for " + event.getMessage());
			event.getPlayer().sendMessage(ChatColor.Bright_Green + "Thanks :D");
		}
		voters.add(event.getPlayer());
		event.setCancel(true);
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
