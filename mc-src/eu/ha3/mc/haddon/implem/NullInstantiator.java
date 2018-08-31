package eu.ha3.mc.haddon.implem;

import java.lang.reflect.Constructor;

public final class NullInstantiator implements Instantiator<Object> {
	public Object instantiate(Object... pars) {
		return null;
	}
	
	public Class<?> lookupClass(String className) {
		try {
			return Class.forName(className, false, NullInstantiator.class.getClassLoader());
		} catch (ClassNotFoundException e) { }
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <E> Instantiator<E> getOrCreate(String className, Class<?>... types) {
		try {
			Class<E> c = (Class<E>)lookupClass(className);
			if (c != null) {
				final Constructor<E> ctor = c.getDeclaredConstructor((Class[])types);
				if (ctor != null) {
				ctor.setAccessible(true);
					return new Instantiator<E>() {
						public E instantiate(Object... pars) {
							try {
								return (E) ctor.newInstance(pars);
							} catch (Exception e) { }
							return null;
						}
					};
				}
			}
		} catch (Exception e) { }
		return (Instantiator<E>)this;
	}
}
