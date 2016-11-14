package com.sdklite.util;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents an introspector for Java Beans
 * 
 * @author johnsonlee
 *
 */
public abstract class Introspector {

    /**
     * Returns the properties of the specified bean
     * 
     * @param bean
     *            The object to introspect
     * @return the properties or it self if the specified bean is a map
     */
    public static Map<String, Object> properties(final Object bean) {
        return properties(bean, false);
    }

    /**
     * Returns the properties of the specified bean
     * 
     * @param bean
     *            The object to introspect
     * @param includeSuperClass
     *            The value indicates whether include properties of super class
     *            or not
     * @return the properties or it self if the specified bean is a map
     */
    public static Map<String, Object> properties(final Object bean, boolean includeSuperClass) {
        return Collections.unmodifiableMap(object2map(bean, includeSuperClass));
    }

    /**
     * Returns the value of the specified property of the specified bean
     * 
     * @param bean
     *            The object to introspect
     * @param name
     *            The property name
     * @return the value of the property
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProperty(final Object bean, final String name) throws Exception {
        final Method getter = getter(bean.getClass(), name);
        if (null == getter) {
            throw new NoSuchMethodException("Getter of " + name);
        }

        getter.setAccessible(true);
        return (T) getter.invoke(bean);
    }

    /**
     * Sets the value o fthe specified property of the specified bean
     * 
     * @param bean
     *            The object to introspect
     * @param name
     *            The property name
     * @param value
     *            The value of property
     * @throws Exception
     */
    public static void setProperty(final Object bean, final String name, final Object value) throws Exception {
        final Method setter = setter(bean.getClass(), name);
        if (null == setter) {
            throw new NoSuchMethodException("Setter of " + name);
        }

        setter.setAccessible(true);
        setter.invoke(bean, value);
    }

    /**
     * Returns the getter of the specified property
     * 
     * @param clazz
     *            The class of bean
     * @param name
     *            The property name
     * @return the getter or null if exception occurred
     */
    public static Method getter(final Class<?> clazz, final String name) {
        try {
            return clazz.getDeclaredMethod("get" + name.substring(0, 1).toUpperCase() + name.substring(1));
        } catch (final Exception e) {
            try {
                final Method getter = clazz
                        .getDeclaredMethod("is" + name.substring(0, 1).toUpperCase() + name.substring(1));
                final Type type = getter.getReturnType();
                if (boolean.class.equals(type) || Boolean.class.equals(type)) {
                    return getter;
                }
            } catch (final Exception ignore) {
            }

            return null;
        }
    }

    /**
     * Returns the setter of the specified property
     * 
     * @param clazz
     *            The class of bean
     * @param name
     *            The property name
     * @return the setter or null if exception occurred
     */
    public static Method setter(final Class<?> clazz, final String name) {
        try {
            final Method setter = clazz
                    .getDeclaredMethod("set" + name.substring(0, 1).toUpperCase() + name.substring(1));
            if (setter.getParameterTypes().length == 1) {
                return setter;
            }
        } catch (final Exception e) {
        }

        return null;
    }

    private static Map<String, Object> object2map(final Object bean, boolean includeSuperClass) {
        final Map<String, Object> map = new TreeMap<String, Object>();
        final Class<?> klass = bean.getClass();

        if (klass.getClassLoader() == null) {
            includeSuperClass = false;
        }

        final Method[] methods = (includeSuperClass) ? klass.getMethods() : klass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i += 1) {
            try {
                final Method method = methods[i];
                final String name = method.getName();

                String key = "";
                if (name.startsWith("get")) {
                    key = name.substring(3);
                } else if (name.startsWith("is")) {
                    key = name.substring(2);
                }

                if (key.length() > 0 && Character.isUpperCase(key.charAt(0))
                        && method.getParameterTypes().length == 0) {
                    if (key.length() == 1) {
                        key = key.toLowerCase();
                    } else if (!Character.isUpperCase(key.charAt(1))) {
                        key = key.substring(0, 1).toLowerCase() + key.substring(1);
                    }

                    final Object result = method.invoke(bean);

                    if (result == null) {
                        map.put(key, null);
                    } else if (result.getClass().isArray()) {
                        final List<Object> array = new ArrayList<Object>();
                        for (int j = 0, n = Array.getLength(result); j < n; j++) {
                            array.add(Array.get(result, j));
                        }
                        map.put(key, array);
                    } else if (result instanceof Collection) {
                        map.put(key, (Collection<?>) result);
                    } else if (result instanceof Map) {
                        map.put(key, (Map<?, ?>) result);
                    } else if (isStandardProperty(result.getClass())) {
                        map.put(key, result);
                    } else {
                        if (result.getClass().getPackage().getName().startsWith("java")
                                || result.getClass().getClassLoader() == null) {
                            map.put(key, result.toString());
                        } else {
                            map.put(key, object2map(result, includeSuperClass));
                        }
                    }
                }
            } catch (final Throwable cause) {
                throw new IllegalArgumentException(cause);
            }
        }

        return map;
    }

    private static boolean isStandardProperty(final Class<?> clazz) {
        return clazz.isPrimitive() || clazz.isAssignableFrom(Byte.class) || clazz.isAssignableFrom(Short.class)
                || clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(Long.class)
                || clazz.isAssignableFrom(Float.class) || clazz.isAssignableFrom(Double.class)
                || clazz.isAssignableFrom(Character.class) || clazz.isAssignableFrom(String.class)
                || clazz.isAssignableFrom(Boolean.class);
    }

    private Introspector() {
    }

}
