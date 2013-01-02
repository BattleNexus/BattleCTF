package net.battlenexus.classic.ctf.map.utl;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;

public class JarLoader {
	
	@SuppressWarnings("deprecation")
	public <T> T getObject(String file, String classpath, Class<? extends T> type, Object...parma) {
		try {
			File f = new File(file);
			if (!f.exists())
				return null;
			URL[] urls = new URL[] { f.toURL() };
			ClassLoader loader = URLClassLoader.newInstance(urls, getClass().getClassLoader());
			Class<?> class_ = Class.forName(classpath, true, loader);
			Class<? extends T> runclass = class_.asSubclass(type);
			Class<?>[] constructparma = new Class<?>[parma.length];
			for (int i = 0; i < parma.length; i++) {
				constructparma[i] = parma[i].getClass();
			}
			Constructor<? extends T> construct = runclass.getConstructor(constructparma);
			T toreturn = construct.newInstance(parma);
			return toreturn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
