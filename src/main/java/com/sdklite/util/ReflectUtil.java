package com.sdklite.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utility for reflective operation
 * 
 * @author johnsonlee
 *
 */
public class ReflectUtil {

    /**
     * Returns the matched public method of the specified class or null if no
     * such method
     * 
     * @param clazz
     *            The represented class
     * @param name
     *            The name of method
     * @param parameterTypes
     *            The parameter types of method
     * @return the matched method or null if not found
     */
    public static Method getMethod(final Class<?> clazz, final String name, final Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(name, parameterTypes);
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * Returns the declared method of the specified class or null if no such
     * method
     * 
     * @param clazz
     *            The represented class
     * @param name
     *            The name of method
     * @param parameterTypes
     *            The parameter types of method
     * @return the matched method or null if not found
     */
    public static Method getDeclaredMethod(final Class<?> clazz, final String name, final Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * Returns the public constructor of the specified class or null if no such
     * constructor
     * 
     * @param clazz
     *            The represented class
     * @param parameterTypes
     *            The parameter types of constructor
     * @return the matched constructor or null if not found
     */
    public static <T> Constructor<T> getConstructor(final Class<T> clazz, final Class<?>... parameterTypes) {
        try {
            return clazz.getConstructor(parameterTypes);
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * Returns the declared constructor of the specified class or null if no
     * such constructor
     * 
     * @param clazz
     *            The represented class
     * @param parameterTypes
     *            The parameter types of constructor
     * @return the matched constructor or null if not found
     */
    public static <T> Constructor<T> getDeclaredConstructor(final Class<T> clazz, final Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredConstructor(parameterTypes);
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * Returns the public field of the specified class or null if no such field
     * 
     * @param clazz
     *            The represented class
     * @param name
     *            the name of field
     * @return the matched field or null if not found
     */
    public static Field getField(final Class<?> clazz, final String name) {
        try {
            return clazz.getField(name);
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * Returns the declared field of the specified class or null if no such
     * field
     * 
     * @param clazz
     *            The represented class
     * @param name
     *            The name of field
     * @return the matched field or null if not found
     */
    public static Field getDeclaredField(final Class<?> clazz, final String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (final Exception e) {
            return null;
        }
    }

    private ReflectUtil() {
    }
}
