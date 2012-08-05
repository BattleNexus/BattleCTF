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

import com.gamezgalaxy.GGS.world.Block;
import com.gamezgalaxy.GGS.world.Level;
import com.gamezgalaxy.ctf.gamemode.Gamemode;
import com.gamezgalaxy.ctf.gamemode.ctf.CTF;
import com.gamezgalaxy.ctf.gamemode.ctf.utl.Team;

public class Map {
	public Level level;
	public String mapname;
	public int teamcount;
	public ArrayList<Team> teams = new ArrayList<Team>();
	public ArrayList<Gamemode> games = new ArrayList<Gamemode>();
	
	public void load(String config) {
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
				else if (strLine.equals("CONFIG.2.0")) {
					//TODO Read and Convert
					break;
				}
				else if (validconfig) {
					String key = strLine.split("\\=")[0].trim();
					String value = strLine.split("\\=")[1].trim();
					if (key.equals("core.gamemode")) {
						Class<?> class_ = Class.forName(value);
						Class<? extends Gamemode> runClass = class_.asSubclass(Gamemode.class);
						Constructor<? extends Gamemode> constructor = runClass.getConstructor();
						Gamemode game = constructor.newInstance();
						games.add(game);
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
							String type = key.split("\\.")[2];
							if (type.equals("smallx"))
								set.area.setSmallX(Integer.parseInt(value));
							else if (type.equals("bigx"))
								set.area.setBigX((Integer.parseInt(value)));
							else if (type.equals("smally"))
								set.area.setSmallY((Integer.parseInt(value)));
							else if (type.equals("bigy"))
								set.area.setBigY((Integer.parseInt(value)));
							else if (type.equals("smallz"))
								set.area.setSmallZ((Integer.parseInt(value)));
							else if (type.equals("bigz"))
								set.area.setBigZ((Integer.parseInt(value)));
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
							Class<?> class_ = Class.forName(value);
							Class<? extends Block> runClass = class_.asSubclass(Block.class);
							Constructor<? extends Block> constructor = runClass.getConstructor();
							set.flagblock = constructor.newInstance();
						}
					}
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void load() {
		load("config/" + mapname + ".config");
	}

}
