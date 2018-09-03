package eu.ha3.mc.haddon.implem;

import java.util.logging.Logger;

@Deprecated
public class HaddonUtilitySingleton {
	final static public Logger LOGGER = Logger.getLogger("HaddonUtilitySingleton");
	private static final HaddonUtilitySingleton instance = new HaddonUtilitySingleton();

	private HaddonUtilitySingleton() { }

	public static HaddonUtilitySingleton getInstance() {
		return instance;
	}
}
