package com.gamezgalaxy.ctf.commands.shop;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import net.mcforge.util.FileUtils;
import com.gamezgalaxy.ctf.main.main;
import com.gamezgalaxy.ctf.map.utl.JarLoader;

public class Shop {

	public final JarLoader JARLOADER = new JarLoader();
	
	public ArrayList<ShopItem> items = new ArrayList<ShopItem>();

	public void Load() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			FileUtils.createIfNotExist(FileUtils.PROPS_DIR, "shop.xml", DEFAULT_XML);
			DocumentBuilder db = dbf.newDocumentBuilder();
			org.w3c.dom.Document dom = db.parse(FileUtils.PROPS_DIR + "shop.xml");
			Element elm = dom.getDocumentElement();
			NodeList nl = elm.getElementsByTagName("Item");
			if (nl != null && nl.getLength() > 0) {
				for (int i = 0; i < nl.getLength(); i++) {
					Element e = (Element)nl.item(i);
					try {
						items.add(read(e));
					} catch (Exception ee) {
						ee.printStackTrace();
					}
				}
			}
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private ShopItem read(Element e) {
		try {
			String name = getTextValue(e, "name");
			int price = getIntValue(e, "price");
			int level = getIntValue(e, "level");
			String classpath = getTextValue(e, "class");
			String jar = "null";
			try {
				jar = e.getAttribute("jar");
			} catch (Exception ee) { }
			ShopItem item = null;
			if (jar.equals("null") || jar.equals("")) {
				Class<?> class_ = Class.forName(classpath);
				Class<? extends ShopItem> runClass = class_.asSubclass(ShopItem.class);
				Constructor<? extends ShopItem> constructor = runClass.getConstructor(Shop.class);
				item = constructor.newInstance(this);
			} else {
				main.INSTANCE.getServer().Log("Loading from " + jar + "..");
				item = JARLOADER.getObject(jar, classpath, ShopItem.class);
			}
			item.setLevel(level);
			item.setPrice(price);
			item.setName(name);
			main.INSTANCE.getServer().Log("Shop Item " + name + " " + (jar.equals("null") || jar.equals("") ? "CTF.jar" : jar) + "@" + classpath + " was loaded!");
			return item;
		} catch (Exception ee) {
			ee.printStackTrace();
			return null;
		}
	}

	/**
	 * I take a xml element and the tag name, look for the tag and get
	 * the text content
	 * i.e for <employee><name>John</name></employee> xml snippet if
	 * the Element points to employee node and tagName is 'name' I will return John
	 */
	private static String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}
	/**
	 * Calls getTextValue and returns a int value
	 */
	private static int getIntValue(Element ele, String tagName) {
		//in production application you would catch the exception
		return Integer.parseInt(getTextValue(ele,tagName));
	}
	
	private static final String DEFAULT_XML = "<!-- \n" +
			  "Copyright (c) 2012 GamezGalaxy.\n" +
			  "All rights reserved. This program and the accompanying materials\n" +
			  "are made available under the terms of the GNU Public License v3.0\n" +
			  "which accompanies this distribution, and is available at\n" +
			  "http://www.gnu.org/licenses/gpl.html\n" +
			"-->\n" +
			"<Shop>\n" +
			 " <Item>\n" +
			        "<name>Cloak</name>\n" +
			        "<price>300</price>\n" +
			        "<level>1</level>\n" +
			        "<class>com.gamezgalaxy.ctf.commands.shop.Cloak</class>\n" +
			  "</Item>\n" +
			  "<Item>\n" +
			        "<name>Jetpack</name>\n" +
			        "<price>500</price>\n" +
			        "<level>5</level>\n" +
			        "<class>com.gamezgalaxy.ctf.commands.shop.JetPack</class>\n" +
			  "</Item>\n" +
			  "<Item>\n" +
			        "<name>Disguise</name>\n" +
			        "<price>800</price>\n" +
			        "<level>7</level>\n" +
			        "<class>com.gamezgalaxy.ctf.commands.shop.Disguise</class>\n" +
			  "</Item>/n" +
			"</Shop>";
}
