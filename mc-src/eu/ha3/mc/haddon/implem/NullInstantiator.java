package eu.ha3.mc.haddon.implem;

import java.lang.reflect.Constructor;

public final class NullInstantiator implements Instantiator<Object> {

    @Override
	public Object instantiate(Object... pars) {
		return null;
	}

	@SuppressWarnings("unchecked")
    public <E> Class<E> lookupClass(String className) {
		try {
			return (Class<E>)Class.forName(className, false, NullInstantiator.class.getClassLoader());
		} catch (ClassNotFoundException e) {

		}

		return null;
	}

	@SuppressWarnings("unchecked")
    public <E> Instantiator<E> getOrCreate(String className, Class<?>... types) {
		try {
            Class<E> c = lookupClass(className);

			if (c != null) {
				Constructor<E> ctor = c.getDeclaredConstructor(types);

				if (ctor != null) {
				    ctor.setAccessible(true);
					return pars -> {
					    try {
                            return (E) ctor.newInstance(pars);
                        } catch (Exception e) {
                            return null;
                        }
				    };
				}
			}
		} catch (Exception e) {

		}

		return (Instantiator<E>)this;
	}
}
