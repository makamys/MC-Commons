package eu.ha3.mc.haddon.implem;

@FunctionalInterface
public interface Instantiator<E> {
	/**
	 * Creates a new instance of an underlying class. Returns null in all cases if it fails.
	 */
	E instantiate(Object... pars);
}
