/*******************************************************************************
 * Copyright (c) 2012 GamezGalaxy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.battlenexus.classic.ctf.map.utl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import net.battlenexus.classic.ctf.main.main;
public class ConfigGraber {

	public static ArrayList<String> getMapList(String path) {
		return main.INSTANCE.maps;
	}
	public static String getMapName(int number, String path) {
		return getMapList(path).get(number - getMapmin(path));
	}
	public static int getMapCount(String path) {
		return getMapList(path).size();
	}
	public static int getMapBackupNumber(String name, String path) {
		return getMapList(path).indexOf(name) + getMapmin(path);
	}
	public static int getMapmin(String path) {
		try {
			Scanner scan = new Scanner(new File("config/maprange.config"));
			return Integer.parseInt(scan.nextLine());
		} catch(Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	public static int getMapmax(String path) {
		try {
			Scanner scan = new Scanner(new File("config/maprange.config"));
			scan.nextLine();
			return Integer.parseInt(scan.nextLine());
		} catch(Exception e) {
			return -1;
		}
	}
	public static void bump(String path) {
		final String mapmin = "" + getMapmin(path);
		final String mapmax = "" + (getMapmax(path) + 1);
		try {
			FileWriter outFile = new FileWriter("config/maprange.config");
			PrintWriter out = new PrintWriter(outFile);
			out.println(mapmin);
			out.println(mapmax);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void addMap(String map, ArrayList<String> config) {
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("config/maps.config", true)));
		    out.println(map);
		    out.close();
		    out = new PrintWriter(new BufferedWriter(new FileWriter("config/" + map + ".config")));
		    for (int i = 0; i < config.size(); i++) {
		    	out.println(config.get(i));
		    }
		    out.close();
		} catch (IOException e) {
		}
	}
	
	private static void createChildDirectories(String filepath) {
		String[] dirs = filepath.split("\\/"); 
		String path = "";
		for (String directory : dirs) {
			path += (path.equals("") ? directory : "/" + directory);
			if (directory.indexOf(".") == -1 && !new File(path).exists())
				new File(path).mkdir();
		}
	}
	
	public static void copyfile(String srFile, String dtFile){
		try{
			createChildDirectories(dtFile);
			File f1 = new File(srFile);
			File f2 = new File(dtFile);
			InputStream in = new FileInputStream(f1);

			//For Append the file.
			//  OutputStream out = new FileOutputStream(f2,true);

			//For Overwrite the file.
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0){
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			System.out.println("File copied.");
		}
		catch(FileNotFoundException ex){
			System.out.println(ex.getMessage() + " in the specified directory.");
		}
		catch(IOException e){
			System.out.println(e.getMessage());  
		}
	}
}
