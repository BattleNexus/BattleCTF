package com.gamezgalaxy.ctf.map.utl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
public class ConfigGraber {

	public static ArrayList<String> getMapList(String path) {
		ArrayList<String> maps = new ArrayList<String>();
		try {
			FileInputStream fstream = new FileInputStream("config/maps.config");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				maps.add(strLine);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return maps;
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
			Scanner scan = new Scanner(path);
			return Integer.parseInt(scan.nextLine());
		} catch(Exception e) {
			return -1;
		}
	}
	public static int getMapmax(String path) {
		try {
			Scanner scan = new Scanner(path);
			scan.nextLine();
			return Integer.parseInt(scan.nextLine());
		} catch(Exception e) {
			return -1;
		}
	}
}
