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
import com.gamezgalaxy.GGS.world.Level;
import com.gamezgalaxy.ctf.gamemode.Gamemode;

public class Map {
	public Level level;
	public int redx;
	public int redy;
	public int redz;
	public int bluex;
	public int bluey;
	public int bluez;
	public String mapname;
	public SafeZone bzone = new SafeZone();
	public SafeZone rzone = new SafeZone();
	public ArrayList<Gamemode> games = new ArrayList<Gamemode>();
	
	public void load(String config) {
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
					else if (key.equals("flag.red.x"))
						redx = Integer.parseInt(value);
					else if (key.equals("flag.red.y"))
						redy = Integer.parseInt(value);
					else if (key.equals("flag.red.z"))
						redz = Integer.parseInt(value);
					else if (key.equals("flag.blue.x"))
						bluex = Integer.parseInt(value);
					else if (key.equals("flag.blue.y"))
						bluey = Integer.parseInt(value);
					else if (key.equals("flag.blue.z"))
						bluez = Integer.parseInt(value);
					else if (key.equals("blue.safezone.smallx"))
						bzone.setSmallX(Integer.parseInt(value));
					else if (key.equals("blue.safezone.bigx"))
						bzone.setBigX(Integer.parseInt(value));
					else if (key.equals("blue.safezone.smally"))
						bzone.setSmallY(Integer.parseInt(value));
					else if (key.equals("blue.safezone.bigy"))
						bzone.setBigY(Integer.parseInt(value));
					else if (key.equals("blue.safezone.smallz"))
						bzone.setSmallZ(Integer.parseInt(value));
					else if (key.equals("blue.safezone.bigz"))
						bzone.setBigZ(Integer.parseInt(value));
					else if (key.equals("red.safezone.smallx"))
						rzone.setSmallX(Integer.parseInt(value));
					else if (key.equals("red.safezone.bigx"))
						rzone.setBigX(Integer.parseInt(value));
					else if (key.equals("red.safezone.smally"))
						rzone.setSmallY(Integer.parseInt(value));
					else if (key.equals("red.safezone.bigy"))
						rzone.setBigY(Integer.parseInt(value));
					else if (key.equals("red.safezone.smallz"))
						rzone.setSmallZ(Integer.parseInt(value));
					else if (key.equals("red.safezone.bigz"))
						rzone.setBigZ(Integer.parseInt(value));
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
