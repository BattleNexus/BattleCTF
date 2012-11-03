/*******************************************************************************
 * Copyright (c) 2012 GamezGalaxy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.gamezgalaxy.ctf.map;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import net.mcforge.world.Block;
import net.mcforge.world.Level;
import com.gamezgalaxy.ctf.exceptions.InvalidConfigException;
import com.gamezgalaxy.ctf.gamemode.Gamemode;
import com.gamezgalaxy.ctf.gamemode.ctf.CTF;
import com.gamezgalaxy.ctf.gamemode.ctf.stalemate.Action;
import com.gamezgalaxy.ctf.gamemode.ctf.utl.Team;
import com.gamezgalaxy.ctf.map.utl.JarLoader;

public class Map {
	public Level level;
	public String mapname;
	public int teamcount;
	public ArrayList<Team> teams = new ArrayList<Team>();
	public ArrayList<Gamemode> games = new ArrayList<Gamemode>();
	public ArrayList<Action> stalemate = new ArrayList<Action>();
	public final JarLoader JARLOADER = new JarLoader();
	public void load(String config) throws InvalidConfigException {
		Team temp;
		for (int i = 0; i < 8; i++) {
			temp = new Team();
			temp.name = CTF.TEAM_NAMES[i];
			temp.system_name = CTF.SYSTEM_TEAM_NAME[i];
			teams.add(temp);
		}
		try {
			FileInputStream fstream = new FileInputStream(config);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			boolean validconfig = false;
			while ((strLine = br.readLine()) != null) {
				if (strLine.startsWith("#"))
					continue;
				if (strLine.equals("CONFIG.2.2"))
					validconfig = true;
				else if (strLine.equals("CONFIG.2.0"))
					throw new InvalidConfigException("The config \"" + config + "\" is invalid. It's config file is v2.0 and cant be converted!");
				else if (validconfig) {
					String key = strLine.split("\\=")[0].trim();
					String value = strLine.split("\\=")[1].trim();
					if (key.equals("core.gamemode")) {
						if (value.split("\\:").length == 2) {
							String jarfile = value.split("\\:")[0];
							String classpath = value.split("\\:")[1];
							Gamemode game = JARLOADER.getType(jarfile, classpath, Gamemode.class);
							games.add(game);
						}
						else {
							Class<?> class_ = Class.forName(value);
							Class<? extends Gamemode> runClass = class_.asSubclass(Gamemode.class);
							Constructor<? extends Gamemode> constructor = runClass.getConstructor();
							Gamemode game = constructor.newInstance();
							games.add(game);
						}
					}
					else if (key.equals("core.teamcount")) {
						teamcount = Integer.parseInt(value);
					}
					else if (key.split("\\.")[0].equalsIgnoreCase("flag")) {
						String team = key.split("\\.")[1];
						Team set = null;
						for (Team t : teams) {
							if (t.system_name.equalsIgnoreCase(team)) {
								set = t;
								break;
							}
						}
						if (set != null) {
							String type = key.split("\\.")[2];
							if (type.equals("x"))
								set.flagx = Integer.parseInt(value);
							else if (type.equals("y"))
								set.flagy = Integer.parseInt(value);
							else if (type.equals("z"))
								set.flagz = Integer.parseInt(value);
						}
					}
					else if (key.split("\\.")[0].equalsIgnoreCase("safezone")) {
						String team = key.split("\\.")[1];
						Team set = null;
						for (Team t : teams) {
							if (t.system_name.equalsIgnoreCase(team)) {
								set = t;
								break;
							}
						}
						if (set != null) {
							String type = key.split("\\.")[2];
							if (type.equals("smallx"))
								set.safe.setSmallX(Integer.parseInt(value));
							else if (type.equals("bigx"))
								set.safe.setBigX((Integer.parseInt(value)));
							else if (type.equals("smally"))
								set.safe.setSmallY((Integer.parseInt(value)));
							else if (type.equals("bigy"))
								set.safe.setBigY((Integer.parseInt(value)));
							else if (type.equals("smallz"))
								set.safe.setSmallZ((Integer.parseInt(value)));
							else if (type.equals("bigz"))
								set.safe.setBigZ((Integer.parseInt(value)));
						}
					}
					else if (key.split("\\.")[0].equals("spawn")) {
						String team = key.split("\\.")[1];
						Team set = null;
						for (Team t : teams) {
							if (t.system_name.equalsIgnoreCase(team)) {
								set = t;
								break;
							}
						}
						if (set != null) {
							String type = key.split("\\.")[2];
							if (type.equalsIgnoreCase("x"))
								set.setSpawnX(Integer.parseInt(value) * 32);
							if (type.equalsIgnoreCase("y"))
								set.setSpawnY(Integer.parseInt(value) * 32);
							if (type.equalsIgnoreCase("z"))
								set.setSpawnZ(Integer.parseInt(value) * 32);
						}
					}
					else if (key.split("\\.")[0].equalsIgnoreCase("area")) {
						String team = key.split("\\.")[1];
						Team set = null;
						for (Team t : teams) {
							if (t.system_name.equalsIgnoreCase(team)) {
								set = t;
								break;
							}
						}
						if (set != null) {
							int index = Integer.parseInt(key.split("\\.")[2]);
							String type = key.split("\\.")[3];
							if (set.area[index] == null)
								set.area[index] = new SafeZone();
							if (type.equals("smallx"))
								set.area[index].setSmallX(Integer.parseInt(value));
							else if (type.equals("bigx"))
								set.area[index].setBigX((Integer.parseInt(value)));
							else if (type.equals("smally"))
								set.area[index].setSmallY((Integer.parseInt(value)));
							else if (type.equals("bigy"))
								set.area[index].setBigY((Integer.parseInt(value)));
							else if (type.equals("smallz"))
								set.area[index].setSmallZ((Integer.parseInt(value)));
							else if (type.equals("bigz"))
								set.area[index].setBigZ((Integer.parseInt(value)));
						}
					}
					else if (key.split("\\.")[0].equalsIgnoreCase("block")) {
						String team = key.split("\\.")[1];
						Team set = null;
						for (Team t : teams) {
							if (t.system_name.equalsIgnoreCase(team)) {
								set = t;
								break;
							}
						}
						if (set != null) {
							if (value.split("\\:").length == 2) {
								String jarfile = value.split("\\:")[0];
								String classpath = value.split("\\:")[1];
								Block block = JARLOADER.getType(jarfile, classpath, Block.class);
								set.flagblock = block;
							}
							else {
								Class<?> class_ = Class.forName(value);
								Class<? extends Block> runClass = class_.asSubclass(Block.class);
								Constructor<? extends Block> constructor = runClass.getConstructor();
								set.flagblock = constructor.newInstance();
							}
						}
					}
					else if (key.split("\\.")[0].equalsIgnoreCase("stalemate.action")) {
						if (value.split("\\:").length == 2) {
							String jarfile = value.split("\\:")[0];
							String classpath = value.split("\\:")[1];
							Action action = JARLOADER.getType(jarfile, classpath, Action.class);
							this.stalemate.add(action);
						}
						else {
							Class<?> class_ = Class.forName(value);
							Class<? extends Action> runClass = class_.asSubclass(Action.class);
							Constructor<? extends Action> constructor = runClass.getConstructor();
							this.stalemate.add(constructor.newInstance());
						}
					}
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void load() {
		try {
			load("config/" + mapname + ".config");
		} catch (InvalidConfigException e) {
			e.printStackTrace();
		}
	}

}
