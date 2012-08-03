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
	public int lavax;
	public int lavay;
	public int lavaz;
	public int minx;
	public int maxx;
	public int minz;
	public int maxz;
	public int mapheight;
	public String mapname;
	public SafeZone zone = new SafeZone();
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
					else if (key.equals("x.spawn"))
						lavax = Integer.parseInt(value);
					else if (key.equals("y.spawn"))
						lavay = Integer.parseInt(value);
					else if (key.equals("z.spawn"))
						lavaz = Integer.parseInt(value);
					else if (key.equals("map.height"))
						mapheight = Integer.parseInt(value);
					else if (key.equals("zombie.spawn.smallx"))
						minx = Integer.parseInt(value);
					else if (key.equals("zombie.spawn.higherx"))
						maxx = Integer.parseInt(value);
					else if (key.equals("zombie.spawn.lowerz"))
						minz = Integer.parseInt(value);
					else if (key.equals("zombie.spawn.higherz"))
						maxz = Integer.parseInt(value);
					else if (key.equals("safezone.smallx"))
						zone.setSmallX(Integer.parseInt(value));
					else if (key.equals("safezone.bigx"))
						zone.setBigX(Integer.parseInt(value));
					else if (key.equals("safezone.smally"))
						zone.setSmallY(Integer.parseInt(value));
					else if (key.equals("safezone.bigy"))
						zone.setBigY(Integer.parseInt(value));
					else if (key.equals("safezone.smallz"))
						zone.setSmallZ(Integer.parseInt(value));
					else if (key.equals("safezone.bigz"))
						zone.setBigZ(Integer.parseInt(value));
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
