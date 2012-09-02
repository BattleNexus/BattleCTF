package com.gamezgalaxy.ctf.gamemode.ctf.utl;

import java.util.ArrayList;
import java.util.HashMap;
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
	private Server server;
	private String map1;
	private String map2;
	private String map3;
	private boolean started;
	private HashMap<String, Integer> votes = new HashMap<String, Integer>();
	private ArrayList<Player> voters = new ArrayList<Player>();
	public Voter(Gamemode game, Server server) {
		this.game = game;
		this.server = server;
	}
	
	public void start() {
		if (started)
			return;
		//Pick 3 random maps
		Random rand = new Random();
		map1 = game.getMain().maps.get(rand.nextInt(game.getMain().maps.size()));
		map2 = game.getMain().maps.get(rand.nextInt(game.getMain().maps.size()));
		map3 = game.getMain().maps.get(rand.nextInt(game.getMain().maps.size()));
		votes.put(map1, 0);
		votes.put(map2, 0);
		votes.put(map3, 0);
		main.GlobalMessage(ChatColor.Yellow + "------------------------------------------------------------");
		main.GlobalMessage(ChatColor.Dark_Green + "Time to vote! (Type the number/name of the map you want!");
		main.GlobalMessage(ChatColor.Bright_Green + "1. " + map1 + ChatColor.Bright_Green + " 2. " + map2 + ChatColor.Bright_Green + " 3. " + map3);
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
			if (index < 0 || index >= 3) {
				event.getPlayer().sendMessage(ChatColor.Dark_Red + "Invalid map!");
				event.getPlayer().sendMessage(ChatColor.Bright_Green + "1. " + map1 + ChatColor.Bright_Green + " 2. " + map2 + ChatColor.Bright_Green + " 3. " + map3);
				event.Cancel(true);
				return;
			}
			String map = votes.keySet().toArray(new String[3])[index - 1];
			int value = votes.get(map).intValue();
			value++;
			votes.remove(map);
			votes.put(map, value);
			event.getPlayer().sendMessage("You voted for " + map);
			event.getPlayer().sendMessage(ChatColor.Bright_Green + "Thanks :D");
		}
		catch (Exception e) {
			if (!votes.containsKey(event.getMessage())) {
				event.getPlayer().sendMessage(ChatColor.Dark_Red + "Invalid map!");
				event.getPlayer().sendMessage(ChatColor.Bright_Green + "1. " + map1 + ChatColor.Bright_Green + " 2. " + map2 + ChatColor.Bright_Green + " 3. " + map3);
				event.Cancel(true);
				return;
			}
			int value = votes.get(event.getMessage()).intValue();
			value++;
			votes.remove(event.getMessage());
			votes.put(event.getMessage(), value);
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
		if (votes.get(map1) > votes.get(map2) && votes.get(map1) > votes.get(map3))
			return map1;
		else if (votes.get(map2) > votes.get(map1) && votes.get(map2) > votes.get(map3))
			return map2;
		else if (votes.get(map3) > votes.get(map1) && votes.get(map3) > votes.get(map2))
			return map3;
		else
			return map1;
	}
	
	public void reset() {
		votes.clear();
		voters.clear();
		map1 = "";
		map2 = "";
		map3 = "";
	}

}
