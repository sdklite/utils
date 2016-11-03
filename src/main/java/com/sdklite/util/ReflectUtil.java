package com.sdklite.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtil {

    public static Method getMethod(final Class<?> clazz, final String name, final Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(name, parameterTypes);
        } catch (final Exception e) {
            return null;
        }
    }

    public static Method getDeclaredMethod(final Class<?> clazz, final String name, final Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (final Exception e) {
            return null;
        }
    }
    
    public static <T> Constructor<T> getConstructor(final Class<T> clazz, final Class<?>... parameterTypes) {
        try {
            return clazz.getConstructor(parameterTypes);
        } catch (final Exception e) {
            return null;
        }
    }

    public static <T> Constructor<T> getDeclaredConstructor(final Class<T> clazz, final Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredConstructor(parameterTypes);
        } catch (final Exception e) {
            return null;
        }
    }

    public static Field getField(final Class<?> clazz, final String name) {
        try {
            return clazz.getField(name);
        } catch (final Exception e) {
            return null;
        }
    }

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
